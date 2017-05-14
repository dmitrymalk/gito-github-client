/*
 * Copyright 2017.  Dmitry Malkovich
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.dmitrymalkovich.android.githubanalytics.traffic;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;

import com.dmitrymalkovich.android.githubanalytics.data.source.GithubDataSource;
import com.dmitrymalkovich.android.githubanalytics.data.source.GithubRepository;
import com.dmitrymalkovich.android.githubanalytics.data.source.local.LoaderProvider;

import org.eclipse.egit.github.core.Repository;

import java.util.List;

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
        mGithubRepository = githubRepository;
        mView = view;
        mLoaderProvider = loaderProvider;
        mLoaderManager = loaderManager;
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
                        // Nothing to do
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
