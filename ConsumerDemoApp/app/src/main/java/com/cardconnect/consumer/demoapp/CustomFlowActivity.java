package com.cardconnect.consumer.demoapp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.cardconnect.consumersdk.CCConsumerTokenCallback;
import com.cardconnect.consumersdk.domain.CCConsumerAccount;
import com.cardconnect.consumersdk.domain.CCConsumerCardInfo;
import com.cardconnect.consumersdk.domain.CCConsumerError;
import com.cardconnect.consumersdk.enums.CCConsumerMaskFormat;
import com.cardconnect.consumersdk.swiper.CCSwiperControllerFactory;
import com.cardconnect.consumersdk.swiper.SwiperController;
import com.cardconnect.consumersdk.swiper.SwiperControllerListener;
import com.cardconnect.consumersdk.swiper.enums.BatteryState;
import com.cardconnect.consumersdk.swiper.enums.SwiperError;
import com.cardconnect.consumersdk.swiper.enums.SwiperType;
import com.cardconnect.consumersdk.views.CCConsumerCreditCardNumberEditText;
import com.cardconnect.consumersdk.views.CCConsumerCvvEditText;
import com.cardconnect.consumersdk.views.CCConsumerExpirationDateEditText;
import com.cardconnect.consumersdk.views.CCConsumerUiTextChangeListener;

