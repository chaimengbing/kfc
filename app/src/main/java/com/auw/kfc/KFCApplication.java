package com.auw.kfc;

import android.app.Application;

import androidx.multidex.MultiDex;

public class KFCApplication extends Application {

    private static KFCApplication mInstance = null;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        MultiDex.install( this );
    }

    public static KFCApplication getInstance() {
        return mInstance;
    }
}
