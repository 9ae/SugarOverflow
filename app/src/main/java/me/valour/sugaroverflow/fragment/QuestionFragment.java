package me.valour.sugaroverflow.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.ListFragment;
import android.view.View;
import android.widget.ListView;


import java.util.ArrayList;

import me.valour.sugaroverflow.adapter.QuestionsAdapter;
import me.valour.sugaroverflow.model.Question;


public class QuestionFragment extends ListFragment {


    private QuestionsAdapter adapter;

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

}
