package com.example.tasktimer;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        AppDatabase appDatabase = AppDatabase.getInstance(this);
//        final SQLiteDatabase db = appDatabase.getReadableDatabase();
//        Log.d(TAG, "onCreate: "+db.toString());


        String[] projection = {TasksContract.Columns._ID,
                TasksContract.Columns.TASKS_NAME,
                TasksContract.Columns.TASKS_DESCRIPTION,
                TasksContract.Columns.TASKS_SORTORDER};

        ContentResolver contentResolver = getContentResolver();
        ContentValues values = new ContentValues();
        String selection=TasksContract.Columns.TASKS_SORTORDER+"="+"?";
                String[] selectionArgs={"4"};
       int count= contentResolver.delete(TasksContract.CONTENT_URI,selection,selectionArgs);
        Log.d(TAG, "onCreate: deleted "+count+" recoreds...");

//      values.put(TasksContract.Columns.TASKS_NAME,"testing");
//        values.put(TasksContract.Columns.TASKS_DESCRIPTION,"testing...");
//        String selection=TasksContract.Columns.TASKS_SORTORDER+"="+4;
//        int count= contentResolver.update(TasksContract.CONTENT_URI,values,selection,null);
//        Log.d(TAG, "onCreate: updated "+count+" records");

//        contentResolver.update(TasksContract.buildTaskUri(4),values,null,null);

//        values.put(TasksContract.Columns.TASKS_NAME,"Task number 1");
//        values.put(TasksContract.Columns.TASKS_DESCRIPTION,"Task number1 description");
//        values.put(TasksContract.Columns.TASKS_SORTORDER,5);
//        Uri uri=contentResolver.insert(TasksContract.CONTENT_URI,values);

        Cursor cursor = contentResolver.query(TasksContract.CONTENT_URI,
                projection,
                null,
                null,
                TasksContract.Columns.TASKS_NAME);
        if (cursor != null) {
            Log.d(TAG, "onCreate: number of rows " + cursor.getCount());
            while (cursor.moveToNext()) {
                for (int i = 0; i < cursor.getColumnCount(); i++) {
                    Log.d(TAG, "onCreate: " + cursor.getColumnName(i) + " : " + cursor.getString(i));
                }
                Log.d(TAG, "onCreate: =============================================");

            }
            cursor.close();
        }

//        FloatingActionButton fab = findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.menumain_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
