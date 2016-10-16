package com.dmitrymalkovich.android.githubanalytics.trendingrepository;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;

import com.dmitrymalkovich.android.githubanalytics.data.source.GithubDataSource;
import com.dmitrymalkovich.android.githubanalytics.data.source.GithubRepository;
import com.dmitrymalkovich.android.githubanalytics.data.source.LoaderProvider;

import org.eclipse.egit.github.core.Repository;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class TrendingRepositoryPresenter implements TrendingRepositoryContract.Presenter,
        LoaderManager.LoaderCallbacks<Cursor>, GithubRepository.LoadDataCallback {

    @SuppressWarnings("unused")
    private static String LOG_TAG = TrendingRepositoryPresenter.class.getSimpleName();
    private static final int TRENDING_LOADER = 3;

    @SuppressWarnings("unused")
    @NonNull
    private final LoaderProvider mLoaderProvider;

    @NonNull
    private final LoaderManager mLoaderManager;

    @NonNull
    private GithubRepository mGithubRepository;

    @NonNull
    private TrendingRepositoryContract.View mView;

    public TrendingRepositoryPresenter(@NonNull GithubRepository githubRepository,
                                       @NonNull TrendingRepositoryContract.View view,
                                       @NonNull LoaderProvider loaderProvider,
                                       @NonNull LoaderManager loaderManager) {
        mGithubRepository = checkNotNull(githubRepository);
        mView = checkNotNull(view);
        mLoaderProvider = checkNotNull(loaderProvider);
        mLoaderManager = checkNotNull(loaderManager, "loaderManager cannot be null!");
        mView.setPresenter(this);
    }

    @Override
    public void start() {
        mView.setLoadingIndicator(true);
        showRepositories();
    }

    @Override
    public void onRefresh() {
        mGithubRepository.getRepositories(new GithubDataSource.GetRepositoriesCallback() {
            @Override
            public void onRepositoriesLoaded(List<Repository> repositoryList) {
                mView.setLoadingIndicator(false);
            }

            @Override
            public void onDataNotAvailable() {
                mView.setLoadingIndicator(false);
            }
        });
    }

    @Override
    public void onDataLoaded(Cursor data) {
        mView.setLoadingIndicator(false);
        mView.showRepositories(data);
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
        return mLoaderProvider.createTrendingLoader();
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
        mLoaderManager.initLoader(TRENDING_LOADER,
                null,
                TrendingRepositoryPresenter.this);
    }
}
