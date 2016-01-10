package com.techgeekme.sis;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by anirudh on 10/01/16.
 */
public class LoadingDialogFragment extends DialogFragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setRetainInstance(true);
        View rootView = inflater.inflate(R.layout.loading_dialog, container, false);
        getDialog().setTitle("Loading!");
        return rootView;
    }

    @Override
    public void onDestroyView() {
        // Workaround for a bug http://stackoverflow.com/questions/14657490/how-to-properly-retain-a-dialogfragment-through-rotation
        if (getDialog() != null && getRetainInstance())
            getDialog().setDismissMessage(null);
        super.onDestroyView();
    }
}
