package com.dmitrymalkovich.android.githubanalytics.traffic;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;

import com.dmitrymalkovich.android.githubanalytics.data.source.GithubRepository;
import com.dmitrymalkovich.android.githubanalytics.data.source.local.LoaderProvider;

import static com.google.common.base.Preconditions.checkNotNull;

class TrafficPresenter implements TrafficContract.Presenter,
        LoaderManager.LoaderCallbacks<Cursor>, GithubRepository.LoadDataCallback {

    @SuppressWarnings("unused")
    private static String LOG_TAG = TrafficPresenter.class.getSimpleName();
    private static final int TRAFFIC_LOADER = 5;

    @SuppressWarnings("unused")
    @NonNull
    private final LoaderProvider mLoaderProvider;

    @NonNull
    private final LoaderManager mLoaderManager;

    @NonNull
    private GithubRepository mGithubRepository;

    @NonNull
    private TrafficContract.View mView;
    private long mRepositoryId;

    public TrafficPresenter(@NonNull GithubRepository githubRepository,
                            @NonNull TrafficContract.View view,
                            @NonNull LoaderProvider loaderProvider,
                            @NonNull LoaderManager loaderManager) {
        mGithubRepository = checkNotNull(githubRepository);
        mView = checkNotNull(view);
        mLoaderProvider = checkNotNull(loaderProvider);
        mLoaderManager = checkNotNull(loaderManager, "loaderManager cannot be null!");
        mView.setPresenter(this);
    }

    @Override
    public void start(Bundle savedInstanceState, long repositoryId) {
        mView.setLoadingIndicator(true);
        mRepositoryId = repositoryId;
        if (savedInstanceState == null) {
            showRepositories();
        } else {
            mLoaderManager.initLoader(TRAFFIC_LOADER, null, TrafficPresenter.this);
        }
    }

    @Override
    public void start(Bundle savedInstanceState) {
    }

    @Override
    public void onDataLoaded(Cursor data) {
    }

    @Override
    public void onDataEmpty() {
    }

    @Override
    public void onDataNotAvailable() {
    }

    @Override
    public void onDataReset() {
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return mLoaderProvider.createTrafficLoader(mRepositoryId);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null) {
            if (data.moveToLast()) {
                onDataLoaded(data);
            } else {
                onDataEmpty();
            }
        } else {
            onDataNotAvailable();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        onDataReset();
    }

    private void showRepositories() {
        mLoaderManager.initLoader(TRAFFIC_LOADER,
                null,
                TrafficPresenter.this);
    }

    @Override
    public void onRefresh() {
    }
}
