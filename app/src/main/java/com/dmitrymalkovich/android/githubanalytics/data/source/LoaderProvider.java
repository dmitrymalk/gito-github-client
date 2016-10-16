package com.dmitrymalkovich.android.githubanalytics.data.source;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;

import com.dmitrymalkovich.android.githubanalytics.data.source.local.contract.RepositoryContract;
import com.dmitrymalkovich.android.githubanalytics.data.source.local.contract.TrendingContract;

import static com.google.common.base.Preconditions.checkNotNull;

public class LoaderProvider {

    @NonNull
    private final Context mContext;

    public LoaderProvider(@NonNull Context context) {
        mContext = checkNotNull(context, "context cannot be null");
    }

    public Loader<Cursor> createPopularRepositoryLoader() {
        return new CursorLoader(
                mContext,
                RepositoryContract.RepositoryEntry.CONTENT_URI,
                RepositoryContract.RepositoryEntry.REPOSITORY_COLUMNS,
                RepositoryContract.RepositoryEntry.COLUMN_REPOSITORY_FORK + " = ?",
                new String[] {"0"},
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

    public Loader<Cursor> createTrendingLoader() {
        return new CursorLoader(
                mContext,
                TrendingContract.TrendingEntry.CONTENT_URI,
                TrendingContract.TrendingEntry.TRENDING_COLUMNS,
                null,
                null,
                null
        );
    }
}
