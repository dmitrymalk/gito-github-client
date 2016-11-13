package com.dmitrymalkovich.android.githubanalytics.data.sync;

import android.content.SharedPreferences;

public class SyncSettings {

    private static final long SYNC_INTERVAL = 1000 * 60 * 30;
    private SharedPreferences mPreferences;

    public SyncSettings(SharedPreferences preferences) {
        mPreferences = preferences;
    }

    public boolean isSynced(String key) {
        long lastSyncTimeMillis = mPreferences.getLong(key, 0);
        return lastSyncTimeMillis != 0 &&
                lastSyncTimeMillis - System.currentTimeMillis() < SYNC_INTERVAL;
    }

    public void synced(String key) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putLong(key, System.currentTimeMillis());
        editor.apply();
    }
}
