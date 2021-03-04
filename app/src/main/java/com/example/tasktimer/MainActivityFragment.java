package com.example.tasktimer;

import android.database.Cursor;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.security.InvalidParameterException;


/**
 * A simple {@link Fragment} subclass.
 */
public class MainActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = "MainActivityFragment";
    public final int LOADER_ID = 0;

    public MainActivityFragment() {
        Log.d(TAG, "MainActivityFragment: called");
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        LoaderManager.getInstance(this).initLoader(LOADER_ID, null, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: starts");

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        Log.d(TAG, "onCreateLoader: starts");
        String[] projection = {TasksContract.Columns._ID,
                TasksContract.Columns.TASKS_NAME,
                TasksContract.Columns.TASKS_DESCRIPTION,
                TasksContract.Columns.TASKS_SORTORDER};
        String sortOrder = TasksContract.Columns.TASKS_NAME + "," + TasksContract.Columns.TASKS_SORTORDER;
        switch (id) {
            case LOADER_ID:
                if (id == LOADER_ID) {
                    return new CursorLoader(getActivity(),
                            TasksContract.CONTENT_URI,
                            projection,
                            null,
                            null,
                            sortOrder);
                }
                break;
            default:
                throw new InvalidParameterException(TAG + "called with invalid id " + id);
        }


        return null;
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        int count = -1;
        Log.d(TAG, "onLoadFinished: start");
        if (data != null) {
            while (data.moveToNext()) {
                for (int i = 0; i < data.getColumnCount(); i++) {
                    Log.d(TAG, "onLoadFinished: " + data.getColumnName(i) + " : " + data.getString(i));
                }
                Log.d(TAG, "onLoadFinished: =====================================");
            }
            count = data.getCount();
        }
        Log.d(TAG, "onLoadFinished: count is" + count);

    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        Log.d(TAG, "onLoaderReset: starts");

    }
}
