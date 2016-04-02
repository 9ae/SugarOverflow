package me.valour.sugaroverflow.orm;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import me.valour.sugaroverflow.model.Question;

/**
 * Created by alice on 4/2/16.
 */
public class AccessDB {
    private static AccessDB ourInstance;
    private static Context ctx;
    private DBHelper helper;

    public static AccessDB getInstance(Context ctx) {

        if(ourInstance==null){
            ourInstance = new AccessDB(ctx);
        }
        return ourInstance;
    }

    private AccessDB(Context ctx) {
        this.ctx = ctx;
        helper = new DBHelper(this.ctx);
    }

    public long insertQuestion(Question question) {
        SQLiteDatabase db = helper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Question.KEY_ID, question.id);
        values.put(Question.KEY_TITLE, question.title);
        values.put(Question.KEY_LINK, question.link);
        values.put(Question.KEY_CREATED_ON, question.creation_date);

        long newRowId = -1;
        try {
            newRowId = db.insertOrThrow(
                    DBHelper.DICTIONARY_TABLE_NAME,
                    null,
                    values);
        } catch (SQLException e) {

        }
        return newRowId;
    }

    public long insertQuestion(JSONObject obj) {
        long newRowId = -1;
        try {
            SQLiteDatabase db = helper.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(Question.KEY_ID, obj.getString(Question.KEY_ID));
            values.put(Question.KEY_TITLE, obj.getString(Question.KEY_TITLE));
            values.put(Question.KEY_LINK, obj.getString(Question.KEY_LINK));
            values.put(Question.KEY_CREATED_ON, obj.getInt(Question.KEY_CREATED_ON));


            try {
                newRowId = db.insertOrThrow(
                        DBHelper.DICTIONARY_TABLE_NAME,
                        null,
                        values);
            } catch (SQLException e) {

            }
        } catch (JSONException e) {

        }

        Log.i("FLOW", "Inserted question id:"+newRowId);
        return newRowId;
    }

    public ArrayList<Question> listQuestions(int sinceDate){
        SQLiteDatabase db = helper.getReadableDatabase();
        ArrayList<Question> questions = new ArrayList<Question>();

        String[] projection = {Question.KEY_ID, Question.KEY_TITLE, Question.KEY_LINK, Question.KEY_CREATED_ON};
        String[] selectionArgs = {Integer.toString(sinceDate)};

        String sortOrder = Question.KEY_CREATED_ON+" ASC";

        Cursor c = db.query(
                DBHelper.DICTIONARY_TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                Question.KEY_CREATED_ON+">?",                                // The columns for the WHERE clause
                selectionArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );

        while(c.moveToNext()){
            String id = c.getString(c.getColumnIndexOrThrow(Question.KEY_ID));
            String title = c.getString(c.getColumnIndexOrThrow(Question.KEY_TITLE));
            String link = c.getString(c.getColumnIndexOrThrow(Question.KEY_LINK));
            int creation_date = c.getInt(c.getColumnIndexOrThrow(Question.KEY_CREATED_ON));

            questions.add(new Question(id, title, link, creation_date));
        }


        return questions;
    }
}