public class CustomFlowActivity extends BaseActivity {
    private static final String TAG = CustomFlowActivity.class.getName();
    private static final int PERMISSIONS_REQUEST_RECORD_AUDIO = 1;
    private SwiperController mSwiperController;
    private CCConsumerCreditCardNumberEditText mCardNumberEditText;
    private CCConsumerExpirationDateEditText mExpirationDateEditText;
    private CCConsumerCvvEditText mCvvEditText;
    private EditText mPostalCodeEditText;
    private CCConsumerCardInfo mCCConsumerCardInfo;
    private TextView mConnectionStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_flow);
        setupToolBar();
        mCardNumberEditText = (CCConsumerCreditCardNumberEditText)findViewById(R.id.text_edit_credit_card_number);
        mExpirationDateEditText =
                (CCConsumerExpirationDateEditText)findViewById(R.id.text_edit_credit_card_expiration_date);
        mCvvEditText = (CCConsumerCvvEditText)findViewById(R.id.text_edit_credit_card_cvv);
        mPostalCodeEditText = (EditText)findViewById(R.id.text_edit_credit_card_postal_code);
        mConnectionStatus = (TextView)findViewById(R.id.text_view_connection_status);
        mCCConsumerCardInfo = new CCConsumerCardInfo();
        mCardNumberEditText.setCreditCardTextChangeListener(
                new CCConsumerUiTextChangeListener() {
                    @Override
                    public void onTextChanged() {
                        // This callback will be used for displaying custom UI showing validation completion
                        if (!mCardNumberEditText.isCardNumberValid() && mCardNumberEditText.getText().length() != 0) {
                            mCardNumberEditText.setError(getString(R.string.card_not_valid));
                        } else {
                            mCardNumberEditText.setError(null);
                        }
                    }
                });

        mExpirationDateEditText.setExpirationDateTextChangeListener(new CCConsumerUiTextChangeListener() {
            @Override
            public void onTextChanged() {
                // This callback will be used for displaying custom UI showing validation completion
                if (!mExpirationDateEditText.isExpirationDateValid() &&
                        mExpirationDateEditText.getText().length() != 0) {
                    mExpirationDateEditText.setError(getString(R.string.date_not_valid));
                } else {
                    mExpirationDateEditText.setError(null);
                }
            }
        });

        mCvvEditText.setCvvTextChangeListener(new CCConsumerUiTextChangeListener() {
            @Override
            public void onTextChanged() {
                // This callback will be used for displaying custom UI showing validation completion
                if (!mCvvEditText.isCvvCodeValid() && mCvvEditText.getText().length() != 0) {
                    mCvvEditText.setError(getString(R.string.cvv_not_valid));
                } else {
                    mCvvEditText.setError(null);
                }
            }
        });

        setupTabMaskOptions();
        // Request android permissions for Swiper
        requestRecordAudioPermission();
    }

    private void setupTabMaskOptions() {
        TabLayout maskOptionsTabLayout = (TabLayout)findViewById(R.id.tab_layout_mask_options);
        maskOptionsTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        mCardNumberEditText.setCCConsumerMaskFormat(CCConsumerMaskFormat.LAST_FOUR);
                        break;
                    case 1:
                        mCardNumberEditText.setCCConsumerMaskFormat(CCConsumerMaskFormat.FIRST_LAST_FOUR);
                        break;
                    case 2:
                        mCardNumberEditText.setCCConsumerMaskFormat(CCConsumerMaskFormat.CARD_MASK_LAST_FOUR);
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // Unused
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // Update default preselection
                if (tab.getPosition() == 0) {
                    mCardNumberEditText.setCCConsumerMaskFormat(CCConsumerMaskFormat.LAST_FOUR);
                }
            }
        });
        TabLayout.Tab selectedTab = maskOptionsTabLayout.getTabAt(0);
        if (selectedTab != null) {
            selectedTab.select();
        }
    }

    public void generateToken(View view) {

        // If using Custom UI Card object needs to be populated from within the component.
        mCardNumberEditText.setCardNumberOnCardInfo(mCCConsumerCardInfo);
        mExpirationDateEditText.setExpirationDateOnCardInfo(mCCConsumerCardInfo);
        mCvvEditText.setCvvCodeOnCardInfo(mCCConsumerCardInfo);
        if (!TextUtils.isEmpty(mPostalCodeEditText.getText())) {
            mCCConsumerCardInfo.setPostalCode(mPostalCodeEditText.getText().toString());
        }

        if (!mCCConsumerCardInfo.isCardValid()) {
            showErrorDialog(getString(R.string.card_invalid));
            return;
        }

        showProgressDialog();

        MainApp.getConsumerApi().generateAccountForCard(mCCConsumerCardInfo, new CCConsumerTokenCallback() {
            @Override
            public void onCCConsumerTokenResponseError(CCConsumerError error) {
                dismissProgressDialog();
                showErrorDialog(error.getResponseMessage());
            }

            @Override
            public void onCCConsumerTokenResponse(CCConsumerAccount consumerAccount) {
                dismissProgressDialog();
                showSnackBarMessage(consumerAccount.getToken());
                mCardNumberEditText.getText().clear();
                mCvvEditText.getText().clear();
                mExpirationDateEditText.getText().clear();
                mPostalCodeEditText.getText().clear();
            }
        });
    }

    private void initSwiperForTokenGeneration() {
        mSwiperController = new CCSwiperControllerFactory().create(this, SwiperType.BBPosDevice,
                new SwiperControllerListener() {
                    @Override
                    public void onTokenGenerated(CCConsumerAccount account, CCConsumerError error) {
                        dismissProgressDialog();
                        if (error != null) {
                            showErrorDialog(error.getResponseMessage());
                        } else {
                            showSnackBarMessage(account.getToken());
                        }
                    }

                    @Override
                    public void onError(SwiperError swipeError) {
                        Log.d(TAG, swipeError.toString());
                    }

                    @Override
                    public void onSwiperReadyForCard() {
                        mConnectionStatus.setText(R.string.ready_for_swipe);
                        Log.d(TAG, "Swiper ready for card");
                    }

                    @Override
                    public void onSwiperConnected() {
                        mConnectionStatus.setText(R.string.connected);
                        Log.d(TAG, "Swiper connected");
                    }

                    @Override
                    public void onSwiperDisconnected() {
                        mConnectionStatus.setText(R.string.disconnected);
                        Log.d(TAG, "Swiper disconnected");
                    }

                    @Override
                    public void onBatteryState(BatteryState batteryState) {
                        Log.d(TAG, batteryState.toString());
                    }

                    @Override
                    public void onStartTokenGeneration() {
                        Log.d(TAG, "Token Generation started.");
                        showProgressDialog();
                    }
                });
    }

    private void showSnackBarMessage(String message) {
        Snackbar.make(findViewById(android.R.id.content), getString(R.string.token_generated_format,
                message), Snackbar.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mSwiperController != null) {
            mSwiperController.release();
        }
    }

    private void requestRecordAudioPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.RECORD_AUDIO},
                    PERMISSIONS_REQUEST_RECORD_AUDIO);
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
