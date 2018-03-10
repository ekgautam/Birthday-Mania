package com.example.ek.birthdaymania;

/**
 * Created by Ek on 14-08-2016.
 */

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.instamojo.android.Instamojo;

public class MyApplication extends Application {

    private static MyApplication mInstance;

    @Override
    public void onCreate() {
        super.onCreate();

        mInstance = this;
        Instamojo.initialize(this);
    }

    public static synchronized MyApplication getInstance() {
        return mInstance;
    }

    public void setConnectivityListener(ConnectivityReceiver.ConnectivityReceiverListener listener) {
        ConnectivityReceiver.connectivityReceiverListener = listener;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
