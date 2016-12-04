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

        public static Uri buildUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

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
