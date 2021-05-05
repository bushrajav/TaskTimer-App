package com.example.tasktimer;

import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.example.tasktimer.debug.TestData;

import org.w3c.dom.Text;

import java.util.zip.Inflater;

import static com.example.tasktimer.R.string.deldiag_message;

public class MainActivity extends AppCompatActivity implements CursorRecyclerViewAdapter.OnTaskClickListener,
        AddEditActivityFragment.OnSaveClicked,
        AppDialog.DialogEvents {
    private static final String TAG = "MainActivity";
    private boolean mTwoPane = false;
    public static final int DIALOG_ID_DELETE = 1;
    public static final int DIALOG_ID_CANCEL_EDIT = 2;
    public static final int DIALOG_ID_CANCEL_EDIT_UP=3;

    private AlertDialog mDialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
//        if (findViewById(R.id.task_details_container) != null) {
//            mTwoPane = true;
        //}
        mTwoPane = (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE);
        FragmentManager fragmentManager = getSupportFragmentManager();
        Boolean editing = fragmentManager.findFragmentById(R.id.task_details_container) != null;

        View addEditLayout = findViewById(R.id.task_details_container);
        View mainFragment = findViewById(R.id.fragment);

        if (mTwoPane) {
            addEditLayout.setVisibility(View.VISIBLE);
            mainFragment.setVisibility(View.VISIBLE);
        } else if (editing) {
            mainFragment.setVisibility(View.GONE);
        } else {
            mainFragment.setVisibility(View.VISIBLE);
            addEditLayout.setVisibility(View.GONE);
        }
    }
    @Override
    public void onSaveClicked() {

        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.task_details_container);
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction()
            .remove(fragment)
            .commit();

        }
        View addEditLayout = findViewById(R.id.task_details_container);
        View mainFragment = findViewById(R.id.fragment);

        if (!mTwoPane) {
            addEditLayout.setVisibility(View.GONE);
            mainFragment.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        if(BuildConfig.DEBUG){
            MenuItem generate=menu.findItem(R.id.menumain_generate);
            generate.setVisible(true);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case R.id.menumain_addTask:
                taskEditRequest(null);
                break;
            case R.id.menumain_showDurations:
                startActivity(new Intent(this,DurationsReport.class));
                break;
            case R.id.menumain_settings:
                break;
            case R.id.menumain_showAbout:
                showAboutDialog();
                break;
            case R.id.menumain_generate:
                TestData.generateTestData(getContentResolver());
                break;
            case android.R.id.home:
                AddEditActivityFragment fragment = (AddEditActivityFragment) getSupportFragmentManager().findFragmentById(R.id.task_details_container);
                if (fragment.canClose()) {
                    return super.onOptionsItemSelected(item);
                }else{
                    showConfirmationDialog(DIALOG_ID_CANCEL_EDIT_UP);
                    return true;
                }
        }

        return super.onOptionsItemSelected(item);
    }

    public void showAboutDialog() {
        View messageView = getLayoutInflater().inflate(R.layout.about, null, false);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.app_name);
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setView(messageView);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (mDialog != null && mDialog.isShowing()) {
                    mDialog.dismiss();
                }
            }
        });


        mDialog = builder.create();
        mDialog.setCanceledOnTouchOutside(true);
        TextView tv = (TextView) messageView.findViewById(R.id.about_version);
        tv.setText("v" + BuildConfig.VERSION_NAME);
        TextView about_url = (TextView) messageView.findViewById(R.id.about_weblink);
        if(about_url != null) {
            about_url.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    String s = ((TextView) v).getText().toString();
                    intent.setData(Uri.parse(s));
                    try {
                        startActivity(intent);
                    } catch(ActivityNotFoundException e) {
                        Toast.makeText(MainActivity.this, "No browser application found, cannot visit world-wide web", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }



        mDialog.show();
    }


    @Override
    public void onEditClick(@NonNull Task task) {
        taskEditRequest(task);

    }

    @Override
    public void onDeleteClick(@NonNull Task task) {
        AppDialog dialog = new AppDialog();
        Bundle args = new Bundle();
        args.putInt(AppDialog.DIALOG_ID, DIALOG_ID_DELETE);
        args.putString(AppDialog.DIALOG_MESSAGE, getString(deldiag_message, task.getId(), task.getName()));
        args.putInt(AppDialog.DIALOG_POSITIVE_RID, R.string.deldiag_positive_caption);
        args.putLong("taskId", task.getId());

        dialog.setArguments(args);
        dialog.show(getSupportFragmentManager(), null);
    }

    private void taskEditRequest(Task task) {
        Log.d(TAG, "taskEditRequest: starts");
        Log.d(TAG, "taskEditRequest: two pane mode");

        AddEditActivityFragment fragment = new AddEditActivityFragment();
        Bundle arguments = new Bundle();
        arguments.putSerializable(Task.class.getSimpleName(), task);
        fragment.setArguments(arguments);
       // getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.task_details_container, fragment);
        fragmentTransaction.commit();



        if (!mTwoPane) {
            View addEditLayout = findViewById(R.id.task_details_container);
            View mainFragment = findViewById(R.id.fragment);
            addEditLayout.setVisibility(View.VISIBLE);
            mainFragment.setVisibility(View.GONE);
        }
    }

    @Override
    public void onPositiveDialogResult(int dialogId, Bundle args) {
        Long taskId = args.getLong("taskId");
        switch (dialogId) {
            case DIALOG_ID_DELETE:
                if (BuildConfig.DEBUG && taskId == 0) throw new AssertionError("Task is zero");
                getContentResolver().delete(TasksContract.buildTaskUri(taskId), null, null);
                break;
            case DIALOG_ID_CANCEL_EDIT:
            case DIALOG_ID_CANCEL_EDIT_UP:
                break;
        }
    }

    @Override
    public void onNegativeDialogResult(int dialogId, Bundle args) {
        switch (dialogId) {
            case DIALOG_ID_CANCEL_EDIT:
            case DIALOG_ID_CANCEL_EDIT_UP:
                FragmentManager fragmentManager=getSupportFragmentManager();
                Fragment fragment=fragmentManager.findFragmentById(R.id.task_details_container);
                if(fragment!=null) {
                    getSupportFragmentManager().beginTransaction()
                            .remove(fragment)
                            .commit();
                    if (mTwoPane) {
                        if(dialogId==DIALOG_ID_CANCEL_EDIT) {
                            finish();
                        }
                    } else {
                        View addEditLayout = findViewById(R.id.task_details_container);
                        View mainFragment = findViewById(R.id.fragment);

                        addEditLayout.setVisibility(View.GONE);
                        mainFragment.setVisibility(View.VISIBLE);
                    }
                }else{
                    finish();
                    }
                break;

            case DIALOG_ID_DELETE:
                break;
        }

    }

    @Override
    public void onDialogCancelled(int dialogId) {

    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed: started");
        AppDialog dialog = new AppDialog();
        FragmentManager fragmentManager = getSupportFragmentManager();
        AddEditActivityFragment fragment = (AddEditActivityFragment) fragmentManager.findFragmentById(R.id.task_details_container);
        if ((fragment == null) || fragment.canClose()) {
            super.onBackPressed();

        }else{
            showConfirmationDialog(DIALOG_ID_CANCEL_EDIT);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }
    }

    private void showConfirmationDialog(int dialogId){
        AppDialog dialog=new AppDialog();
        Bundle args = new Bundle();
        args.putInt(AppDialog.DIALOG_ID, dialogId);
        args.putString(AppDialog.DIALOG_MESSAGE, getString(R.string.cancelEditDiag_message));
        args.putInt(AppDialog.DIALOG_POSITIVE_RID, R.string.cancelEditDiag_positive_caption);
        args.putInt(AppDialog.DIALOG_NEGATIVE_RID, R.string.cancelEditDial_negative_caption);
        dialog.setArguments(args);
        dialog.show(getSupportFragmentManager(), null);
    }

    @Override
    public void onTaskLongClick(@NonNull Task task) {
    }
}
