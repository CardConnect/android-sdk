package com.cardconnect.consumer.demoapp;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

public abstract class BaseFragment extends Fragment {

    protected void showErrorDialog(@NonNull String errorMessage) {
        Activity activity = getActivity();
        if (activity instanceof BaseActivity) {
            ((BaseActivity)activity).showErrorDialog(errorMessage);
        }
    }

    protected void showSnackBarMessage(@NonNull String message) {
        Activity activity = getActivity();
        if (activity instanceof BaseActivity) {
            ((BaseActivity)activity).showSnackMessage(message);
        }
    }

    protected void showProgressDialog() {
        Activity activity = getActivity();
        if (activity instanceof BaseActivity) {
            ((BaseActivity)activity).showProgressDialog();
        }
    }

    protected void dismissProgressDialog() {
        Activity activity = getActivity();
        if (activity instanceof BaseActivity) {
            ((BaseActivity)activity).dismissProgressDialog();
        }
    }
}
