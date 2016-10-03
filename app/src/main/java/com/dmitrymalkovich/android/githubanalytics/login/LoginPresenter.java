package com.dmitrymalkovich.android.githubanalytics.login;

import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;

import com.dmitrymalkovich.android.githubanalytics.data.source.GithubRepository;
import com.dmitrymalkovich.android.githubanalytics.data.source.LoaderProvider;

import static com.google.common.base.Preconditions.checkNotNull;

public class LoginPresenter implements LoginContract.Presenter {

    @NonNull
    private final LoaderProvider mLoaderProvider;

    @NonNull
    private final LoaderManager mLoaderManager;

    @NonNull
    private GithubRepository mGithubRepository;

    @NonNull
    private LoginContract.View mLoginView;

    public LoginPresenter(@NonNull GithubRepository tasksRepository,
                          @NonNull LoginContract.View addTaskView,
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
