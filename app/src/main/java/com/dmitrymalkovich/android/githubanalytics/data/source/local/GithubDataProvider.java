package com.dmitrymalkovich.android.githubanalytics.data.source.local;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.dmitrymalkovich.android.githubanalytics.data.source.local.contract.ClonesContract;
import com.dmitrymalkovich.android.githubanalytics.data.source.local.contract.ReferrerContract;
import com.dmitrymalkovich.android.githubanalytics.data.source.local.contract.RepositoryContract;
import com.dmitrymalkovich.android.githubanalytics.data.source.local.contract.ViewsContract;

public class GithubDataProvider extends ContentProvider {

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    static final int REPOSITORIES = 300;
    static final int REFERRERS = 400;
    static final int CLONES = 401;
    static final int VIEWS = 402;
    private GithubAnalyticsDbHelper mOpenHelper;

    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = RepositoryContract.CONTENT_AUTHORITY;
        matcher.addURI(authority, RepositoryContract.PATH_REPOSITORY, REPOSITORIES);
        matcher.addURI(authority, ReferrerContract.PATH_REFERRERS, REFERRERS);
        matcher.addURI(authority, ViewsContract.PATH_VIEWS, VIEWS);
        matcher.addURI(authority, ClonesContract.PATH_CLONES, CLONES);
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
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (getContext() != null) {
            getContext().getContentResolver().notifyChange(uri, null);
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
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0 && getContext() != null) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }
}
