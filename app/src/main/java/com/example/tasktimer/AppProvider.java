package com.example.tasktimer;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.net.URI;

import javax.xml.datatype.Duration;

public class AppProvider extends ContentProvider {
    private static final String TAG = "AppProvider";
    private AppDatabase mOpenHelper;
    private static final UriMatcher sUriMatcher = buildUriMatcher();

    static final String CONTENT_AUTHORITY = "com.example.tasktimer.provider";
    public static final Uri CONTENT_AUTHORITY_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    private static final int TASKS = 100;
    private static final int TASKS_ID = 101;

    private static final int TIMINGS = 200;
    private static final int TIMINGS_ID = 201;

    private static final int TASKS_DURATION = 400;
    private static final int TASKS_DURATION_ID = 401;

    private static UriMatcher buildUriMatcher() {
        //when there is no table name provided
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);

        matcher.addURI(CONTENT_AUTHORITY, TasksContract.TABLE_NAME, TASKS);
        matcher.addURI(CONTENT_AUTHORITY, TasksContract.TABLE_NAME + "/#", TASKS_ID);

//       matcher.addURI(CONTENT_AUTHORITY,TimingsContract.TABLE_NAME,TASKS_TIMINGS);
//        matcher.addURI(CONTENT_AUTHORITY,TimingsContract.TABLE_NAME+"/#",TASKS_TIMINGS_ID);
//
//        matcher.addURI(CONTENT_AUTHORITY,DurationContract.TABLE_NAME,TASKS_DURATION);
//        matcher.addURI(CONTENT_AUTHORITY, DurationContract.TABLE_NAME+"/#",TASKS_DURATION_ID);

