package com.dmitrymalkovich.android.githubanalytics.data.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * Define a Service that returns an IBinder for the
 * sync adapter class, allowing the sync adapter framework to call
 * onPerformSync().
 * <p>
 * https://developer.android.com/training/sync-adapters/creating-sync-adapter.html
 */
public class SyncService extends Service {
    private static final Object sSyncAdapterLock = new Object();
    private static String LOG_TAG = SyncAdapter.class.getSimpleName();
    private static SyncAdapter sSyncAdapter = null;

    @Override
    public void onCreate() {
        Log.d(LOG_TAG, "SyncService.onCreate");
        synchronized (sSyncAdapterLock) {
            if (sSyncAdapter == null) {
                sSyncAdapter = new SyncAdapter(getApplicationContext(), true);
            }
        }
    }

    /**
     * Return an object that allows the system to invoke
     * the sync adapter.
     */
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(LOG_TAG, "SyncService.onBind");
        return sSyncAdapter.getSyncAdapterBinder();
    }
}
