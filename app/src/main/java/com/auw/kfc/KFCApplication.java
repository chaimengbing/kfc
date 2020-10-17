package com.auw.kfc;

import android.app.Application;

public class KFCApplication extends Application {

    private static KFCApplication mInstance = null;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    }

    public static KFCApplication getInstance() {
        return mInstance;
    }
}
