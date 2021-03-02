package com.example.tasktimer;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

class AppDatabase extends SQLiteOpenHelper {
    private static final String TAG = "AppDatabase";
    public static final String DATABASE_NAME="TaskTimer.db";
    public static final int DATABASE_VERSION=1;
    private static AppDatabase instance=null;

    private AppDatabase(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        Log.d(TAG, "AppDatabase: constructor");
    }
    static AppDatabase getInstance(Context context){
        if(instance==null){
            Log.d(TAG, "getInstance: creating an instance");
            instance=new AppDatabase(context);
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "onCreate: starts");
        String sSQL;
       // sSQL="create table Tasks (_id integer primary key not null,Names text not null,Description text,SortOrder integer,CatagoryID integer);";
        sSQL="create table "+TasksContract.TABLE_NAME+"("+TasksContract.Columns._ID+" integer primary key not null, "
                +TasksContract.Columns.TASKS_NAME+ " text not null, "+TasksContract.Columns.TASKS_DESCRIPTION+" text, "
                +TasksContract.Columns.TASKS_SORTORDER+" integer);";
        Log.d(TAG, sSQL);
        db.execSQL(sSQL);
        Log.d(TAG, "onCreate: ends");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "onUpgrade: starts");
        switch (oldVersion){
            case 1:
                break;
            default:
                throw new IllegalStateException("on upgrade with unknown new version");
        }
        Log.d(TAG, "onUpgrade: ends");
    }
}
