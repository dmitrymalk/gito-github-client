package com.dmitrymalkovich.android.githubanalytics.dashboard;

import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;

import com.dmitrymalkovich.android.githubanalytics.data.source.GithubRepository;
import com.dmitrymalkovich.android.githubanalytics.data.source.LoaderProvider;

import static com.google.common.base.Preconditions.checkNotNull;

public class DashboardPresenter implements DashboardContract.Presenter {

    private static String LOG_TAG = DashboardPresenter.class.getSimpleName();

    @NonNull
    private final LoaderProvider mLoaderProvider;

    @NonNull
    private final LoaderManager mLoaderManager;

    @NonNull
    private GithubRepository mGithubRepository;

    @NonNull
    private DashboardContract.View mWelcomeView;

    public DashboardPresenter(@NonNull GithubRepository githubRepository,
                       @NonNull DashboardContract.View view,
                       @NonNull LoaderProvider loaderProvider,
                       @NonNull LoaderManager loaderManager) {
        mGithubRepository = checkNotNull(githubRepository);
        mWelcomeView = checkNotNull(view);
        mLoaderProvider = checkNotNull(loaderProvider);
        mLoaderManager = checkNotNull(loaderManager, "loaderManager cannot be null!");

        mWelcomeView.setPresenter(this);
    }

    @Override
    public void start() {
    }
}
