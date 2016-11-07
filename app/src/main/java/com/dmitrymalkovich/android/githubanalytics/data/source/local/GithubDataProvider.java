package com.dmitrymalkovich.android.githubanalytics.data.source.local;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.dmitrymalkovich.android.githubanalytics.data.source.local.contract.ClonesContract;
import com.dmitrymalkovich.android.githubanalytics.data.source.local.contract.ReferrerContract;
import com.dmitrymalkovich.android.githubanalytics.data.source.local.contract.RepositoryContract;
import com.dmitrymalkovich.android.githubanalytics.data.source.local.contract.StargazersContract;
import com.dmitrymalkovich.android.githubanalytics.data.source.local.contract.TrendingContract;
import com.dmitrymalkovich.android.githubanalytics.data.source.local.contract.ViewsContract;
import com.dmitrymalkovich.android.githubanalytics.util.TimeUtils;

public class GithubDataProvider extends ContentProvider {

    public static String LOG_TAG = GithubDataProvider.class.getSimpleName();
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    static final int REPOSITORIES = 300;
    static final int REFERRERS = 400;
    static final int CLONES = 401;
    static final int VIEWS = 402;
    static final int TRENDING = 500;
    static final int STARGAZERS = 600;
    static final int REPOSITORIES_STARGAZERS = 700;
    private GithubAnalyticsDbHelper mOpenHelper;

    private static final SQLiteQueryBuilder sRepositoryByVisitorsAndStarsQueryBuilder;

