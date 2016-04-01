package me.valour.sugaroverflow.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.android.volley.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import me.valour.sugaroverflow.activity.MainActivity;
import me.valour.sugaroverflow.model.Question;

public class GetQuestionService extends Service{

    public final static String UPDATED_ENTERIES ="updated_entries";

    private final IBinder binder = new Glue();
    private final int pageSize = 25;
    private final int maxPages = 4;
    private long lastFetched = 0;

    private boolean hasMoreEntries = true;

    private Timer newEntriesTimer = null;
    private ArrayList<Question> entries;
    private InitialEntriesResponse initResponse;
    private NewEntriesResponse newEntriesResponse;

    public GetQuestionService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        entries = new ArrayList<Question>();
        initResponse = new InitialEntriesResponse();
        newEntriesResponse = new NewEntriesResponse();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        queueUpRequests();
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * We are going to divide our request to StackExchange
     * 100 entries might take too long,
     * so we will divide it up and place them into the queue
     */
    public void queueUpRequests(){

        for(int page=1; page <= maxPages; page++){
            if(!hasMoreEntries){
                StackExchangeAPI.getInstance(this).clearQueue(StackExchangeAPI.STARTER);
                break;
            }
            StackExchangeAPI.getInstance(this).listAndroidQuestions(pageSize, page, initResponse);
        }
    }

    /**
     * After the MainActivity of the app stops,
     * we want to keep this service alive and check for updates.
     * If there are new entries then we want to send a notifcation
     *
     * Check every 15 minutes
     */
    public void startRecurringFetch(){
        lastFetched = System.currentTimeMillis();

        final Context ctx = this;
        newEntriesTimer = new Timer();
        newEntriesTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
               StackExchangeAPI.getInstance(ctx).getAndroidQuestionsAfter(lastFetched, newEntriesResponse);
            }
        }, 0, TimeUnit.MINUTES.toMillis(15));

    }

    /**
     * If the App is active, we want to stop checking for updates
     */
    public void stopRecurringFetch(){
        if(newEntriesTimer!=null) {
            newEntriesTimer.cancel();
            newEntriesTimer = null;
        }
    }

    /**
     * Launch notifiction
     * @param newEntries Number of new entries found since last check
     */
    private void sendNotification(int newEntries){

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(android.R.drawable.stat_notify_chat)
                .setContentTitle("SugarOverflow got more questions!")
                .setContentText(newEntries+" new entries retrieved");

        Intent resultIntent = new Intent(this, MainActivity.class);

        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        this,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        builder.setContentIntent(resultPendingIntent);

        NotificationManager mNotifyMgr =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mNotifyMgr.notify(1999, builder.build());

    }

    /**
     * Get new entries stored in temp array list
     * @return Array list of new entries found
     */
    public ArrayList<Question> getNewEntries(){
        return entries;
    }

    /**
     * Empty temp array list of entries
     */
    public void clearEntries(){
        entries.clear();
    }

    /**
     * Binder to connect this serivce to an activity
     * allowing the activity to access public methods of GetQuestionService
     */
    public class Glue extends Binder {
        public GetQuestionService getService() {
            return GetQuestionService.this;
        }
    }

    /**
     * ResponseHandler for initial retrieval of 100 entries
     */
    class InitialEntriesResponse implements Response.Listener<JSONObject> {
        @Override
        public void onResponse(JSONObject jsonObject) {
            try {
                JSONArray items = jsonObject.getJSONArray("items");
                int itemsCount = items.length();
                for(int i=0; i<itemsCount; i++){
                    Question quest = new Question(items.getJSONObject(i));
                    entries.add(quest);
                }
                if(itemsCount>0){
                    sendBroadcast(new Intent(UPDATED_ENTERIES));
                }
                hasMoreEntries = jsonObject.getBoolean("has_more");

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    /**
     * ResponseHandler of the request tht checks for new entries
     */
    class NewEntriesResponse implements Response.Listener<JSONObject> {
        @Override
        public void onResponse(JSONObject jsonObject) {
            try {
                JSONArray items = jsonObject.getJSONArray("items");

                int itemsCount = items.length();

                if(itemsCount>0){
                    sendNotification(itemsCount);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            lastFetched = System.currentTimeMillis();

        }
    }

}
