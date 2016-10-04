package com.dmitrymalkovich.android.githubanalytics.basicauthorization;

import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;

import com.dmitrymalkovich.android.githubanalytics.data.source.GithubRepository;
import com.dmitrymalkovich.android.githubanalytics.data.source.LoaderProvider;

import static com.google.common.base.Preconditions.checkNotNull;

public class BasicAuthorizationPresenter implements BasicAuthorizationContract.Presenter {

    @NonNull
    private final LoaderProvider mLoaderProvider;

    @NonNull
    private final LoaderManager mLoaderManager;

    @NonNull
    private GithubRepository mGithubRepository;

    @NonNull
    private BasicAuthorizationContract.View mLoginView;

    public BasicAuthorizationPresenter(@NonNull GithubRepository tasksRepository,
                                       @NonNull BasicAuthorizationContract.View addTaskView,
                                       @NonNull LoaderProvider loaderProvider,
                                       @NonNull LoaderManager loaderManager) {
        mGithubRepository = checkNotNull(tasksRepository);
        mLoginView = checkNotNull(addTaskView);
        mLoaderProvider = checkNotNull(loaderProvider);
        mLoaderManager = checkNotNull(loaderManager, "loaderManager cannot be null!");

        mLoginView.setPresenter(this);
    }

    @Override
    public void start() {

    }

    @Override
    public void login(String username, String password) {
        mGithubRepository.login(username, password);
    }
}
