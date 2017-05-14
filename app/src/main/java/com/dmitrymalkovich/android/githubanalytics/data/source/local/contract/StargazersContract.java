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
import com.dmitrymalkovich.android.githubapi.core.gson.Star;

public class StargazersContract {
    public static final String LOG_TAG = StargazersContract.class.getSimpleName();
    public static final String CONTENT_AUTHORITY = "com.dmitrymalkovich.android.githubanalytics.data";
    public static final String PATH_STARGAZERS = "stargazers";
    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final class Entry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_STARGAZERS).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_STARGAZERS;

        public static final String TABLE_NAME = "stargazers";
        public static final String COLUMN_REPOSITORY_KEY = "repository_id";
        public static final String COLUMN_TIMESTAMP = "timestamp";

        public static final String[] STARTGAZERS_COLUMNS = {
                _ID,
                COLUMN_REPOSITORY_KEY,
                COLUMN_TIMESTAMP
        };

        public static final int COL_ID = 0;
        public static final int COL_REPOSITORY_KEY = 1;
        public static final int COL_TIMESTAMP = 2;

        public static Uri buildUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static ContentValues buildContentValues(long repositoryId,
                                                       Star star) {
            String timestamp = star.getStarredAt();
            long timeInMilliseconds = TimeConverter.iso8601ToMilliseconds(timestamp);

            ContentValues contentValues = new ContentValues();
            contentValues.put(COLUMN_REPOSITORY_KEY, repositoryId);
            contentValues.put(COLUMN_TIMESTAMP, timeInMilliseconds);
            return contentValues;
        }
    }
}