        return matcher;


    }

    @Override
    public boolean onCreate() {
        mOpenHelper = AppDatabase.getInstance(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        Log.d(TAG, "query: URI is " + uri);
        final int match = sUriMatcher.match(uri);
        Log.d(TAG, "query: match is " + match);
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        switch (match) {
            case TASKS:
                queryBuilder.setTables(TasksContract.TABLE_NAME);
                break;
            case TASKS_ID:
                queryBuilder.setTables(TasksContract.TABLE_NAME);
                long taskId = TasksContract.getTaskId(uri);
                queryBuilder.appendWhere(TasksContract.Columns._ID + "=" + taskId);
                break;

//            case TIMINGS:
//                queryBuilder.setTables(TimingsContract.TABLE_NAME);
//                break;
//            case TIMINGS_ID:
//                queryBuilder.setTables(TimingsContract.TABLE_NAME);
//                long timingId = TimingsContract.getTimingId(uri);
//                queryBuilder.appendWhere(TimingsContract.Columns._ID + "=" + timingId);
//                break;
//
//            case TASKS_DURATION:
//                queryBuilder.setTables(DurationsContract.TABLE_NAME);
//                break;
//            case TASKS_DURATION_ID:
//                queryBuilder.setTables(DurationsContract.TABLE_NAME);
//                long durationsId = DurationsContract.getDurationId(uri);
//                queryBuilder.appendWhere(DurationsContract.Columns._ID + "=" + durationsId);
//                break;

            default:
                throw new IllegalArgumentException("Unknown Uri " + uri);
        }

        SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        return queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);

    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case TASKS:
                return TasksContract.CONTENT_TYPE;
            case TASKS_ID:
                return TasksContract.CONTENT_ITEM_TYPE;


//            case TIMINGS:
//                return TimingsContract.Timings.CONTENT_TYPE;
//
//            case TIMINGS_ID:
//                return TimingsContract.Timings.CONTENT_ITEM_TYPE;
//
//            case TASKS_DURATION:
            //return DurationContract.Duration.CONTENT_TYPE;
//
//            case TASKS_DURATION_ID:
            //return DurationContract.Duration.CONTENT_ITEM_TYPE;
//
            default:
                throw new IllegalArgumentException("Unknown Uri " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        Log.d(TAG, "insert: called " + uri);
        final int match = sUriMatcher.match(uri);
        Log.d(TAG, "match is " + match);
        final SQLiteDatabase db;
        long recordId;
        Uri returnUri;
        switch (match) {

            case TASKS:
                db = mOpenHelper.getWritableDatabase();
                recordId = db.insert(TasksContract.TABLE_NAME, null, values);
                if (recordId >= 0) {
                    returnUri = TasksContract.buildTaskUri(recordId);
                } else {
                    throw new android.database.SQLException("Failed to insert into " + uri.toString());
                }
                break;
//            case TIMINGS:
//                db = mOpenHelper.getWritableDatabase();
//                recordId = db.insert(TimingsContract.TABLE_NAME, null, values);
//                if (recordId >= 0) {
//                    returnUri = TimingsContract.buildTimingsUri(recordId);
//                } else {
//                    throw new android.database.SQLException("Failed to insert into " + uri.toString());
//                }
//                break;
            default:
                throw new IllegalArgumentException("Unknown uri " + uri);
        }
        Log.d(TAG, "Exiting insert: "+returnUri);
        return returnUri;

    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[]
            selectionArgs) {
            Log.d(TAG, "delete: called " + uri);
            final int match = sUriMatcher.match(uri);
            Log.d(TAG, "match is " + match);
            final SQLiteDatabase db;
            String selectionCriteria=null;
            int count;
            long TaskId;

            switch (match) {

                case TASKS:
                    db = mOpenHelper.getWritableDatabase();
                    count = db.delete(TasksContract.TABLE_NAME, selection, selectionArgs);
                    break;

                case TASKS_ID:
                    db = mOpenHelper.getWritableDatabase();
                    TaskId = TasksContract.getTaskId(uri);
                    if((selection!=null)&&(selection.length()>0)){
                        selectionCriteria+="AND ("+ selection +")";
                    }
                    selectionCriteria = TasksContract.Columns._ID + "=" + TaskId;
                    count = db.delete(TasksContract.TABLE_NAME, selectionCriteria, selectionArgs);
                    break;

//            case TIMINGS:
//                db = mOpenHelper.getWritableDatabase();
//                count = db.delete(TasksContract.TABLE_NAME, selection, selectionArgs);
//                break;
//
//            case TIMINGS_ID:
//                db = mOpenHelper.getWritableDatabase();
//                long TimingId = TimingsContract.getTimingId(uri);
//                if((selection!=null)&&(selection.length()>0)){
//                    selectionCriteria+="AND ("+ selection +")";
//                }
//                selectionCriteria = TimingsContract.Columns._ID + "=" + TimingId;
//                count = db.delete(TimingsContract.TABLE_NAME, selectionCriteria, selectionArgs);
//                break;

                default:
                    throw new IllegalArgumentException("Unknown uri " + uri);
            }
            Log.d(TAG, "Exiting delete, returning " + count);
            return count;
        }


    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String
            selection, @Nullable String[] selectionArgs) {
        Log.d(TAG, "update: called " + uri);
        final int match = sUriMatcher.match(uri);
        Log.d(TAG, "match is " + match);
        final SQLiteDatabase db;
        String selectionCriteria=null;
        int count;
        long TaskId;

        switch (match) {

            case TASKS:
                db = mOpenHelper.getWritableDatabase();
                count = db.update(TasksContract.TABLE_NAME, values, selection, selectionArgs);
                break;

            case TASKS_ID:
                db = mOpenHelper.getWritableDatabase();
                TaskId = TasksContract.getTaskId(uri);
                if((selection!=null)&&(selection.length()>0)){
                    selectionCriteria+="AND ("+ selection +")";
                }
                selectionCriteria = TasksContract.Columns._ID + "=" + TaskId;
                count = db.update(TasksContract.TABLE_NAME, values, selectionCriteria, selectionArgs);
                break;

//            case TIMINGS:
//                db = mOpenHelper.getWritableDatabase();
//                count = db.update(TasksContract.TABLE_NAME, values, selection, selectionArgs);
//                break;
//
//            case TIMINGS_ID:
//                db = mOpenHelper.getWritableDatabase();
//                long TimingId = TimingsContract.getTimingId(uri);
//                if((selection!=null)&&(selection.length()>0)){
//                    selectionCriteria+="AND ("+ selection +")";
//                }
//                selectionCriteria = TimingsContract.Columns._ID + "=" + TimingId;
//                count = db.update(TimingsContract.TABLE_NAME, values, selectionCriteria, selectionArgs);
//                break;

            default:
                throw new IllegalArgumentException("Unknown uri " + uri);
        }
        Log.d(TAG, "Exiting update, returning " + count);
        return count;
    }
}