    static {
        sRepositoryByVisitorsAndStarsQueryBuilder = new SQLiteQueryBuilder();
        sRepositoryByVisitorsAndStarsQueryBuilder.setTables(
                RepositoryContract.RepositoryEntry.TABLE_NAME
                + " LEFT JOIN (SELECT stargazers.repository_id, COUNT(stargazers.timestamp) as stars FROM stargazers WHERE timestamp >= "
                + TimeUtils.today() +
                " GROUP BY stargazers.repository_id) as stargazers ON stargazers.repository_id = repository.repository_id"

                + " LEFT JOIN (SELECT stargazers.repository_id, COUNT(stargazers.timestamp) as stars FROM stargazers WHERE timestamp >= "
                + TimeUtils.yesterday() + " AND timestamp < " + TimeUtils.today() +
                " GROUP BY stargazers.repository_id) as stargazers_yesterday ON stargazers_yesterday.repository_id = repository.repository_id"

                + " LEFT JOIN (SELECT stargazers.repository_id, COUNT(stargazers.timestamp) as stars FROM stargazers WHERE timestamp >= "
                + TimeUtils.twoWeeksAgo() +
                " GROUP BY stargazers.repository_id) as stargazers_two_weeks ON stargazers_two_weeks.repository_id = repository.repository_id"

                + " LEFT JOIN (SELECT traffic_views.repository_id, traffic_views.uniques, traffic_views.count FROM traffic_views WHERE timestamp >= "
                + TimeUtils.today() +
                ") as traffic_views ON traffic_views.repository_id = repository.repository_id"

                + " LEFT JOIN (SELECT traffic_views.repository_id, traffic_views.uniques, traffic_views.count FROM traffic_views WHERE timestamp >= "
                + TimeUtils.yesterday() + " AND timestamp < " + TimeUtils.today() +
                ") as traffic_views_yesterday ON traffic_views_yesterday.repository_id = repository.repository_id"

                + " LEFT JOIN (SELECT traffic_views.repository_id, SUM(traffic_views.uniques) as uniques, SUM(traffic_views.count) as count FROM traffic_views WHERE timestamp >= "
                + TimeUtils.twoWeeksAgo() +
                " GROUP BY traffic_views.repository_id) as traffic_views_two_weeks ON traffic_views_two_weeks.repository_id = repository.repository_id"

                + " LEFT JOIN (SELECT traffic_clones.repository_id, traffic_clones.uniques, traffic_clones.count FROM traffic_clones WHERE timestamp >= "
                + TimeUtils.today() +
                ") as traffic_clones ON traffic_clones.repository_id = repository.repository_id"

                + " LEFT JOIN (SELECT traffic_clones.repository_id, traffic_clones.uniques, traffic_clones.count FROM traffic_clones WHERE timestamp >= "
                + TimeUtils.yesterday() + " AND timestamp <" + TimeUtils.today() +
                ") as traffic_clones_yesterday ON traffic_clones_yesterday.repository_id = repository.repository_id"

                + " LEFT JOIN (SELECT traffic_clones.repository_id, SUM(traffic_clones.uniques) as uniques, SUM(traffic_clones.count) as count FROM traffic_clones WHERE timestamp >= "
                + TimeUtils.twoWeeksAgo() +
                " GROUP BY traffic_clones.repository_id) as traffic_clones_two_weeks ON traffic_clones_two_weeks.repository_id = repository.repository_id"
        );
    }

    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = RepositoryContract.CONTENT_AUTHORITY;
        matcher.addURI(authority, RepositoryContract.PATH_REPOSITORY, REPOSITORIES);
        matcher.addURI(authority, ReferrerContract.PATH_REFERRERS, REFERRERS);
        matcher.addURI(authority, ViewsContract.PATH_VIEWS, VIEWS);
        matcher.addURI(authority, ClonesContract.PATH_CLONES, CLONES);
        matcher.addURI(authority, TrendingContract.PATH_TRENDING, TRENDING);
        matcher.addURI(authority, StargazersContract.PATH_STARGAZERS, STARGAZERS);
        matcher.addURI(authority, RepositoryContract.PATH_REPOSITORY_STARGAZERS, REPOSITORIES_STARGAZERS);
        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new GithubAnalyticsDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        Cursor cursor;
        switch (sUriMatcher.match(uri)) {
            case REPOSITORIES: {
                cursor = mOpenHelper.getReadableDatabase().query(
                        RepositoryContract.RepositoryEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case REFERRERS: {
                cursor = mOpenHelper.getReadableDatabase().query(
                        ReferrerContract.ReferrerEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case CLONES: {
                cursor = mOpenHelper.getReadableDatabase().query(
                        ClonesContract.ClonesEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case VIEWS: {
                cursor = mOpenHelper.getReadableDatabase().query(
                        ViewsContract.ViewsEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case TRENDING: {
                cursor = mOpenHelper.getReadableDatabase().query(
                        TrendingContract.TrendingEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case STARGAZERS: {
                cursor = mOpenHelper.getReadableDatabase().query(
                        StargazersContract.Entry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case REPOSITORIES_STARGAZERS: {
                cursor = sRepositoryByVisitorsAndStarsQueryBuilder.query(
                        mOpenHelper.getReadableDatabase(),
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (getContext() != null) {
            cursor.setNotificationUri(getContext().getContentResolver(), uri);
        }
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case REPOSITORIES:
                return RepositoryContract.RepositoryEntry.CONTENT_TYPE;
            case REFERRERS:
                return ReferrerContract.ReferrerEntry.CONTENT_TYPE;
            case CLONES:
                return ClonesContract.ClonesEntry.CONTENT_TYPE;
            case VIEWS:
                return ViewsContract.ViewsEntry.CONTENT_TYPE;
            case TRENDING:
                return TrendingContract.TrendingEntry.CONTENT_TYPE;
            case STARGAZERS:
                return StargazersContract.Entry.CONTENT_TYPE;
            case REPOSITORIES_STARGAZERS:
                return RepositoryContract.RepositoryEntry.CONTENT_TYPE_REPOSITORY_STARGAZERS;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;
        switch (match) {
            case REPOSITORIES: {
                long id = db.insert(RepositoryContract.RepositoryEntry.TABLE_NAME, null, values);
                if (id > 0) {
                    returnUri = RepositoryContract.RepositoryEntry.buildRepositoryUri(id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            }
            case REFERRERS: {
                long id = db.insert(ReferrerContract.ReferrerEntry.TABLE_NAME, null, values);
                if (id > 0) {
                    returnUri = ReferrerContract.ReferrerEntry.buildUri(id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            }
            case VIEWS: {
                long id = db.insert(ViewsContract.ViewsEntry.TABLE_NAME, null, values);
                if (id > 0) {
                    returnUri = ViewsContract.ViewsEntry.buildUri(id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            }
            case CLONES: {
                long id = db.insert(ClonesContract.ClonesEntry.TABLE_NAME, null, values);
                if (id > 0) {
                    returnUri = ClonesContract.ClonesEntry.buildUri(id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            }
            case TRENDING: {
                long id = db.insert(TrendingContract.TrendingEntry.TABLE_NAME, null, values);
                if (id > 0) {
                    returnUri = TrendingContract.TrendingEntry.buildUri(id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            }
            case STARGAZERS: {
                long id = db.insert(StargazersContract.Entry.TABLE_NAME, null, values);
                if (id > 0) {
                    returnUri = StargazersContract.Entry.buildUri(id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (getContext() != null) {
            getContext().getContentResolver().notifyChange(uri, null);
            if (match == STARGAZERS || match == REPOSITORIES || match == CLONES
                    || match == VIEWS || match == REFERRERS)
            {
                getContext().getContentResolver().notifyChange(
                        RepositoryContract.RepositoryEntry.CONTENT_URI_REPOSITORY_STARGAZERS, null);
            }
        }
        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;

        if (null == selection) {
            selection = "1";
        }
        switch (match) {
            case REPOSITORIES:
                rowsDeleted = db.delete(
                        RepositoryContract.RepositoryEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case REFERRERS:
                rowsDeleted = db.delete(
                        ReferrerContract.ReferrerEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case CLONES:
                rowsDeleted = db.delete(
                        ClonesContract.ClonesEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case VIEWS:
                rowsDeleted = db.delete(
                        ViewsContract.ViewsEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case TRENDING:
                rowsDeleted = db.delete(
                        TrendingContract.TrendingEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case STARGAZERS:
                rowsDeleted = db.delete(
                        StargazersContract.Entry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (rowsDeleted != 0 && getContext() != null) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case REPOSITORIES:
                rowsUpdated = db.update(RepositoryContract.RepositoryEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case REFERRERS:
                rowsUpdated = db.update(ReferrerContract.ReferrerEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case CLONES:
                rowsUpdated = db.update(ClonesContract.ClonesEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case VIEWS:
                rowsUpdated = db.update(ViewsContract.ViewsEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case TRENDING:
                rowsUpdated = db.update(TrendingContract.TrendingEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case STARGAZERS:
                rowsUpdated = db.update(StargazersContract.Entry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0 && getContext() != null) {
            getContext().getContentResolver().notifyChange(uri, null);
            if (match == STARGAZERS || match == REPOSITORIES || match == CLONES
                    || match == VIEWS || match == REFERRERS)
            {
                getContext().getContentResolver().notifyChange(
                        RepositoryContract.RepositoryEntry.CONTENT_URI_REPOSITORY_STARGAZERS, null);
            }
        }
        return rowsUpdated;
    }
}
