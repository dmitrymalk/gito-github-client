package com.dmitrymalkovich.android.githubanalytics.trendingrepository;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;

import com.dmitrymalkovich.android.githubanalytics.R;
import com.dmitrymalkovich.android.githubanalytics.data.source.GithubDataSource;
import com.dmitrymalkovich.android.githubanalytics.data.source.GithubRepository;
import com.dmitrymalkovich.android.githubanalytics.data.source.LoaderProvider;
import com.dmitrymalkovich.android.githubanalytics.data.source.local.GithubLocalDataSource;
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

        String period = mGithubRepository.getDefaultPeriodForTrending();
        switch (period) {
            case GithubLocalDataSource.TRENDING_PERIOD_MONTHLY:
                mView.selectTab(2);
                break;
            case GithubLocalDataSource.TRENDING_PERIOD_WEEKLY:
                mView.selectTab(1);
                break;
            default:
            case GithubLocalDataSource.TRENDING_PERIOD_DAILY:
                mView.selectTab(0);
                break;
        }
    }

    @Override
    public String getTitle(Context context) {
        return context.getString(R.string.navigation_view_trending);
    }

    @Override
    public void changeLanguage(String language) {
        mGithubRepository.setDefaultLanguageForTrending(language);
        updateList();
    }

    @Override
    public void onTabSelected(int position) {
        switch (position) {
            case 0:
                mGithubRepository.setDefaultPeriodForTrending(
                        GithubLocalDataSource.TRENDING_PERIOD_DAILY);
                break;
            case 1:
                mGithubRepository.setDefaultPeriodForTrending(
                        GithubLocalDataSource.TRENDING_PERIOD_WEEKLY);
                break;
            case 2:
                mGithubRepository.setDefaultPeriodForTrending(
                        GithubLocalDataSource.TRENDING_PERIOD_MONTHLY);
                break;
        }
        updateList();
    }

    @Override
    public void onRefresh() {
        mGithubRepository.getTrendingRepositories(mGithubRepository.getDefaultPeriodForTrending(),
                mGithubRepository.getDefaultLanguageForTrending(),
                new GithubDataSource.GetTrendingRepositories() {
            @Override
            public void onTrendingRepositoriesLoaded(List<ResponseTrending> responseTrendingList) {
                mView.setRefreshIndicator(false);
                mView.setLoadingIndicator(false);
                mLoaderManager.restartLoader(TRENDING_LOADER, null, TrendingRepositoryPresenter.this);
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
        return mLoaderProvider.createTrendingLoader(mGithubRepository.getDefaultLanguageForTrending(),
                mGithubRepository.getDefaultPeriodForTrending());
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

    private void updateList() {
        mView.showRepositories(null);
        mView.setLoadingIndicator(true);
        mLoaderManager.restartLoader(TRENDING_LOADER, null, TrendingRepositoryPresenter.this);
    }
}
