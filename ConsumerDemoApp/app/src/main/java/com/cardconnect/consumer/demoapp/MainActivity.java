package com.cardconnect.consumer.demoapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.cardconnect.consumer.demoapp.apicommunication.ApiBridgeImpl;
import com.cardconnect.consumer.demoapp.swiperinitialization.SwiperTestActivity;
import com.cardconnect.consumersdk.androidpay.CCConsumerAndroidPayActivity;
import com.cardconnect.consumersdk.androidpay.CCConsumerAndroidPayConfiguration;
import com.cardconnect.consumersdk.domain.CCConsumerAccount;
import com.cardconnect.consumersdk.domain.response.CCConsumerApiAndroidPayTokenResponse;
import com.cardconnect.consumersdk.views.payment.accounts.PaymentAccountsActivity;

import java.util.UUID;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupToolBar();
        findViewById(R.id.button_custom_flow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startCustomFlowActivity();
            }
        });
        findViewById(R.id.button_integrated_flow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startIntegratedFlowActivity();
            }
        });
        findViewById(R.id.button_show_activity_with_fragment).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent swiperActivity = new Intent(MainActivity.this, SwiperTestActivity.class);
                startActivity(swiperActivity);
            }
        });
    }

    public void startCustomFlowActivity() {
        Intent intent = new Intent(this, CustomFlowActivity.class);
        startActivity(intent);
    }

    public void startIntegratedFlowActivity() {
        ApiBridgeImpl apiBridgeImpl = new ApiBridgeImpl();
        Intent intent = new Intent(this, PaymentAccountsActivity.class);
        intent.putExtra(PaymentAccountsActivity.API_BRIDGE_IMPL_KEY, apiBridgeImpl);

        // Android Pay Integration
        CCConsumerAndroidPayConfiguration.getInstance().setAndroidPayUiEnabled(true);
        CCConsumerAndroidPayConfiguration.getInstance().setMerchantName("Merchant Test Name");
        CCConsumerAndroidPayConfiguration.getInstance().setMerchantTransactionId(UUID.randomUUID().toString());
        intent.putExtra(CCConsumerAndroidPayActivity.ANDROID_PAY_TOTAL_AMOUNT_KEY, "5.00");

        startActivityForResult(intent, PaymentAccountsActivity.PAYMENT_ACTIVITY_REQUEST_CODE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_custom_flow:
                startCustomFlowActivity();
                break;
            case R.id.button_integrated_flow:
                startIntegratedFlowActivity();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Get Account selected by integrated UI flow
        if (requestCode == PaymentAccountsActivity.PAYMENT_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {

            // Check for Android Pay selected Payment
            if (data.hasExtra(PaymentAccountsActivity.ANDROID_PAY_TOKEN_RESPONSE_KEY)) {
                CCConsumerApiAndroidPayTokenResponse response =
                        data.getParcelableExtra(PaymentAccountsActivity.ANDROID_PAY_TOKEN_RESPONSE_KEY);
                Toast.makeText(this, getString(R.string.selected_android_pay_account_text_format,
                        response.getGoogleTransactionId(), response.getToken()), Toast.LENGTH_SHORT).show();
            } else {
                // Example of displaying account selected
                CCConsumerAccount selectedAccount =
                        data.getParcelableExtra(PaymentAccountsActivity.PAYMENT_ACTIVITY_ACCOUNT_SELECTED);
                Toast.makeText(this, getString(R.string.selected_account_text_format, selectedAccount.getAccountType()
                        .toString(), selectedAccount.getLast4()), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
