package me.valour.sugaroverflow.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.ListFragment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ListView;


import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import me.valour.sugaroverflow.adapter.QuestionsAdapter;
import me.valour.sugaroverflow.model.Question;
import me.valour.sugaroverflow.orm.AccessDB;


public class QuestionFragment extends ListFragment {


    private QuestionsAdapter adapter;

    private Handler dbCheckHandler;
    private CheckDBForUpdates dbChecker;
    protected int lastChecked = 0;

    private Context appContext;

    /**
     * Instantiate Fragment
     * @return
     */
    public static QuestionFragment newInstance() {
        QuestionFragment fragment = new QuestionFragment();
        return fragment;
    }

    public QuestionFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        adapter = new QuestionsAdapter(getActivity());
        setListAdapter(adapter);

        appContext = this.getActivity();
        dbCheckHandler = new Handler();
        dbChecker = new CheckDBForUpdates();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        openLink(adapter.getItem(position).link);

    }

    /**
     * Adds elements to the adpater's internal list and notifies change
     * @param newEntries
     */
    public void updateList(ArrayList<Question> newEntries) {
        adapter.addAll(newEntries);
        adapter.notifyDataSetChanged(); //TODO: do we actually need this?
    }

    /**
     * Launched intent to open URL with default browser
     * @param url
     */
    public void openLink(String url){
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }

    /**
     * Gives a list of all current questions added into the listview
     * @return ArrayList of Questions
     */
    public ArrayList<Question> getQuestions(){
        int count = adapter.getCount();
        ArrayList<Question> questions = new ArrayList<Question>();
        for(int i=0; i<count; i++){
            questions.add(adapter.getItem(i));
        }
        return questions;
    }

    public void startCheckDB(){
        dbChecker.run();
    }


    class CheckDBForUpdates implements Runnable {

        @Override
        public void run() {
            ArrayList<Question> questions = AccessDB.getInstance(appContext).listQuestions(lastChecked);
            updateList(questions);

            if(questions.size()>0) {
                lastChecked = (int) (System.currentTimeMillis() / 1000);
            }
            Log.i("FLOW", "checking DB");
            dbCheckHandler.postDelayed(this, 30000);
        }
    }

}
