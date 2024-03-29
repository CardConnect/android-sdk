package com.cardconnect.consumer.demoapp.swiperinitialization;

import android.os.Bundle;
import android.view.View;

import com.cardconnect.consumer.demoapp.BaseActivity;
import com.cardconnect.consumer.demoapp.R;

public class SwiperTestActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swiper_test);
        setupToolBar();
        findViewById(R.id.button_show_fragment_by_action).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getSupportFragmentManager().findFragmentByTag(SwiperTestFragment.TAG) == null) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.frame_layout_container, new SwiperTestFragment(), SwiperTestFragment.TAG)
                            .addToBackStack(SwiperTestFragment.TAG).commit();
                }
            }
        });
    }
}
