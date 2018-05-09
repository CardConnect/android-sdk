package com.cardconnect.consumer.demoapp.swiperinitialization;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cardconnect.consumer.demoapp.BaseFragment;
import com.cardconnect.consumer.demoapp.R;
import com.cardconnect.consumersdk.domain.CCConsumerAccount;
import com.cardconnect.consumersdk.domain.CCConsumerError;
import com.cardconnect.consumersdk.swiper.CCSwiperControllerFactory;
import com.cardconnect.consumersdk.swiper.SwiperController;
import com.cardconnect.consumersdk.swiper.SwiperControllerListener;
import com.cardconnect.consumersdk.swiper.enums.BatteryState;
import com.cardconnect.consumersdk.swiper.enums.SwiperError;
import com.cardconnect.consumersdk.swiper.enums.SwiperType;

public class SwiperTestFragment extends BaseFragment {
    public static final String TAG = SwiperTestFragment.class.getSimpleName();
    private static final int PERMISSIONS_REQUEST_RECORD_AUDIO = 1;
    private TextView mConnectionStateTextView;
    private SwiperController mSwiperController;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_swiper_test, container, false);
        mConnectionStateTextView = (TextView)v.findViewById(R.id.text_view_connection);
        requestRecordAudioPermission();
        return v;
    }

    private void initSwiperForTokenGeneration() {
        mSwiperController = new CCSwiperControllerFactory().create(getActivity(), SwiperType.BBPosDevice,
                new SwiperControllerListener() {
                    @Override
                    public void onTokenGenerated(CCConsumerAccount account, CCConsumerError error) {
                        Log.d(TAG, "onTokenGenerated");
                        dismissProgressDialog();
                        if (error == null) {
                            showSnackBarMessage(account.getToken());
                        } else {
                            showErrorDialog(error.getResponseMessage());
                        }
                    }

                    @Override
                    public void onError(SwiperError swipeError) {
                        Log.d(TAG, swipeError.toString());
                    }

                    @Override
                    public void onSwiperReadyForCard() {
                        Log.d(TAG, "Swiper ready for card");
                        showSnackBarMessage(getString(R.string.ready_for_swipe));
                    }

                    @Override
                    public void onSwiperConnected() {
                        Log.d(TAG, "Swiper connected");
                        mConnectionStateTextView.setText(R.string.connected);
                    }

                    @Override
                    public void onSwiperDisconnected() {
                        Log.d(TAG, "Swiper disconnected");
                        mConnectionStateTextView.setText(R.string.disconnected);
                    }

                    @Override
                    public void onBatteryState(BatteryState batteryState) {
                        Log.d(TAG, batteryState.toString());
                    }

                    @Override
                    public void onStartTokenGeneration() {
                        showProgressDialog();
                        Log.d(TAG, "Token Generation started.");
                    }
                });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mSwiperController != null) {
            mSwiperController.release();
        }
    }

    private void requestRecordAudioPermission() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.RECORD_AUDIO) !=
                PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[] {Manifest.permission.RECORD_AUDIO}, PERMISSIONS_REQUEST_RECORD_AUDIO);
        } else {
            initSwiperForTokenGeneration();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
            @NonNull int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_RECORD_AUDIO) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initSwiperForTokenGeneration();
            }
        }
    }
}
