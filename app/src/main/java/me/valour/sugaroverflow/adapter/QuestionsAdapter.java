package me.valour.sugaroverflow.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import me.valour.sugaroverflow.R;
import me.valour.sugaroverflow.model.Question;

/**
 * Created by alice on 3/30/16.
 */
public class QuestionsAdapter extends ArrayAdapter<Question> {

    ViewHolder holder;

    public QuestionsAdapter(Context ctx){
        super(ctx, android.R.layout.simple_selectable_list_item);

    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if(convertView==null){
            LayoutInflater inflater = ((Activity)getContext()).getLayoutInflater();
            convertView = inflater.inflate(R.layout.question_view, parent, false);
            holder = new ViewHolder();
            holder.titleView = (TextView) convertView.findViewById(R.id.title);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.position = position;
        Question q = getItem(position);
        holder.titleView.setText(q.title);

        return convertView;
    }

    static class ViewHolder{
        public int position;
        public TextView titleView;
    }

}
