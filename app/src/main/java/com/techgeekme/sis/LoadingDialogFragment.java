package com.techgeekme.sis;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

import java.lang.ref.WeakReference;

/**
 * Created by anirudh on 10/01/16.
 */
public class LoadingDialogFragment extends DialogFragment {
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        SisApplication.getInstance().loadingDialogFragmentWeakReference = new WeakReference<LoadingDialogFragment>(this);
        setCancelable(false);
        ProgressDialog progressDialog = new ProgressDialog(getContext());
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
