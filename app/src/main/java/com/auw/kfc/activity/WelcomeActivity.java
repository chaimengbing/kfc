package com.auw.kfc.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.auw.kfc.R;

public class WelcomeActivity extends BaseActivity {

    private static final long DELAY_TIME = 3000L;
    private Handler handler=new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        redirectByTime();
    }
    private void redirectByTime() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(WelcomeActivity.this, HomeActivity.class));
                finish();
            }
        }, DELAY_TIME);
    }
}
