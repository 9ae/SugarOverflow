package me.valour.sugaroverflow.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
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
import me.valour.sugaroverflow.orm.AccessDB;

public class GetQuestionService extends Service{

    public final static String UPDATED_ENTERIES ="updated_entries";

    private final int pageSize = 1; //TODO: change back to 25
    private final int maxPages = 4;
    private long lastFetched = 0;
    private int entriesRetrieved = 0;

    private boolean hasMoreEntries = true;

    private InitialEntriesResponse initResponse;
    private NewEntriesResponse newEntriesResponse;

    private Context myContext;

    public GetQuestionService() {

    }

    @Override
    public void onCreate() {
        super.onCreate();
        initResponse = new InitialEntriesResponse();
        newEntriesResponse = new NewEntriesResponse();

        myContext = this.getApplicationContext();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        queueUpRequests();
        startRecurringFetch();

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
                lastFetched = System.currentTimeMillis()/1000;
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

        final Handler handler = new Handler();
        Runnable runner = new Runnable() {
            @Override
            public void run() {
                if(!hasMoreEntries){
                    StackExchangeAPI.getInstance(myContext).getAndroidQuestionsAfter(lastFetched, newEntriesResponse);
                    lastFetched = System.currentTimeMillis()/1000;
                    handler.postDelayed(this, 15*60000);
                } else {
                    handler.postDelayed(this, 5000);
                }
            }
        };
        runner.run();

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
     * ResponseHandler for initial retrieval of 100 entries
     */
    class InitialEntriesResponse implements Response.Listener<JSONObject> {
        @Override
        public void onResponse(JSONObject jsonObject) {
            try {
                JSONArray items = jsonObject.getJSONArray("items");
                int itemsCount = items.length();
                for(int i=0; i<itemsCount; i++){
                    AccessDB.getInstance(myContext).insertQuestion(items.getJSONObject(i));
                }
                entriesRetrieved += itemsCount;
                hasMoreEntries = jsonObject.getBoolean("has_more");

            } catch (JSONException e) {
                e.printStackTrace();
            }
            if(entriesRetrieved==(pageSize*maxPages)){
                hasMoreEntries = false;
                lastFetched = System.currentTimeMillis()/1000;
            }
        }
    }

    /**
     * ResponseHandler of the request that checks for new entries
     */
    class NewEntriesResponse implements Response.Listener<JSONObject> {
        @Override
        public void onResponse(JSONObject jsonObject) {
            try {
                JSONArray items = jsonObject.getJSONArray("items");

                int itemsCount = items.length();
                for(int i=0; i<itemsCount; i++){
                    AccessDB.getInstance(myContext).insertQuestion(items.getJSONObject(i));
                }

                if(itemsCount>0){
                    sendNotification(itemsCount);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            lastFetched = System.currentTimeMillis()/1000;

        }
    }

}
