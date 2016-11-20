package com.dmitrymalkovich.android.githubanalytics;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

public class GithubAnalyticsApplication extends Application {

    @SuppressLint("StaticFieldLeak")
    private static Context sContext;

    @Override
    protected void attachBaseContext(Context context) {
        super.attachBaseContext(context);
        MultiDex.install(this);
        sContext = context;
    }

    public static Context context() {
        return sContext;
    }
}
