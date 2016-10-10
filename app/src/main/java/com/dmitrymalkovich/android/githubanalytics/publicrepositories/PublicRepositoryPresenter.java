package com.dmitrymalkovich.android.githubanalytics.publicrepositories;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;

import com.dmitrymalkovich.android.githubanalytics.data.source.GithubDataSource;
import com.dmitrymalkovich.android.githubanalytics.data.source.GithubRepository;
import com.dmitrymalkovich.android.githubanalytics.data.source.LoaderProvider;

import static com.google.common.base.Preconditions.checkNotNull;

public class PublicRepositoryPresenter implements PublicRepositoriesContract.Presenter,
        LoaderManager.LoaderCallbacks<Cursor>, GithubRepository.LoadDataCallback {

    @SuppressWarnings("unused")
    private static String LOG_TAG = PublicRepositoryPresenter.class.getSimpleName();
    private static final int REPOSITORIES_LOADER = 2;

    @SuppressWarnings("unused")
    @NonNull
    private final LoaderProvider mLoaderProvider;

    @NonNull
    private final LoaderManager mLoaderManager;

    @NonNull
    private GithubRepository mGithubRepository;

    @NonNull
    private PublicRepositoriesContract.View mPublicRepositoriesView;

    public PublicRepositoryPresenter(@NonNull GithubRepository githubRepository,
                                     @NonNull PublicRepositoriesContract.View view,
                                     @NonNull LoaderProvider loaderProvider,
                                     @NonNull LoaderManager loaderManager) {
        mGithubRepository = checkNotNull(githubRepository);
        mPublicRepositoriesView = checkNotNull(view);
        mLoaderProvider = checkNotNull(loaderProvider);
        mLoaderManager = checkNotNull(loaderManager, "loaderManager cannot be null!");
        mPublicRepositoriesView.setPresenter(this);
    }

    @Override
    public void start() {
        mPublicRepositoriesView.setLoadingIndicator(true);
        showRepositories();
    }

    @Override
    public void onRefresh() {
        mGithubRepository.getRepositories(new GithubDataSource.GetRepositoriesCallback() {
            @Override
            public void onRepositoriesLoaded() {
                mPublicRepositoriesView.setLoadingIndicator(false);
            }

            @Override
            public void onDataNotAvailable() {
                mPublicRepositoriesView.setLoadingIndicator(false);
            }
        });
    }

    @Override
    public void onDataLoaded(Cursor data) {
        mPublicRepositoriesView.setLoadingIndicator(false);
        mPublicRepositoriesView.showRepositories(data);
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
        return mLoaderProvider.createRepositoryLoader();
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
        mLoaderManager.initLoader(REPOSITORIES_LOADER,
                null,
                PublicRepositoryPresenter.this);
    }
}
