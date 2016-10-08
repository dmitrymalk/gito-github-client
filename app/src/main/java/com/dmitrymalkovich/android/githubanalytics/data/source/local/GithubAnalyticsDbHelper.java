package com.dmitrymalkovich.android.githubanalytics.data.source.local;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

class GithubAnalyticsDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "GithubAnalytics.db";

    GithubAnalyticsDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_REPOSITORIES_TABLE = "CREATE TABLE " + RepositoryContract.RepositoryEntry.TABLE_NAME
                + " (" +
                RepositoryContract.RepositoryEntry._ID + " INTEGER PRIMARY KEY," +
                RepositoryContract.RepositoryEntry.COLUMN_REPOSITORY_ID + " INTEGER NOT NULL, " +
                RepositoryContract.RepositoryEntry.COLUMN_REPOSITORY_NAME + " TEXT NOT NULL ," +
                RepositoryContract.RepositoryEntry.COLUMN_REPOSITORY_FULL_NAME + " TEXT NOT NULL ," +
                RepositoryContract.RepositoryEntry.COLUMN_REPOSITORY_DESCRIPTION + " TEXT NOT NULL ," +
                RepositoryContract.RepositoryEntry.COLUMN_REPOSITORY_PRIVATE + " TEXT NOT NULL ," +
                RepositoryContract.RepositoryEntry.COLUMN_REPOSITORY_FORK + " TEXT NOT NULL ," +
                RepositoryContract.RepositoryEntry.COLUMN_REPOSITORY_URL + " TEXT NOT NULL ," +
                RepositoryContract.RepositoryEntry.COLUMN_REPOSITORY_HTML_URL + " TEXT NOT NULL" +
                " );";
        sqLiteDatabase.execSQL(SQL_CREATE_REPOSITORIES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + RepositoryContract.RepositoryEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}