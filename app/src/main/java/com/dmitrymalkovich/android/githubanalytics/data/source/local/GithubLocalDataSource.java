package com.dmitrymalkovich.android.githubanalytics.data.source.local;

import android.content.ContentResolver;
import android.support.annotation.NonNull;

import com.dmitrymalkovich.android.githubanalytics.data.source.GithubDataSource;

import static com.google.common.base.Preconditions.checkNotNull;

public class GithubLocalDataSource implements GithubDataSource {

    private static GithubLocalDataSource INSTANCE;
    private ContentResolver mContentResolver;

    public static GithubLocalDataSource getInstance(ContentResolver contentResolver) {
        if (INSTANCE == null) {
            INSTANCE = new GithubLocalDataSource(contentResolver);
        }
        return INSTANCE;
    }

    private GithubLocalDataSource(@NonNull ContentResolver contentResolver) {
        checkNotNull(contentResolver);
        mContentResolver = contentResolver;
    }

    @Override
    public void login(String login, String password) {
    }

    @Override
    public void getRepositories() {
    }
}
