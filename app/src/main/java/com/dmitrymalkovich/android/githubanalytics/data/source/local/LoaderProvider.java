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
package com.dmitrymalkovich.android.githubanalytics.data.source.local;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;

import com.dmitrymalkovich.android.githubanalytics.data.source.local.contract.ClonesContract;
import com.dmitrymalkovich.android.githubanalytics.data.source.local.contract.ReferrerContract;
import com.dmitrymalkovich.android.githubanalytics.data.source.local.contract.RepositoryContract;
import com.dmitrymalkovich.android.githubanalytics.data.source.local.contract.TrendingContract;
import com.dmitrymalkovich.android.githubanalytics.data.source.local.contract.UserContract;
import com.dmitrymalkovich.android.githubanalytics.data.source.local.contract.ViewsContract;
import com.dmitrymalkovich.android.githubanalytics.util.TimeUtils;

import static com.google.common.base.Preconditions.checkNotNull;

public class LoaderProvider {

    @NonNull
    private final Context mContext;

    public LoaderProvider(@NonNull Context context) {
        mContext = checkNotNull(context, "context cannot be null");
    }

    public Loader<Cursor> createUsersLoader() {
        return new CursorLoader(
                mContext,
                UserContract.UsersEntry.CONTENT_URI,
                UserContract.UsersEntry.USERS_COLUMNS,
                null, null, null
        );
    }

    public Loader<Cursor> createReferrersLoader(long repositoryId) {
        return new CursorLoader(
                mContext,
                ReferrerContract.ReferrerEntry.CONTENT_URI,
                ReferrerContract.ReferrerEntry.REFERRER_COLUMNS,
                ReferrerContract.ReferrerEntry.TABLE_NAME + "."
                        + ReferrerContract.ReferrerEntry.COLUMN_REPOSITORY_KEY + " = ? ",
                new String[] {String.valueOf(repositoryId)}, null
        );
    }

    public Loader<Cursor> createClonesLoader(long repositoryId) {
        return new CursorLoader(
                mContext,
                ClonesContract.ClonesEntry.CONTENT_URI,
                ClonesContract.ClonesEntry.CLONES_COLUMNS,
                ClonesContract.ClonesEntry.TABLE_NAME + "."
                        + ClonesContract.ClonesEntry.COLUMN_REPOSITORY_KEY + " = ? AND " +
                ViewsContract.ViewsEntry.COLUMN_VIEWS_TIMESTAMP + " >= " + TimeUtils.weekAgo(),
                new String[] {String.valueOf(repositoryId)}, null
        );
    }

    public Loader<Cursor> createViewsLoader(long repositoryId) {
        return new CursorLoader(
                mContext,
                ViewsContract.ViewsEntry.CONTENT_URI,
                ViewsContract.ViewsEntry.VIEWS_COLUMNS,
                ViewsContract.ViewsEntry.COLUMN_REPOSITORY_KEY + " = ? AND " +
                        ViewsContract.ViewsEntry.COLUMN_VIEWS_TIMESTAMP + " >= " + TimeUtils.weekAgo(),
                new String[] {String.valueOf(repositoryId)}, null
        );
    }

    public Loader<Cursor> createTrafficRepositoryLoader(long repositoryId) {
        return new CursorLoader(
                mContext,
                RepositoryContract.RepositoryEntry.CONTENT_URI_REPOSITORY_STARGAZERS,
                RepositoryContract.RepositoryEntry.REPOSITORY_COLUMNS_WITH_ADDITIONAL_INFO,
                RepositoryContract.RepositoryEntry.TABLE_NAME + "."
                        + RepositoryContract.RepositoryEntry.COLUMN_REPOSITORY_ID + " = ? " ,
                new String[] {String.valueOf(repositoryId)}, null
        );
    }

    public Loader<Cursor> createPopularRepositoryLoader() {
        return new CursorLoader(
                mContext,
                RepositoryContract.RepositoryEntry.CONTENT_URI_REPOSITORY_STARGAZERS,
                RepositoryContract.RepositoryEntry.REPOSITORY_COLUMNS_WITH_ADDITIONAL_INFO,
                RepositoryContract.RepositoryEntry.COLUMN_REPOSITORY_FORK + " = ? AND "
                        + RepositoryContract.RepositoryEntry.COLUMN_REPOSITORY_WATCHERS + " > ? AND "
                        + RepositoryContract.RepositoryEntry.COLUMN_REPOSITORY_PINNED + " = ? ",
                new String[] {"0", "0", "1"},
                RepositoryContract.RepositoryEntry.COLUMN_REPOSITORY_WATCHERS + " DESC"
        );
    }

    public Loader<Cursor> createRepositoryLoader() {
        return new CursorLoader(
                mContext,
                RepositoryContract.RepositoryEntry.CONTENT_URI,
                RepositoryContract.RepositoryEntry.REPOSITORY_COLUMNS,
                null,
                null,
                RepositoryContract.RepositoryEntry.COLUMN_REPOSITORY_NAME + " ASC"
        );
    }

    public Loader<Cursor> createTrendingLoader(String language, String period) {
        return new CursorLoader(
                mContext,
                TrendingContract.TrendingEntry.CONTENT_URI,
                TrendingContract.TrendingEntry.TRENDING_COLUMNS,
                TrendingContract.TrendingEntry.COLUMN_LANGUAGE + " = ? AND " +
                        TrendingContract.TrendingEntry.COLUMN_PERIOD + " = ? ",
                new String[]{language, period},
                null
        );
    }
}
