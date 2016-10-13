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
                RepositoryContract.RepositoryEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                RepositoryContract.RepositoryEntry.COLUMN_REPOSITORY_ID + " INTEGER NOT NULL, " +
                RepositoryContract.RepositoryEntry.COLUMN_REPOSITORY_NAME + " TEXT ," +
                RepositoryContract.RepositoryEntry.COLUMN_REPOSITORY_FULL_NAME + " TEXT ," +
                RepositoryContract.RepositoryEntry.COLUMN_REPOSITORY_DESCRIPTION + " TEXT ," +
                RepositoryContract.RepositoryEntry.COLUMN_REPOSITORY_PRIVATE + " TEXT ," +
                RepositoryContract.RepositoryEntry.COLUMN_REPOSITORY_FORK + " TEXT ," +
                RepositoryContract.RepositoryEntry.COLUMN_REPOSITORY_URL + " TEXT ," +
                RepositoryContract.RepositoryEntry.COLUMN_REPOSITORY_HTML_URL + " TEXT ," +
                RepositoryContract.RepositoryEntry.COLUMN_REPOSITORY_FORKS + " INTEGER ," +
                RepositoryContract.RepositoryEntry.COLUMN_REPOSITORY_WATCHERS + " INTEGER ," +
                RepositoryContract.RepositoryEntry.COLUMN_REPOSITORY_LANGUAGE + " TEXT" +
                " );";
        sqLiteDatabase.execSQL(SQL_CREATE_REPOSITORIES_TABLE);
        final String SQL_CREATE_TRAFFIC_VIEWS_TABLE = "CREATE TABLE " + TrafficViewsContract.TrafficViewsEntry.TABLE_NAME
                + " (" +
                TrafficViewsContract.TrafficViewsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                TrafficViewsContract.TrafficViewsEntry.COLUMN_REPOSITORY_KEY + " INTEGER NOT NULL, " +
                TrafficViewsContract.TrafficViewsEntry.COLUMN_TRAFFIC_VIEWS_COUNT + " TEXT NOT NULL ," +
                TrafficViewsContract.TrafficViewsEntry.COLUMN_TRAFFIC_VIEWS_UNIQUES + " TEXT NOT NULL ," +
                TrafficViewsContract.TrafficViewsEntry.COLUMN_TRAFFIC_VIEWS_TIMESTAMP + " DATE NOT NULL," +
                " FOREIGN KEY (" + TrafficViewsContract.TrafficViewsEntry.COLUMN_REPOSITORY_KEY + ") REFERENCES " +
                RepositoryContract.RepositoryEntry.TABLE_NAME + " (" + RepositoryContract.RepositoryEntry._ID + ")" +
                " );";
        sqLiteDatabase.execSQL(SQL_CREATE_TRAFFIC_VIEWS_TABLE);
        final String SQL_CREATE_TRAFFIC_CLONES_TABLE = "CREATE TABLE " + TrafficClonesContract.TrafficClonesEntry.TABLE_NAME
                + " (" +
                TrafficClonesContract.TrafficClonesEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                TrafficClonesContract.TrafficClonesEntry.COLUMN_REPOSITORY_KEY + " INTEGER NOT NULL, " +
                TrafficClonesContract.TrafficClonesEntry.COLUMN_TRAFFIC_CLONES_COUNT + " TEXT NOT NULL ," +
                TrafficClonesContract.TrafficClonesEntry.COLUMN_TRAFFIC_CLONES_UNIQUES + " TEXT NOT NULL ," +
                TrafficClonesContract.TrafficClonesEntry.COLUMN_TRAFFIC_CLONES_TIMESTAMP + " DATE NOT NULL," +
                " FOREIGN KEY (" + TrafficClonesContract.TrafficClonesEntry.COLUMN_REPOSITORY_KEY + ") REFERENCES " +
                RepositoryContract.RepositoryEntry.TABLE_NAME + " (" + RepositoryContract.RepositoryEntry._ID + ")" +
                " );";
        sqLiteDatabase.execSQL(SQL_CREATE_TRAFFIC_CLONES_TABLE);
        final String SQL_CREATE_TRAFFIC_PATHS_TABLE = "CREATE TABLE " + TrafficPathsContract.TrafficPathsEntry.TABLE_NAME
                + " (" +
                TrafficPathsContract.TrafficPathsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                TrafficPathsContract.TrafficPathsEntry.COLUMN_REPOSITORY_KEY + " INTEGER NOT NULL, " +
                TrafficPathsContract.TrafficPathsEntry.COLUMN_TRAFFIC_PATHS_PATH + " TEXT NOT NULL ," +
                TrafficPathsContract.TrafficPathsEntry.COLUMN_TRAFFIC_PATHS_TITLE + " TEXT NOT NULL ," +
                TrafficPathsContract.TrafficPathsEntry.COLUMN_TRAFFIC_PATHS_COUNT + " TEXT NOT NULL ," +
                TrafficPathsContract.TrafficPathsEntry.COLUMN_TRAFFIC_PATHS_UNIQUES + " TEXT NOT NULL ," +
                " FOREIGN KEY (" + TrafficPathsContract.TrafficPathsEntry.COLUMN_REPOSITORY_KEY + ") REFERENCES " +
                RepositoryContract.RepositoryEntry.TABLE_NAME + " (" + RepositoryContract.RepositoryEntry._ID + ")" +
                " );";
        sqLiteDatabase.execSQL(SQL_CREATE_TRAFFIC_PATHS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + RepositoryContract.RepositoryEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TrafficViewsContract.TrafficViewsEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TrafficPathsContract.TrafficPathsEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}