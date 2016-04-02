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

        Intent intent = new Intent(this, GetQuestionService.class);
        startService(intent);

    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.i("FLOW", "resume");
        listFragment.startCheckDB();

    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i("FLOW", "pause");

    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i("FLOW", "stop");

    }


}
