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
package com.dmitrymalkovich.android.githubanalytics.repositories;

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

public class PublicRepositoryPresenter implements PublicRepositoryContract.Presenter,
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
    private PublicRepositoryContract.View mPublicRepositoriesView;

    public PublicRepositoryPresenter(@NonNull GithubRepository githubRepository,
                                     @NonNull PublicRepositoryContract.View view,
                                     @NonNull LoaderProvider loaderProvider,
                                     @NonNull LoaderManager loaderManager) {
        mGithubRepository = githubRepository;
        mPublicRepositoriesView = view;
        mLoaderProvider = loaderProvider;
        mLoaderManager = loaderManager;
        mPublicRepositoriesView.setPresenter(this);
    }

    @Override
    public void start(Bundle savedInstanceState) {
        mPublicRepositoriesView.setLoadingIndicator(true);
        if (savedInstanceState == null) {
            showRepositories();
            onRefresh();
        } else {
            mLoaderManager.initLoader(REPOSITORIES_LOADER,
                    null,
                    PublicRepositoryPresenter.this);
        }
    }

    @Override
    public void onRefresh() {
        mGithubRepository.getRepositories(new GithubDataSource.GetRepositoriesCallback() {
            @Override
            public void onRepositoriesLoaded(List<Repository> repositoryList) {
                mPublicRepositoriesView.setLoadingIndicator(false);
                mPublicRepositoriesView.setRefreshIndicator(false);
                mLoaderManager.restartLoader(REPOSITORIES_LOADER,
                        null,
                        PublicRepositoryPresenter.this);
            }

            @Override
            public void onDataNotAvailable() {
                PublicRepositoryPresenter.this.onDataNotAvailable(REPOSITORIES_LOADER);
            }
        });
    }

    @Override
    public void onDataLoaded(Cursor data, int id) {
        mPublicRepositoriesView.setLoadingIndicator(false);
        mPublicRepositoriesView.setRefreshIndicator(false);
        mPublicRepositoriesView.showRepositories(data);
    }

    @Override
    public void onDataEmpty(int id) {
        mPublicRepositoriesView.setEmptyState(true);
    }

    @Override
    public void onDataNotAvailable(int id) {
        mPublicRepositoriesView.setLoadingIndicator(false);
        mPublicRepositoriesView.setRefreshIndicator(false);
    }

    @Override
    public void onDataReset() {
        // Nothing to do
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return mLoaderProvider.createRepositoryLoader();
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

    private void showRepositories() {
        mLoaderManager.initLoader(REPOSITORIES_LOADER,
                null,
                PublicRepositoryPresenter.this);
    }

    @Override
    public void setPinned(boolean active, long id) {
        mGithubRepository.setPinned(active, id);
    }
}
