package com.techgeekme.sis;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;

/**
 * Created by anirudh on 10/01/16.
 */
public class LoadingDialogFragment extends DialogFragment {
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        setCancelable(false);
        ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading...");
        return progressDialog;
    }

    @Override
    public void onDestroyView() {
        // Workaround for a bug http://stackoverflow.com/questions/14657490/how-to-properly-retain-a-dialogfragment-through-rotation
        if (getDialog() != null && getRetainInstance())
            getDialog().setDismissMessage(null);
        super.onDestroyView();
    }
}
