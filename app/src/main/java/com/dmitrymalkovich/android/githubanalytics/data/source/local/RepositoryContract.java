package com.dmitrymalkovich.android.githubanalytics.data.source.local;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

import android.content.ContentResolver;

/**
 * GitHub API: https://developer.github.com/v3/repos/
 */
@SuppressWarnings("unused")
class RepositoryContract {

    static final String CONTENT_AUTHORITY = "com.dmitrymalkovich.android.githubanalytics";
    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    static final String PATH_REPOSITORY = "repository";

    static final class RepositoryEntry implements BaseColumns {

        static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_REPOSITORY).build();

        static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_REPOSITORY;

        static final String TABLE_NAME = "repository";
        static final String COLUMN_REPOSITORY_ID = "repository_id";
        static final String COLUMN_REPOSITORY_NAME = "repository_name";

        static Uri buildRepositoryUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static final String[] MOVIE_COLUMNS = {
                COLUMN_REPOSITORY_ID,
                COLUMN_REPOSITORY_NAME,
        };

        public static final int COL_REPOSITORY_ID = 0;
        public static final int COL_REPOSITORY_NAME = 1;
    }
}