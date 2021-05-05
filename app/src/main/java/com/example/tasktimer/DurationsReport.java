package com.example.tasktimer;

import android.app.DatePickerDialog;
import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.BaseColumns;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

import java.security.InvalidParameterException;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class DurationsReport extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>,
        DatePickerDialog.OnDateSetListener,
        AppDialog.DialogEvents,
        View.OnClickListener{
    private static final String TAG = "DurationsReport";
    private static final int LOADER_ID = 1;

    public static final int DIALOG_FILTER = 1;
    public static final int DIALOG_DELETE = 2;

    private static final String SELECTION_PARAM = "SELECTION";
    private static final String SELECTION_ARGS_PARAM = "SELECTION_ARGS";
    private static final String SORT_ORDER_PARAM = "SORT_ORDER";

    public static final String DELETION_DATE = "DELETION_DATE";

    public static final String CURRENT_DATE = "CURRENT_DATE";
    public static final String DISPLAY_WEEK = "DISPLAY_WEEK";

    private Bundle mArgs = new Bundle();
    private boolean mDisplayWeek = true;
    private DurationsRVAdapter mAdapter;
    private final GregorianCalendar mCalendar = new GregorianCalendar();


    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putLong(CURRENT_DATE, mCalendar.getTimeInMillis());
        outState.putBoolean(DISPLAY_WEEK, mDisplayWeek);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_durations_report);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        if (savedInstanceState != null) {
            long timeInMillis = savedInstanceState.getLong(CURRENT_DATE, 0);
            if (timeInMillis != 0) {
                mCalendar.setTimeInMillis(timeInMillis);
                mCalendar.clear(GregorianCalendar.HOUR_OF_DAY);
                mCalendar.clear(GregorianCalendar.MINUTE);
                mCalendar.clear(GregorianCalendar.SECOND);
            }
            mDisplayWeek = savedInstanceState.getBoolean(DISPLAY_WEEK, true);
        }
        applyFilter();
        TextView taskName=findViewById(R.id.td_name_heading);
        taskName.setOnClickListener(this);

        TextView taskDesc=findViewById(R.id.td_description_heading);
        if(taskDesc!=null) {
            taskDesc.setOnClickListener(this);
        }
        TextView taskDate=findViewById(R.id.td_start_heading);
        taskDate.setOnClickListener(this);

        TextView taskDuration=findViewById(R.id.td_duration_heading);
        taskDuration.setOnClickListener(this);



        
        RecyclerView recyclerView = findViewById(R.id.td_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        // Create an empty adapter we will use, to display the loaded data.
        if (mAdapter == null) {
            mAdapter = new DurationsRVAdapter(this, null);
        }
        recyclerView.setAdapter(mAdapter);

        getSupportLoaderManager().initLoader(LOADER_ID, mArgs, this);

    }

    @Override
    public void onClick(View view) {
        Log.d(TAG, "onClick: called");
        switch (view.getId()){
            case R.id.td_name_heading:
                mArgs.putString(SORT_ORDER_PARAM,DurationsContract.Columns.DURATIONS_NAME);
                break;
            case R.id.td_description_heading:
                mArgs.putString(SORT_ORDER_PARAM,DurationsContract.Columns.DURATIONS_DESCRIPTION);
                break;
            case R.id.td_start_heading:
                mArgs.putString(SORT_ORDER_PARAM,DurationsContract.Columns.DURATIONS_START_DATE);
                break;
            case R.id.td_duration_heading:
                mArgs.putString(SORT_ORDER_PARAM,DurationsContract.Columns.DURATIONS_DURATION);
                break;
        }
        getSupportLoaderManager().restartLoader(LOADER_ID,mArgs,this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_report, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.rm_filter_period:
                mDisplayWeek = !mDisplayWeek;
                applyFilter();
                invalidateOptionsMenu();
                getSupportLoaderManager().restartLoader(LOADER_ID, mArgs, this);
                return true;
            case R.id.rm_filter_date:
                showDatePickerDialog(getString(R.string.date_title_filter), DIALOG_FILTER);
                return true;
            case R.id.rm_delete:
                showDatePickerDialog(getString(R.string.date_title_delete), DIALOG_DELETE);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.rm_filter_period);
        if (item != null) {
            if (mDisplayWeek) {
                item.setIcon(R.drawable.ic_baseline_filter_1_24);
                item.setTitle(R.string.rm_title_filter_day);
            } else {
                item.setIcon(R.drawable.ic_baseline_filter_7_24);
                item.setTitle(R.string.rm_title_filter_week);
            }
        }
        return super.onPrepareOptionsMenu(menu);
    }

    private void showDatePickerDialog(String title, int id) {
        Log.d(TAG, "showDatePickerDialog: Entering");
        DialogFragment dialogFragment = new DatePickerFragment();

        Bundle arguments = new Bundle();
        arguments.putInt(DatePickerFragment.DATE_PICKER_ID, id);
        arguments.putString(DatePickerFragment.DATE_PICKER_TITLE, title);
        arguments.putSerializable(DatePickerFragment.DATE_PICKER_DATE, mCalendar.getTime());
        dialogFragment.setArguments(arguments);
        dialogFragment.show(getSupportFragmentManager(), "datePicker");
        Log.d(TAG, "showDatePickerDialog: Exiting");

    }

    @Override
    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
        int dialogId = (int) datePicker.getTag();
        mCalendar.set(i, i1, i2, 0, 0, 0);

        switch (dialogId) {
            case DIALOG_FILTER:
                applyFilter();
                getSupportLoaderManager().restartLoader(LOADER_ID, mArgs, this);
                break;
            case DIALOG_DELETE:
                String fromDate = DateFormat.getDateFormat(this).format(mCalendar.getTimeInMillis());
                AppDialog dialog = new AppDialog();
                Bundle arguments = new Bundle();
                arguments.putInt(AppDialog.DIALOG_ID, 1);
                arguments.putString(AppDialog.DIALOG_MESSAGE, getString(R.string.delete_timing_message, fromDate));
                arguments.putLong(DELETION_DATE, mCalendar.getTimeInMillis());
                dialog.setArguments(arguments);
                dialog.show(getSupportFragmentManager(), null);

                break;
            default:
                throw new IllegalArgumentException("Invalid mode when receiving datePickerDialog result");
        }
    }

    private void deleteRecords(long timeInMillis) {
        Log.d(TAG, "deleteRecords: Entering delete records...");
        long longDate = timeInMillis / 1000;
        String[] selectionArgs = new String[]{Long.toString(longDate)};
        String selection = TimingsContract.Columns.TIMINGS_START_TIME + " < ?";
        Log.d(TAG, "deleteRecords: Deleting prior to " + longDate);
        ContentResolver contentResolver = getContentResolver();
        contentResolver.delete(TimingsContract.CONTENT_URI, selection, selectionArgs);
        applyFilter();
        getSupportLoaderManager().restartLoader(LOADER_ID, mArgs, this);
        Log.d(TAG, "deleteRecords: Exiting...");
    }

    @Override
    public void onPositiveDialogResult(int dialogId, Bundle args) {
        Log.d(TAG, "onPositiveDialogResult: called ");
        long deleteDate = args.getLong(DELETION_DATE);
        deleteRecords(deleteDate);
        getSupportLoaderManager().restartLoader(LOADER_ID, mArgs, this);
    }

    @Override
    public void onNegativeDialogResult(int dialogId, Bundle args) {

    }

    @Override
    public void onDialogCancelled(int dialogId) {

    }

    private void applyFilter() {
        Log.d(TAG, " Entering applyFilter");
        if (mDisplayWeek) {
            Log.d(TAG, "applyFilter(7) starts");
            Date currentCalendarDate = mCalendar.getTime();
            int dayOfWeek = mCalendar.get(GregorianCalendar.DAY_OF_WEEK);
            int weekStart = mCalendar.getFirstDayOfWeek();
            Log.d(TAG, "applyFilter: date is " + currentCalendarDate);
            Log.d(TAG, "applyFilter: day of week is " + dayOfWeek);
            Log.d(TAG, "applyFilter: week start is " + weekStart);

            mCalendar.set(GregorianCalendar.DAY_OF_WEEK, weekStart);
            String startDate = String.format(Locale.US, "%04d-%02d-%02d",
                    mCalendar.get(GregorianCalendar.YEAR),
                    mCalendar.get(GregorianCalendar.MONTH) + 1,
                    mCalendar.get(GregorianCalendar.DAY_OF_MONTH));
            mCalendar.add(GregorianCalendar.DATE, 6);
            String endDate = String.format(Locale.US, "%04d-%02d-%02d",
                    mCalendar.get(GregorianCalendar.YEAR),
                    mCalendar.get(GregorianCalendar.MONTH) + 1,
                    mCalendar.get(GregorianCalendar.DAY_OF_MONTH));
            String[] selectionArgs = new String[]{startDate, endDate};
            mCalendar.setTime(currentCalendarDate);
            Log.d(TAG, "In applyFilter(7), Start date is " + startDate + ", End Date is " + endDate);
            mArgs.putString(SELECTION_PARAM, "StartDate Between ? AND ?");
            mArgs.putStringArray(SELECTION_ARGS_PARAM, selectionArgs);


        } else {

            String startDate = String.format(Locale.US, "%04d-%02d-%02d",
                    mCalendar.get(GregorianCalendar.YEAR),
                    mCalendar.get(GregorianCalendar.MONTH) + 1,
                    mCalendar.get(GregorianCalendar.DAY_OF_MONTH));
            Log.d(TAG, "applyFilter(1) start date is " + startDate);
            String[] selectionArgs = new String[]{startDate};
            mArgs.putString(SELECTION_PARAM, "startDate = ?");
            mArgs.putStringArray(SELECTION_ARGS_PARAM, selectionArgs);
        }
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        switch (id) {
            case LOADER_ID:
                String[] projection = {BaseColumns._ID,
                        DurationsContract.Columns.DURATIONS_NAME,
                        DurationsContract.Columns.DURATIONS_DESCRIPTION,
                        DurationsContract.Columns.DURATIONS_START_TIME,
                        DurationsContract.Columns.DURATIONS_START_DATE,
                        DurationsContract.Columns.DURATIONS_DURATION};

                String selection = null;
                String[] selectionArgs = null;
                String sortOrder = null;

                if (args != null) {
                    selection = args.getString(SELECTION_PARAM);
                    selectionArgs = args.getStringArray(SELECTION_ARGS_PARAM);
                    sortOrder = args.getString(SORT_ORDER_PARAM);
                }

                return new CursorLoader(this,
                        DurationsContract.CONTENT_URI,
                        projection,
                        selection,
                        selectionArgs,
                        sortOrder);

            default:
                throw new InvalidParameterException(TAG + ".onCreateLoader called with invalid loader id " + id);
        }
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        Log.d(TAG, "onLoadFinished: start");
        mAdapter.swapCursor(data);
        int count = mAdapter.getItemCount();
        Log.d(TAG, "onLoadFinished: count is " + count);

    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        Log.d(TAG, "onLoaderReset: starts");
        mAdapter.swapCursor(null);

    }
}