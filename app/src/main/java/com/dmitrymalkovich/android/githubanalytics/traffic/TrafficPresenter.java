package com.dmitrymalkovich.android.githubanalytics.traffic;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;

import com.dmitrymalkovich.android.githubanalytics.dashboard.DashboardPresenter;
import com.dmitrymalkovich.android.githubanalytics.data.source.GithubDataSource;
import com.dmitrymalkovich.android.githubanalytics.data.source.GithubRepository;
import com.dmitrymalkovich.android.githubanalytics.data.source.local.LoaderProvider;

import org.eclipse.egit.github.core.Repository;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

class TrafficPresenter implements TrafficContract.Presenter,
        LoaderManager.LoaderCallbacks<Cursor>, GithubRepository.LoadDataCallback {

    @SuppressWarnings("unused")
    private static String LOG_TAG = TrafficPresenter.class.getSimpleName();
    private static final int TRAFFIC_LOADER = 5;
    private static final int VIEWS_LOADER = 6;
    private static final int CLONES_LOADER = 7;
    private static final int REFERENCES_LOADER = 8;

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

        mLoaderManager.initLoader(TRAFFIC_LOADER, null, TrafficPresenter.this);
        mLoaderManager.initLoader(VIEWS_LOADER, null, TrafficPresenter.this);
        mLoaderManager.initLoader(CLONES_LOADER, null, TrafficPresenter.this);
        mLoaderManager.initLoader(REFERENCES_LOADER, null, TrafficPresenter.this);

        mGithubRepository.getRepositoriesWithAdditionalInfo(repositoryId,
                new GithubDataSource.GetRepositoriesCallback() {
                    @Override
                    public void onRepositoriesLoaded(List<Repository> repositoryList) {
                    }

                    @Override
                    public void onDataNotAvailable() {
                    }
                }, false);
    }

    @Override
    public void start(Bundle savedInstanceState) {
    }

    @Override
    public void onDataLoaded(Cursor data, int id) {
        if (id == TRAFFIC_LOADER) {
            mView.setLoadingIndicator(false);
            mView.showRepository(data);
        } else if (id == REFERENCES_LOADER) {
            mView.showReferrers(data);
        } else if (id == CLONES_LOADER) {
            mView.showClones(data);
        } else if (id == VIEWS_LOADER) {
            mView.showViews(data);
        }
    }

    @Override
    public void onDataEmpty(int id) {
    }

    @Override
    public void onDataNotAvailable(int id) {
        if (id == TRAFFIC_LOADER) {
            mView.setEmptyState(true);
        }
    }

    @Override
    public void onDataReset() {
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case VIEWS_LOADER:
                return mLoaderProvider.createViewsLoader(mRepositoryId);
            case CLONES_LOADER:
                return mLoaderProvider.createClonesLoader(mRepositoryId);
            case REFERENCES_LOADER:
                return mLoaderProvider.createReferrersLoader(mRepositoryId);
            default:
            case TRAFFIC_LOADER:
                return mLoaderProvider.createTrafficRepositoryLoader(mRepositoryId);
        }
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
            onDataNotAvailable(loader.getId());
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        onDataReset();
    }


}
