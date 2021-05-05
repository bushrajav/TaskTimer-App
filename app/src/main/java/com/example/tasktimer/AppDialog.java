package com.example.tasktimer;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class AppDialog extends DialogFragment {
    private static final String TAG = "AppDialog";
    public static final String DIALOG_ID = "dialogId";
    public static final String DIALOG_MESSAGE = "message";
    public static final String DIALOG_POSITIVE_RID = "positive_rid";
    public static final String DIALOG_NEGATIVE_RID = "negative_rid";

    interface DialogEvents {
        void onPositiveDialogResult(int dialogId, Bundle args);

        void onNegativeDialogResult(int dialogId, Bundle args);

        void onDialogCancelled(int dialogId);

    }

    private DialogEvents mDialogEvents;

    @Override
    public void onAttach(@NonNull Context context) {
        Log.d(TAG, "onAttach: entering " + context.toString());
        super.onAttach(context);
        if (!(context instanceof DialogEvents)) {
            throw new ClassCastException(context.toString() + " must implement DialogEvents interface");
        }
        mDialogEvents = (DialogEvents) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mDialogEvents = null;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final Bundle arguments = getArguments();
        String messageString;
        int positiveStringId;
        int negativeStringId;
        final int dialogId;
        if (arguments != null) {
            dialogId = arguments.getInt(DIALOG_ID);
            messageString = arguments.getString(DIALOG_MESSAGE);
            if (dialogId == 0 || messageString == null) {
                throw new IllegalArgumentException("Dialog id and/or message not present in the bundle");
            }
            positiveStringId = arguments.getInt(DIALOG_POSITIVE_RID);
            if (positiveStringId == 0) {
                positiveStringId = R.string.ok;
            }
            negativeStringId = arguments.getInt(DIALOG_NEGATIVE_RID);
            if (negativeStringId == 0) {
                negativeStringId = R.string.cancel;
            }
        } else {
            throw new IllegalArgumentException("Dialog id and message not present");
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(messageString)
                .setPositiveButton(positiveStringId, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (mDialogEvents != null) {
                            mDialogEvents.onPositiveDialogResult(dialogId, arguments);
                        }

                    }
                }).setNegativeButton(negativeStringId, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (mDialogEvents != null) {
                    mDialogEvents.onNegativeDialogResult(dialogId, arguments);
                }
            }
        });


        return builder.create();

    }

    @Override
    public void onCancel(@NonNull DialogInterface dialog) {
        Log.d(TAG, "onCancel: onCancel called");
        if (mDialogEvents != null) {
            int dialodId = getArguments().getInt(DIALOG_ID);
            mDialogEvents.onDialogCancelled(dialodId);
        }
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        Log.d(TAG, "onDismiss: called");
        super.onDismiss(dialog);
    }
}
