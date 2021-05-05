package com.example.tasktimer;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


public class AddEditActivity extends AppCompatActivity implements AddEditActivityFragment.OnSaveClicked,
        AppDialog.DialogEvents {
    private static final String TAG = "AddEditActivity";
    public static final int DIALOG_ID_BACKPRESS = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: starts");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle arguments = getIntent().getExtras();

        AddEditActivityFragment fragment = new AddEditActivityFragment();
        //Bundle arguments=new Bundle();
        // arguments.putSerializable(Task.class.getSimpleName(),arguments);
        fragment.setArguments(arguments);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment, fragment);
        fragmentTransaction.commit();

    }

    @Override
    public void onSaveClicked() {
        finish();
    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed: started");
        FragmentManager fragmentManager = getSupportFragmentManager();
        AddEditActivityFragment fragment = (AddEditActivityFragment) fragmentManager.findFragmentById(R.id.fragment);
        if (fragment.canClose()) {
            super.onBackPressed();
        }
        showConfirmationDialog();

    }

    @Override
    public void onPositiveDialogResult(int dialogId, Bundle args) {

    }

    @Override
    public void onNegativeDialogResult(int dialogId, Bundle args) {
        finish();

    }

    @Override
    public void onDialogCancelled(int dialogId) {

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                AddEditActivityFragment fragment = (AddEditActivityFragment) getSupportFragmentManager().findFragmentById(R.id.fragment);
                if (fragment.canClose()) {
                    return super.onOptionsItemSelected(item);
                }else{
                    showConfirmationDialog();
                    return true;
                }
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    public void showConfirmationDialog(){
        AppDialog dialog=new AppDialog();
        Bundle args = new Bundle();
        args.putInt(AppDialog.DIALOG_ID, DIALOG_ID_BACKPRESS);
        args.putString(AppDialog.DIALOG_MESSAGE, getString(R.string.cancelEditDiag_message));
        args.putInt(AppDialog.DIALOG_POSITIVE_RID, R.string.cancelEditDiag_positive_caption);
        args.putInt(AppDialog.DIALOG_NEGATIVE_RID, R.string.cancelEditDial_negative_caption);
        dialog.setArguments(args);
        dialog.show(getSupportFragmentManager(), null);
    }
}