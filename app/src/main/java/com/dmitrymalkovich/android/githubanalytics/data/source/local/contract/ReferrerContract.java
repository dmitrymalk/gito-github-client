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

import com.dmitrymalkovich.android.githubapi.core.gson.ReferringSite;

/**
 * https://developer.github.com/v3/repos/traffic/
 */
public class ReferrerContract {
    public static final String CONTENT_AUTHORITY = "com.dmitrymalkovich.android.githubanalytics.data";
    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_REFERRERS = "traffic.paths";

    public static final class ReferrerEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_REFERRERS).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_REFERRERS;

        public static final String TABLE_NAME = "traffic_paths";
        public static final String COLUMN_REPOSITORY_KEY = "repository_id";
        public static final String COLUMN_REFERRER_REFERRER = "referrer";
        public static final String COLUMN_REFERRER_COUNT = "count";
        public static final String COLUMN_REFERRER_UNIQUES = "uniques";

        public static final String[] REFERRER_COLUMNS = {
                _ID,
                COLUMN_REPOSITORY_KEY,
                COLUMN_REFERRER_REFERRER,
                COLUMN_REFERRER_COUNT,
                COLUMN_REFERRER_UNIQUES
        };

        public static final int COL_ID = 0;
        public static final int COL_REPOSITORY_KEY = 1;
        public static final int COL_PATHS_REFERRER = 2;
        public static final int COL_PATHS_COUNT = 3;
        public static final int COL_PATHS_UNIQUES = 4;

        public static Uri buildUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static ContentValues createContentValues(long repositoryId, ReferringSite referrer) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(ReferrerEntry.COLUMN_REPOSITORY_KEY,
                    repositoryId);
            contentValues.put(ReferrerEntry.COLUMN_REFERRER_REFERRER,
                    referrer.getReferrer());
            contentValues.put(ReferrerEntry.COLUMN_REFERRER_COUNT,
                    referrer.getCount());
            contentValues.put(ReferrerEntry.COLUMN_REFERRER_UNIQUES,
                    referrer.getUniques());
            return contentValues;
        }
    }
}
