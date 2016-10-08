package com.dmitrymalkovich.android.githubanalytics.data.source.local;

import android.content.ContentResolver;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import com.dmitrymalkovich.android.githubanalytics.data.source.GithubDataSource;

import static com.google.common.base.Preconditions.checkNotNull;

public class GithubLocalDataSource implements GithubDataSource {

    private static final String PREFERENCES_TOKEN = "PREFERENCES_TOKEN";
    private static GithubLocalDataSource INSTANCE;
    private ContentResolver mContentResolver;
    private SharedPreferences mPreferences;

    public static GithubLocalDataSource getInstance(ContentResolver contentResolver,
                                                    SharedPreferences preferences) {
        if (INSTANCE == null) {
            INSTANCE = new GithubLocalDataSource(contentResolver, preferences);
        }
        return INSTANCE;
    }

    private GithubLocalDataSource(@NonNull ContentResolver contentResolver,
                                  SharedPreferences preferences) {
        checkNotNull(contentResolver);
        checkNotNull(preferences);
        mContentResolver = contentResolver;
        mPreferences = preferences;
    }

    @Override
    public void login(String login, String password) {
    }

    @Override
    public void getRepositories() {
    }

    @Override
    public void requestTokenFromCode(String code, RequestTokenFromCodeCallback callback) {
    }

    @Override
    public void saveToken(String token) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putString(PREFERENCES_TOKEN, token);
        editor.apply();
    }

    @Override
    public String getToken() {
        return mPreferences.getString(PREFERENCES_TOKEN, null);
    }
}
