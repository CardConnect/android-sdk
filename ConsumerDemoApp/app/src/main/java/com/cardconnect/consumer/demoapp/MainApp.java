package com.cardconnect.consumer.demoapp;

import android.app.Application;

import com.cardconnect.consumersdk.CCConsumer;
import com.cardconnect.consumersdk.network.CCConsumerApi;

public class MainApp extends Application {

    private static MainApp sAppContext;

    public static CCConsumerApi getConsumerApi() {
        return CCConsumer.getInstance().getApi();
    }

    public static MainApp get() {
        return sAppContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sAppContext = (MainApp)getApplicationContext();
        setupConsumerApi();
    }

    /**
     * Initial Configuration for Consumer Api
     */
    private void setupConsumerApi() {
        CCConsumer.getInstance().getApi().setEndPoint("https://qa.cardconnect.com:443/cardsecure/cs");
        CCConsumer.getInstance().getApi().setDebugEnabled(true);
    }
}
