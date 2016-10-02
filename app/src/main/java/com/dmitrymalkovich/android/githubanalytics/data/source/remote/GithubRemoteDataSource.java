package com.dmitrymalkovich.android.githubanalytics.data.source.remote;

import com.dmitrymalkovich.android.githubanalytics.data.source.GithubDataSource;

public class GithubRemoteDataSource implements GithubDataSource {

    private static GithubRemoteDataSource INSTANCE;

    private GithubRemoteDataSource() {
    }

    public static GithubRemoteDataSource getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new GithubRemoteDataSource();
        }
        return INSTANCE;
    }
}
