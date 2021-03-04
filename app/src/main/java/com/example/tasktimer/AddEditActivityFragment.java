package com.example.tasktimer;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public class AddEditActivityFragment extends Fragment {
    private static final String TAG = "AddEditActivityFragment";

    public enum FragmentEditMode {ADD, EDIT}

    private FragmentEditMode mMode;
    private EditText mNameTextView;
    private EditText mDescriptionTextView;
    private EditText mSortOrderTextView;
    private Button mSaveButton;

    public AddEditActivityFragment() {
        Log.d(TAG, "AddEditActivityFragment: constructor called");
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: starts");
        View view = inflater.inflate(R.layout.add_edit_activity_fragment, container, false);
        mNameTextView = view.findViewById(R.id.addedit_name);
        mDescriptionTextView = view.findViewById(R.id.addedit_description);
        mSortOrderTextView = view.findViewById(R.id.addedit_sortorder);
        mSaveButton = view.findViewById(R.id.addedit_save);
        Bundle arguments = getActivity().getIntent().getExtras();
        final Task task;
        if (arguments != null) {
            task = (Task) arguments.getSerializable(Task.class.getSimpleName());
            if (task != null) {
                mNameTextView.setText(task.getName());
                mDescriptionTextView.setText(task.getDescription());
                mSortOrderTextView.setText(Integer.toString(task.getSortOrder()));
                mMode = FragmentEditMode.EDIT;
            } else {
                mMode = FragmentEditMode.ADD;
            }
        } else {
            task = null;
            mMode = FragmentEditMode.ADD;

        }
        mSaveButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                int so;
                if (mSortOrderTextView.length() > 0) {
                    so = Integer.parseInt(mSortOrderTextView.getText().toString());
                } else {
                    so = 0;
                }
                ContentResolver contentResolver = getActivity().getContentResolver();
                ContentValues values = new ContentValues();
                switch (mMode) {
                    case EDIT:
                        if (mNameTextView.getText().equals(task.getName())) {
                            values.put(TasksContract.Columns.TASKS_NAME, mNameTextView.getText().toString());
                        }
                        if (mDescriptionTextView.getText().equals(task.getDescription())) {
                            values.put(TasksContract.Columns.TASKS_DESCRIPTION, mDescriptionTextView.getText().toString());
                        }
                        if (so != task.getSortOrder()) {
                            values.put(TasksContract.Columns.TASKS_SORTORDER, so);
                        }
                        if (values.size() != 0) {
                            contentResolver.update(TasksContract.buildTaskUri(task.getId()),
                                    values,
                                    null,
                                    null);
                        }
                        break;
                    case ADD:
                        if (mNameTextView.length() > 0) {
                            values.put(TasksContract.Columns.TASKS_NAME, mNameTextView.getText().toString());
                            values.put(TasksContract.Columns.TASKS_DESCRIPTION, mDescriptionTextView.getText().toString());
                            values.put(TasksContract.Columns.TASKS_SORTORDER, so);
                            contentResolver.insert(TasksContract.CONTENT_URI,values);
                        }
                        break;
                }


            }
        });

        return view;
    }


}
