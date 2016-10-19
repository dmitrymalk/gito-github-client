package com.dmitrymalkovich.android.githubanalytics.trendingrepository;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;

import com.dmitrymalkovich.android.githubanalytics.data.source.GithubDataSource;
import com.dmitrymalkovich.android.githubanalytics.data.source.GithubRepository;
import com.dmitrymalkovich.android.githubanalytics.data.source.LoaderProvider;
import com.dmitrymalkovich.android.githubanalytics.data.source.remote.gson.ResponseTrending;

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
        showTrendingRepositories();
    }

    @Override
    public void onRefresh() {
        mGithubRepository.getTrendingRepositories("day", "java",
                new GithubDataSource.GetTrendingRepositories() {
            @Override
            public void onTrendingRepositoriesLoaded(List<ResponseTrending> responseTrendingList) {
                mView.setRefreshIndicator(false);
                mView.setLoadingIndicator(false);
            }

            @Override
            public void onDataNotAvailable() {
                TrendingRepositoryPresenter.this.onDataNotAvailable();
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
        mView.setRefreshIndicator(false);
        mView.setLoadingIndicator(false);
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

    private void showTrendingRepositories() {
        mLoaderManager.initLoader(TRENDING_LOADER,
                null,
                TrendingRepositoryPresenter.this);
        mGithubRepository.getTrendingRepositories("day", "java",
                new GithubDataSource.GetTrendingRepositories() {
                    @Override
                    public void onTrendingRepositoriesLoaded(List<ResponseTrending> responseTrendingList) {
                        mView.setLoadingIndicator(false);
                    }

                    @Override
                    public void onDataNotAvailable() {

                    }
                });
    }
}
