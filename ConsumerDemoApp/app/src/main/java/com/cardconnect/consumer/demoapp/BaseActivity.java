package com.cardconnect.consumer.demoapp;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.cardconnect.consumer.demoapp.components.SpinningDialogFragment;

public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (this instanceof CustomFlowActivity && getResources().getBoolean(R.bool.is_phone)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    protected void showProgressDialog() {
        if (getSupportFragmentManager().findFragmentByTag(SpinningDialogFragment.TAG) == null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(SpinningDialogFragment.newInstance(), SpinningDialogFragment.TAG);
            transaction.commitAllowingStateLoss();
        }
    }

    protected void setupToolBar() {
        Toolbar toolbar = (Toolbar)findViewById(R.id.tool_bar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            if (!(this instanceof MainActivity) && getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
        }
    }

    protected void showSnackMessage(@NonNull String message) {
        Snackbar.make(this.findViewById(android.R.id.content), message,
                Snackbar.LENGTH_SHORT).show();
    }

    protected void dismissProgressDialog() {
        SpinningDialogFragment fragment =
                (SpinningDialogFragment)getSupportFragmentManager().findFragmentByTag(SpinningDialogFragment.TAG);

        if (fragment != null) {
            getSupportFragmentManager().beginTransaction().remove(fragment).commitAllowingStateLoss();
        }
    }

    protected void showErrorDialog(@NonNull String errorMessage) {
        new AlertDialog.Builder(this).setTitle(getString(R.string.error)).setMessage(errorMessage)
                .setNeutralButton(android.R.string.ok, null)
                .show();
    }
}
