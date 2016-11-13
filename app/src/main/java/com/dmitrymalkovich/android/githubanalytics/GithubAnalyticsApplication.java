package com.dmitrymalkovich.android.githubanalytics;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

public class GithubAnalyticsApplication extends Application {

    @Override
    protected void attachBaseContext(Context context) {
        super.attachBaseContext(context);
        MultiDex.install(this);
    }
}
