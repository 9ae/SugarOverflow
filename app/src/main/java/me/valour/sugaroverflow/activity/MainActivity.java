package me.valour.sugaroverflow.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import java.util.ArrayList;

import me.valour.sugaroverflow.model.Question;
import me.valour.sugaroverflow.fragment.QuestionFragment;
import me.valour.sugaroverflow.R;
import me.valour.sugaroverflow.service.GetQuestionService;


public class MainActivity extends Activity {

    private QuestionFragment listFragment;
    private GetQuestionService service;

    private boolean serviceBound = false;
    private DataUpdateReceiver dataUpdateReceiver;

    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder bind) {
            GetQuestionService.Glue binder = (GetQuestionService.Glue) bind;
            service = binder.getService();
            serviceBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            serviceBound = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("FLOW", "create");

        setContentView(R.layout.activity_main);

        if (listFragment == null) {

            listFragment = QuestionFragment.newInstance();

            getFragmentManager().beginTransaction()
                    .add(R.id.container, listFragment)
                    .commit();
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i("FLOW", "start");

        if(service != null){
            service.stopRecurringFetch();
        }

        if(!serviceBound) {
            Log.i("FLOW", "Binding service");
            Intent intent = new Intent(this, GetQuestionService.class);
            bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
            startService(intent);
        }
        registerServiceReceiver();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i("FLOW", "pause");

        unregisterServiceReceiver();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("FLOW", "resume");

        registerServiceReceiver();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i("FLOW", "stop");

        service.startRecurringFetch();
        unregisterServiceReceiver();
        if (serviceBound) {
            unbindService(mConnection);
            serviceBound = false;
        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("questions", listFragment.getQuestions());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        listFragment.updateList(savedInstanceState.<Question>getParcelableArrayList("questions"));
    }

    /**
     * Register service receiver to activity
     */
    private void registerServiceReceiver(){

        if (dataUpdateReceiver == null) {
            dataUpdateReceiver = new DataUpdateReceiver();
        }
        IntentFilter intentFilter = new IntentFilter(GetQuestionService.UPDATED_ENTERIES);
        registerReceiver(dataUpdateReceiver, intentFilter);

    }

    /**
     * Deregister service receiver to activity
     */
    private void unregisterServiceReceiver(){
        if (dataUpdateReceiver != null){
            unregisterReceiver(dataUpdateReceiver);
            dataUpdateReceiver = null;
        }
    }

    /**
     * Listens for when the service has successfully gotten new question entries
     * and notifies ListFragment so it can add it to the adpater
     * and informs service to clear out its queue
     */
    private class DataUpdateReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (service!= null && listFragment!=null &&
                    intent.getAction().equals(GetQuestionService.UPDATED_ENTERIES)) {
                listFragment.updateList(service.getNewEntries());
                service.clearEntries();
            }
        }
    }

    /**
     * Will probably re-use this for unit testing
     * Here right no to test app when internet connection is down
     * or request to stackexchange is failing
     */
    private void mockQuestions(){
        ArrayList<Question> questions = new ArrayList<Question>();
        questions.add(new Question("1", "wonderland", "http://wwww.yahoo.com"));
        questions.add(new Question("2", "jungle", "http://wwww.aol.com"));
        questions.add(new Question("3", "skynet", "http://wwww.google.com"));
        listFragment.updateList(questions);
    }

}
