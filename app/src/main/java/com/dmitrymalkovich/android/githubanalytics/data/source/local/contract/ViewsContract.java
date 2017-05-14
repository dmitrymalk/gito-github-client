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
package com.dmitrymalkovich.android.githubanalytics.data.source.local.contract;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.net.Uri;
import android.provider.BaseColumns;

import com.dmitrymalkovich.android.githubapi.core.TimeConverter;
import com.dmitrymalkovich.android.githubapi.core.gson.Views;

/**
 * https://developer.github.com/v3/repos/traffic/
 */
public class ViewsContract {
    public static String LOG_TAG = ViewsContract.class.getSimpleName();
    public static final String CONTENT_AUTHORITY = "com.dmitrymalkovich.android.githubanalytics.data";
    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_VIEWS = "traffic.views";

    public static final class ViewsEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_VIEWS).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_VIEWS;

        public static final String TABLE_NAME = "traffic_views";
        public static final String COLUMN_REPOSITORY_KEY = "repository_id";
        public static final String COLUMN_VIEWS_COUNT = "count";
        public static final String COLUMN_VIEWS_UNIQUES = "uniques";
        public static final String COLUMN_VIEWS_TIMESTAMP = "timestamp";

        public static Uri buildUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static final String[] VIEWS_COLUMNS = {
                _ID,
                COLUMN_REPOSITORY_KEY,
                COLUMN_VIEWS_COUNT,
                COLUMN_VIEWS_UNIQUES,
                COLUMN_VIEWS_TIMESTAMP
        };

        public static final int COL_ID = 0;
        public static final int COL_REPOSITORY_KEY = 1;
        public static final int COL_VIEWS_COUNT = 2;
        public static final int COL_VIEWS_UNIQUES = 3;
        public static final int COL_VIEWS_TIMESTAMP = 4;

        public static ContentValues buildContentValues(long repositoryId, Views.View view) {
            String timestamp = view.getTimestamp();
            long timeInMilliseconds = TimeConverter.iso8601ToMilliseconds(timestamp);

            ContentValues contentValues = new ContentValues();
            contentValues.put(COLUMN_REPOSITORY_KEY, repositoryId);
            contentValues.put(COLUMN_VIEWS_COUNT, view.getCount());
            contentValues.put(COLUMN_VIEWS_TIMESTAMP, timeInMilliseconds);
            contentValues.put(COLUMN_VIEWS_UNIQUES, view.getUniques());
            return contentValues;
        }
    }
}
