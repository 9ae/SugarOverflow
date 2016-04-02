package me.valour.sugaroverflow.orm;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import me.valour.sugaroverflow.model.Question;

/**
 * Created by alice on 4/2/16.
 */
public class DBHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "sugaroverflow";
    public static final String DICTIONARY_TABLE_NAME = "questions";
    private static final String DICTIONARY_TABLE_CREATE =
            "CREATE TABLE " + DICTIONARY_TABLE_NAME + " (" +
                    Question.KEY_ID+ " TEXT UNIQUE, "+
                    Question.KEY_TITLE+ " TEXT, " +
                    Question.KEY_LINK+" TEXT, " +
                    Question.KEY_CREATED_ON + " INTEGER );";
    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + DICTIONARY_TABLE_NAME;

    DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DICTIONARY_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
}
