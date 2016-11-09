package com.dmitrymalkovich.android.githubanalytics.trending;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;

import com.dmitrymalkovich.android.githubanalytics.R;
import com.dmitrymalkovich.android.githubanalytics.data.source.GithubDataSource;
import com.dmitrymalkovich.android.githubanalytics.data.source.GithubRepository;
import com.dmitrymalkovich.android.githubanalytics.data.source.local.LoaderProvider;
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
    public void start(Bundle savedInstanceState) {
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

        mLoaderManager.restartLoader(TRENDING_LOADER, null, TrendingRepositoryPresenter.this);

        showTrendingRepositories();
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

        mLoaderManager.restartLoader(TRENDING_LOADER, null, TrendingRepositoryPresenter.this);

        showTrendingRepositories();
    }

    @Override
    public void onRefresh() {
        final String period = mGithubRepository.getDefaultPeriodForTrending();
        final String language = mGithubRepository.getDefaultLanguageForTrending();

        mGithubRepository.getTrendingRepositories(period, language,
                new GithubDataSource.GetTrendingRepositories() {
                    @Override
                    public void onTrendingRepositoriesLoaded(List<ResponseTrending> repositories,
                                                             String language, String period) {
                        String currentPeriod = mGithubRepository.getDefaultPeriodForTrending();
                        String currentLanguage = mGithubRepository.getDefaultLanguageForTrending();

                        if (currentLanguage.equals(language)
                                && currentPeriod.equals(period)) {
                            mView.setRefreshIndicator(false);
                        }
                    }

                    @Override
                    public void onDataNotAvailable() {
                        String currentPeriod = mGithubRepository.getDefaultPeriodForTrending();
                        String currentLanguage = mGithubRepository.getDefaultLanguageForTrending();

                        if (currentLanguage.equals(language)
                                && currentPeriod.equals(period)) {
                            mView.setRefreshIndicator(false);
                            TrendingRepositoryPresenter.this.onDataNotAvailable(TRENDING_LOADER);
                        }
                    }
                }, false);
    }

    @Override
    public void onDataLoaded(Cursor data, int id) {
        mView.showRepositories(data);
        mView.setEmptyState(false);
    }

    @Override
    public void onDataEmpty(int id) {
        mView.showRepositories(null);
        mView.setEmptyState(true);
    }

    @Override
    public void onDataNotAvailable(int id) {
        onDataEmpty(TRENDING_LOADER);
    }

    @Override
    public void onDataReset() {
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String period = mGithubRepository.getDefaultPeriodForTrending();
        String language = mGithubRepository.getDefaultLanguageForTrending();
        return mLoaderProvider.createTrendingLoader(language, period);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null) {
            if (data.moveToLast()) {
                onDataLoaded(data, loader.getId());
            } else {
                onDataEmpty(loader.getId());
            }
        } else {
            onDataEmpty(loader.getId());
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        onDataReset();
    }

    @Override
    public boolean isLanguageSelected(String language) {
        return language.equals(mGithubRepository.getDefaultLanguageForTrending());
    }

    private void showTrendingRepositories() {
        final String period = mGithubRepository.getDefaultPeriodForTrending();
        final String language = mGithubRepository.getDefaultLanguageForTrending();

        mView.setLoadingIndicator(true);

        mGithubRepository.getTrendingRepositories(period, language,
                new GithubDataSource.GetTrendingRepositories() {
                    @Override
                    public void onTrendingRepositoriesLoaded(List<ResponseTrending> repositories,
                                                             String language, String period) {
                        String currentPeriod = mGithubRepository.getDefaultPeriodForTrending();
                        String currentLanguage = mGithubRepository.getDefaultLanguageForTrending();

                        if (currentLanguage.equals(language)
                                && currentPeriod.equals(period)) {
                            mView.setLoadingIndicator(false);
                        }
                    }

                    @Override
                    public void onDataNotAvailable() {
                        String currentPeriod = mGithubRepository.getDefaultPeriodForTrending();
                        String currentLanguage = mGithubRepository.getDefaultLanguageForTrending();

                        if (currentLanguage.equals(language)
                                && currentPeriod.equals(period)) {

                            mView.setLoadingIndicator(false);

                            TrendingRepositoryPresenter.this.onDataNotAvailable(TRENDING_LOADER);
                        }
                    }
                }, true);
    }
}
