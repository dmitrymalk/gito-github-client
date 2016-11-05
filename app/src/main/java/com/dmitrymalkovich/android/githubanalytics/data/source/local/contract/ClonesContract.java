package com.dmitrymalkovich.android.githubanalytics.data.source.local.contract;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.net.Uri;
import android.provider.BaseColumns;

import com.dmitrymalkovich.android.githubanalytics.data.source.remote.gson.ResponseClones;
import com.dmitrymalkovich.android.githubanalytics.util.TimeUtils;

/**
 * https://developer.github.com/v3/repos/traffic/
 */
public class ClonesContract {
    public static String LOG_TAG = ClonesContract.class.getSimpleName();
    public static final String CONTENT_AUTHORITY = "com.dmitrymalkovich.android.githubanalytics.data";
    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_CLONES = "traffic.clones";

    public static final class ClonesEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_CLONES).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CLONES;

        public static final String TABLE_NAME = "traffic_clones";
        public static final String COLUMN_REPOSITORY_KEY = "repository_id";
        public static final String COLUMN_CLONES_COUNT = "count";
        public static final String COLUMN_CLONES_UNIQUES = "uniques";
        public static final String COLUMN_CLONES_TIMESTAMP = "timestamp";

        public static Uri buildUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static final String[] CLONES_COLUMNS = {
                _ID,
                COLUMN_REPOSITORY_KEY,
                COLUMN_CLONES_COUNT,
                COLUMN_CLONES_UNIQUES,
                COLUMN_CLONES_TIMESTAMP
        };

        public static final int COL_ID = 0;
        public static final int COL_REPOSITORY_KEY = 1;
        public static final int COL_CLONES_COUNT = 2;
        public static final int COL_CLONES_UNIQUES = 3;
        public static final int COL_CLONES_TIMESTAMP = 4;

        public static ContentValues buildContentValues(long repositoryId, ResponseClones.Clone clone) {
            String timestamp = clone.getTimestamp();
            long timeInMilliseconds = TimeUtils.iso8601ToMilliseconds(timestamp);

            ContentValues contentValues = new ContentValues();
            contentValues.put(COLUMN_REPOSITORY_KEY, repositoryId);
            contentValues.put(COLUMN_CLONES_COUNT, clone.getCount());
            contentValues.put(COLUMN_CLONES_TIMESTAMP, timeInMilliseconds);
            contentValues.put(COLUMN_CLONES_UNIQUES, clone.getUniques());
            return contentValues;
        }
    }
}
