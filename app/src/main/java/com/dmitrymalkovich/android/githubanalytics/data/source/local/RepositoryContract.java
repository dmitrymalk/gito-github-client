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

    static final String CONTENT_AUTHORITY = "com.dmitrymalkovich.android.githubanalytics.data";
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
        static final String COLUMN_REPOSITORY_FULL_NAME = "repository_full_name";
        static final String COLUMN_REPOSITORY_DESCRIPTION = "repository_description";
        static final String COLUMN_REPOSITORY_PRIVATE = "repository_private";
        static final String COLUMN_REPOSITORY_FORK = "repository_fork";
        static final String COLUMN_REPOSITORY_URL = "repository_url";
        static final String COLUMN_REPOSITORY_HTML_URL = "repository_html_url";

        static Uri buildRepositoryUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static final String[] MOVIE_COLUMNS = {
                COLUMN_REPOSITORY_ID,
                COLUMN_REPOSITORY_NAME,
        };

        public static final int COL_REPOSITORY_ID = 0;
        public static final int COL_REPOSITORY_NAME = 1;
        public static final int COL_REPOSITORY_FULL_NAME = 2;
        public static final int COL_REPOSITORY_DESCRIPTION = 3;
        public static final int COL_REPOSITORY_PRIVATE = 4;
        public static final int COL_REPOSITORY_FORK = 5;
        public static final int COL_REPOSITORY_URL = 6;
        public static final int COL_REPOSITORY_HTML_URL = 7;
    }
}