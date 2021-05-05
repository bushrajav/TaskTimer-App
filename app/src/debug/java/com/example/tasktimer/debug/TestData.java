package com.example.tasktimer.debug;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import com.example.tasktimer.TasksContract;
import com.example.tasktimer.TimingsContract;

import java.util.GregorianCalendar;

public class TestData {
    public static void generateTestData(ContentResolver contentResolver) {
        final int SECS_IN_DAY = 86400;
        final int LOWER_BOUND = 100;
        final int UPPER_BOUND = 500;
        final int MAX_DURATION = SECS_IN_DAY / 6;

        String[] projection = {TasksContract.Columns._ID};
        Uri uri = TasksContract.CONTENT_URI;
        Cursor cursor = contentResolver.query(uri, projection, null, null, null);
        if ((cursor != null) && (cursor.moveToFirst())) {
            do {
                int loopCount=LOWER_BOUND+getRandomInt(UPPER_BOUND-LOWER_BOUND);
                for(int i=0;i<loopCount;i++) {
                    long taskId = cursor.getLong(cursor.getColumnIndex(TasksContract.Columns._ID));
                    long randomDate = randomDateTime();
                    long duration = (long) getRandomInt(MAX_DURATION);
                    TestTiming testTiming = new TestTiming(taskId, randomDate, duration);
                    saveCurrentTiming(contentResolver, testTiming);
                }

            } while (cursor.moveToNext());
            cursor.close();
        }
    }

    private static int getRandomInt(int max) {
        return (int) Math.round(Math.random() * max);
    }

    private static long randomDateTime() {
        final int startYear = 2017;
        final int endYear = 2018;

        int year = startYear + getRandomInt(endYear - startYear);
        int month = getRandomInt(11);
        int hour = getRandomInt(23);
        int min = getRandomInt(59);
        int sec = getRandomInt(59);
        GregorianCalendar gc = new GregorianCalendar(year, month, 1);
        int day = 1 + getRandomInt(gc.getActualMaximum(GregorianCalendar.DAY_OF_MONTH));
        gc.set(year, month, day, hour, min, sec);
        return gc.getTimeInMillis();
    }

    private static void saveCurrentTiming(ContentResolver contentResolver, TestTiming currentTiming) {
        ContentValues values = new ContentValues();
        values.put(TimingsContract.Columns.TIMINGS_TASK_ID, currentTiming.taskId);
        values.put(TimingsContract.Columns.TIMINGS_START_TIME, currentTiming.startTime);
        values.put(TimingsContract.Columns.TIMINGS_DURATION, currentTiming.duration);
        contentResolver.insert(TimingsContract.CONTENT_URI, values);
    }


}
