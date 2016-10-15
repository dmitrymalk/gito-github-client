package com.dmitrymalkovich.android.githubanalytics.data.source.local.contract;

import android.content.ContentUris;
import android.content.ContentValues;
import android.net.Uri;
import android.provider.BaseColumns;

import android.content.ContentResolver;

import org.eclipse.egit.github.core.Repository;

import java.io.File;

/**
 * GitHub API: https://developer.github.com/v3/repos/
 */
@SuppressWarnings("unused")
public class RepositoryContract {

    public static final String CONTENT_AUTHORITY = "com.dmitrymalkovich.android.githubanalytics.data";
    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_REPOSITORY = "repository";

    public static final class RepositoryEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_REPOSITORY).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_REPOSITORY;

        public static final String TABLE_NAME = "repository";
        public static final String COLUMN_REPOSITORY_ID = "repository_id";
        public static final String COLUMN_REPOSITORY_NAME = "repository_name";
        public static final String COLUMN_REPOSITORY_FULL_NAME = "repository_full_name";
        public static final String COLUMN_REPOSITORY_DESCRIPTION = "repository_description";
        public static final String COLUMN_REPOSITORY_PRIVATE = "repository_private";
        public static final String COLUMN_REPOSITORY_FORK = "repository_fork";
        public static final String COLUMN_REPOSITORY_URL = "repository_url";
        public static final String COLUMN_REPOSITORY_HTML_URL = "repository_html_url";
        public static final String COLUMN_REPOSITORY_FORKS = "repository_forks";
        public static final String COLUMN_REPOSITORY_WATCHERS = "repository_watchers";
        public static final String COLUMN_REPOSITORY_LANGUAGE = "repository_language";

        public static Uri buildRepositoryUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static final String[] REPOSITORY_COLUMNS = {
                _ID,
                COLUMN_REPOSITORY_ID,
                COLUMN_REPOSITORY_NAME,
                COLUMN_REPOSITORY_FULL_NAME,
                COLUMN_REPOSITORY_DESCRIPTION,
                COLUMN_REPOSITORY_PRIVATE,
                COLUMN_REPOSITORY_FORK,
                COLUMN_REPOSITORY_URL,
                COLUMN_REPOSITORY_HTML_URL,
                COLUMN_REPOSITORY_FORKS,
                COLUMN_REPOSITORY_WATCHERS,
                COLUMN_REPOSITORY_LANGUAGE
        };

        public static final int COL_ID = 0;
        public static final int COL_REPOSITORY_ID = 1;
        public static final int COL_REPOSITORY_NAME = 2;
        public static final int COL_REPOSITORY_FULL_NAME = 3;
        public static final int COL_REPOSITORY_DESCRIPTION = 4;
        public static final int COL_REPOSITORY_PRIVATE = 5;
        public static final int COL_REPOSITORY_FORK = 6;
        public static final int COL_REPOSITORY_URL = 7;
        public static final int COL_REPOSITORY_HTML_URL = 8;
        public static final int COL_REPOSITORY_FORKS = 9;
        public static final int COL_REPOSITORY_WATCHERS = 10;
        public static final int COL_REPOSITORY_LANGUAGE = 11;

        public static ContentValues buildContentValues(Repository repo) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(RepositoryContract.RepositoryEntry.COLUMN_REPOSITORY_ID,
                    repo.getId());
            contentValues.put(RepositoryContract.RepositoryEntry.COLUMN_REPOSITORY_NAME,
                    repo.getName());
            contentValues.put(RepositoryContract.RepositoryEntry.COLUMN_REPOSITORY_FULL_NAME,
                    repo.getOwner().getName() + File.separator + repo.getName());
            contentValues.put(RepositoryContract.RepositoryEntry.COLUMN_REPOSITORY_DESCRIPTION,
                    repo.getDescription());
            contentValues.put(RepositoryContract.RepositoryEntry.COLUMN_REPOSITORY_PRIVATE,
                    repo.isPrivate());
            contentValues.put(RepositoryContract.RepositoryEntry.COLUMN_REPOSITORY_FORK,
                    repo.isFork());
            contentValues.put(RepositoryContract.RepositoryEntry.COLUMN_REPOSITORY_URL,
                    repo.getUrl());
            contentValues.put(RepositoryContract.RepositoryEntry.COLUMN_REPOSITORY_HTML_URL,
                    repo.getHtmlUrl());
            contentValues.put(RepositoryContract.RepositoryEntry.COLUMN_REPOSITORY_FORKS,
                    repo.getForks());
            contentValues.put(RepositoryContract.RepositoryEntry.COLUMN_REPOSITORY_WATCHERS,
                    repo.getWatchers());
            contentValues.put(RepositoryContract.RepositoryEntry.COLUMN_REPOSITORY_LANGUAGE,
                    repo.getLanguage());
            return contentValues;
        }
    }
}