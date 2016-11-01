package com.dmitrymalkovich.android.githubanalytics.data.source.local.contract;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.net.Uri;
import android.provider.BaseColumns;

import com.dmitrymalkovich.android.githubanalytics.data.source.remote.gson.ResponseTrending;

/**
 * https://github.com/mingjunli/GithubTrending
 */
@SuppressWarnings("unused")
public class TrendingContract {

    public static final String CONTENT_AUTHORITY = "com.dmitrymalkovich.android.githubanalytics.data";
    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_TRENDING = "trending";

    public static final class TrendingEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_TRENDING).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TRENDING;

        public static final String TABLE_NAME = "trending";
        public static final String COLUMN_HTML_URL = "trending_name";
        public static final String COLUMN_WATCHER_COUNT = "watchers_count";
        public static final String COLUMN_LANGUAGE = "language";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_AVATAR = "avatar";
        public static final String COLUMN_PERIOD = "period";

        public static Uri buildUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static final String[] TRENDING_COLUMNS = {
                _ID,
                COLUMN_HTML_URL,
                COLUMN_WATCHER_COUNT,
                COLUMN_LANGUAGE,
                COLUMN_DESCRIPTION,
                COLUMN_NAME,
                COLUMN_AVATAR,
                COLUMN_PERIOD
        };

        public static final int COL_ID = 0;
        public static final int COL_HTML_URL = 1;
        public static final int COL_WATCHER_COUNT = 2;
        public static final int COL_LANGUAGE = 3;
        public static final int COL_DESCRIPTION = 4;
        public static final int COL_NAME = 5;
        public static final int COL_AVATAR = 6;
        public static final int COL_PERIOD = 7;

        public static ContentValues buildContentValues(ResponseTrending responseTrending, String period,
                                                       String language) {
            String htmlUrl = responseTrending.getHtmlUrl();
            if (!htmlUrl.contains("https://github.com/")) {
                htmlUrl = htmlUrl.replace("https://github.com", "https://github.com/");
            }
            ContentValues contentValues = new ContentValues();
            contentValues.put(TrendingContract.TrendingEntry.COLUMN_HTML_URL,
                    htmlUrl);
            contentValues.put(TrendingContract.TrendingEntry.COLUMN_WATCHER_COUNT,
                    responseTrending.getWatchersCount());
            contentValues.put(TrendingContract.TrendingEntry.COLUMN_LANGUAGE, language);
            contentValues.put(TrendingContract.TrendingEntry.COLUMN_DESCRIPTION,
                    responseTrending.getDescription());
            contentValues.put(TrendingContract.TrendingEntry.COLUMN_NAME, responseTrending.getOwner()
                    + "/" + responseTrending.getName());
            contentValues.put(TrendingEntry.COLUMN_AVATAR, responseTrending.getAvatar());
            contentValues.put(TrendingEntry.COLUMN_PERIOD, period);
            return contentValues;
        }
    }
}