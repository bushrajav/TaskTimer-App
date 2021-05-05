package com.example.tasktimer;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.security.InvalidParameterException;


/**
 * A simple {@link Fragment} subclass.
 */
public class MainActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>,
        CursorRecyclerViewAdapter.OnTaskClickListener {
    private static final String TAG = "MainActivityFragment";
    public static final int LOADER_ID = 0;
    private CursorRecyclerViewAdapter mAdapter;
    private Timing mCurrentTiming = null;

    public MainActivityFragment() {
        Log.d(TAG, "MainActivityFragment: called");
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
            Log.d(TAG, "onCreate: called");
            setRetainInstance(true);
        }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Activity activity = getActivity();
        if (!(activity instanceof CursorRecyclerViewAdapter.OnTaskClickListener)) {
            throw new ClassCastException(activity.getClass().getSimpleName() + " must implement CursorRecyclerViewAdapter.OnTaskClickListener interface ");
        }
        getLoaderManager().initLoader(LOADER_ID, null, this);
        setTimingText(mCurrentTiming);
    }

    @Override
    public void onEditClick(@NonNull Task task) {
        CursorRecyclerViewAdapter.OnTaskClickListener listener = (CursorRecyclerViewAdapter.OnTaskClickListener) getActivity();
        if (listener != null) {
            listener.onEditClick(task);
        }
    }

    @Override
    public void onDeleteClick(@NonNull Task task) {
        CursorRecyclerViewAdapter.OnTaskClickListener listener = (CursorRecyclerViewAdapter.OnTaskClickListener) getActivity();
        if (listener != null) {
            listener.onDeleteClick(task);
        }
    }

    @Override
    public void onTaskLongClick(@NonNull Task task) {
        Log.d(TAG, "onTaskLongClick: starts");
        if (mCurrentTiming != null) {
            if (task.getId() == mCurrentTiming.getTask().getId()) {
                saveTiming(mCurrentTiming);
                mCurrentTiming = null;
                setTimingText(null);
            } else {
                saveTiming(mCurrentTiming);
                mCurrentTiming = new Timing(task);
                setTimingText(mCurrentTiming);
            }
        } else {
            mCurrentTiming = new Timing(task);
            setTimingText(mCurrentTiming);
        }
    }

    private void saveTiming(@NonNull Timing currentTiming) {
        Log.d(TAG, "saveTiming: starts");
        currentTiming.setDuration();
        ContentResolver contentResolver = getActivity().getContentResolver();
        ContentValues values = new ContentValues();
        values.put(TimingsContract.Columns.TIMINGS_TASK_ID, currentTiming.getTask().getId());
        values.put(TimingsContract.Columns.TIMINGS_START_TIME, currentTiming.getStartTime());
        values.put(TimingsContract.Columns.TIMINGS_DURATION, currentTiming.getDuration());
        contentResolver.insert(TimingsContract.CONTENT_URI, values);
        Log.d(TAG, "saveTiming: ends");
    }

    private void setTimingText(Timing timing) {
        TextView textName = getActivity().findViewById(R.id.current_task);
        if (timing != null) {
            textName.setText(getString(R.string.timing_text) +" "+ timing.getTask().getName());
        } else {
            textName.setText(R.string.no_task_message);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: starts");

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.task_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        if (mAdapter == null) {
            mAdapter = new CursorRecyclerViewAdapter(null, this);
        }
        recyclerView.setAdapter(mAdapter);
        return view;
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        Log.d(TAG, "onCreateLoader: starts");
        String[] projection = {TasksContract.Columns._ID,
                TasksContract.Columns.TASKS_NAME,
                TasksContract.Columns.TASKS_DESCRIPTION,
                TasksContract.Columns.TASKS_SORTORDER};
        String sortOrder = TasksContract.Columns.TASKS_NAME + "," + TasksContract.Columns.TASKS_SORTORDER + " COLLATE NOCASE";
        switch (id) {
            case LOADER_ID:
                return new CursorLoader(getActivity(),
                        TasksContract.CONTENT_URI,
                        projection,
                        null,
                        null,
                        sortOrder);
            default:
                throw new InvalidParameterException(TAG + "called with invalid id " + id);
        }
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        Log.d(TAG, "onLoadFinished: start");
        mAdapter.swapCursor(data);
        int count = mAdapter.getItemCount();
        Log.d(TAG, "onLoadFinished: count is" + count);

    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        Log.d(TAG, "onLoaderReset: starts");
        mAdapter.swapCursor(null);

    }
}
