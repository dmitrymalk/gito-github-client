package com.dmitrymalkovich.android.githubanalytics.data.source.local;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * https://developer.github.com/v3/repos/traffic/
 */
public class TrafficClonesContract {
    static final String CONTENT_AUTHORITY = "com.dmitrymalkovich.android.githubanalytics.data";
    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    static final String PATH_TRAFFIC_CLONES = "traffic.clones";

    public static final class TrafficClonesEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_TRAFFIC_CLONES).build();

        static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TRAFFIC_CLONES;

        static final String TABLE_NAME = "traffic_clones";
        public static final String COLUMN_REPOSITORY_KEY = "repository_id";
        public static final String COLUMN_TRAFFIC_CLONES_COUNT = "count";
        public static final String COLUMN_TRAFFIC_CLONES_UNIQUES = "uniques";
        public static final String COLUMN_TRAFFIC_CLONES_TIMESTAMP = "timestamp";

        public static Uri buildUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static final String[] TRAFFIC_CLONES_COLUMNS = {
                _ID,
                COLUMN_REPOSITORY_KEY,
                COLUMN_TRAFFIC_CLONES_COUNT,
                COLUMN_TRAFFIC_CLONES_UNIQUES,
                COLUMN_TRAFFIC_CLONES_TIMESTAMP
        };

        public static final int COL_ID = 0;
        public static final int COL_REPOSITORY_KEY = 1;
        public static final int COL_TRAFFIC_CLONES_COUNT = 2;
        public static final int COL_TRAFFIC_CLONES_UNIQUES = 3;
        public static final int COL_TRAFFIC_CLONES_TIMESTAMP = 4;
    }
}
