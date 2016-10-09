package com.dmitrymalkovich.android.githubanalytics.data.sync;


import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class SyncAuthenticatorService extends Service {
    private AuthenticatorService mAuthenticator;

    @Override
    public void onCreate() {
        mAuthenticator = new AuthenticatorService(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mAuthenticator.getIBinder();
    }
}
