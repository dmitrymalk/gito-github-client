package com.dmitrymalkovich.android.githubanalytics.data.source;

import android.content.Context;
import android.support.annotation.NonNull;

import com.dmitrymalkovich.android.githubanalytics.data.source.local.GithubLocalDataSource;
import com.dmitrymalkovich.android.githubanalytics.data.source.remote.GithubRemoteDataSource;

import static com.google.common.base.Preconditions.checkNotNull;

public class Injection {

    public static GithubRepository provideGithubRepository(@NonNull Context context) {
        checkNotNull(context);
        return GithubRepository.getInstance(GithubRemoteDataSource.getInstance(), provideLocalDataSource(context));
    }

    private static GithubLocalDataSource provideLocalDataSource(@NonNull Context context) {
        checkNotNull(context);
        return GithubLocalDataSource.getInstance(context.getContentResolver(),
                context.getSharedPreferences("GITHUB_ANALYTICS_PREFERENCES", Context.MODE_PRIVATE));
    }
}
