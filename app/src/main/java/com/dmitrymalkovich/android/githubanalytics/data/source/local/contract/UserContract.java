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

import com.dmitrymalkovich.android.githubapi.core.gson.User;

public class UserContract {
    public static String LOG_TAG = UserContract.class.getSimpleName();
    public static final String CONTENT_AUTHORITY = "com.dmitrymalkovich.android.githubanalytics.data";
    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_USERS = "users";

    public static final class UsersEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_USERS).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_USERS;

        public static final String TABLE_NAME = "users";
        public static final String COLUMN_LOGIN = "login";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_AVATAR = "avatar";
        public static final String COLUMN_FOLLOWERS = "followers";

        public static final String[] USERS_COLUMNS = {
                _ID,
                COLUMN_LOGIN,
                COLUMN_NAME,
                COLUMN_AVATAR,
                COLUMN_FOLLOWERS
        };

        public static final int COL_ID = 0;
        public static final int COL_LOGIN = 1;
        public static final int COL_NAME = 2;
        public static final int COL_AVATAR = 3;
        public static final int COL_FOLLOWERS = 4;

        public static Uri buildUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static ContentValues buildContentValues(User user) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(COLUMN_LOGIN, user.getLogin());
            contentValues.put(COLUMN_NAME, user.getName());
            contentValues.put(COLUMN_AVATAR, user.getAvatarUrl());
            contentValues.put(COLUMN_FOLLOWERS, user.getFollowers());
            return contentValues;
        }
    }
}
