package com.dmitrymalkovich.android.githubanalytics.data.source.local;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.dmitrymalkovich.android.githubanalytics.data.source.local.contract.ClonesContract;
import com.dmitrymalkovich.android.githubanalytics.data.source.local.contract.ReferrerContract;
import com.dmitrymalkovich.android.githubanalytics.data.source.local.contract.RepositoryContract;
import com.dmitrymalkovich.android.githubanalytics.data.source.local.contract.StargazersContract;
import com.dmitrymalkovich.android.githubanalytics.data.source.local.contract.TrendingContract;
import com.dmitrymalkovich.android.githubanalytics.data.source.local.contract.UserContract;
import com.dmitrymalkovich.android.githubanalytics.data.source.local.contract.ViewsContract;

class GithubAnalyticsDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 4;
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
                RepositoryContract.RepositoryEntry.COLUMN_REPOSITORY_LANGUAGE + " TEXT ," +
                RepositoryContract.RepositoryEntry.COLUMN_REPOSITORY_PINNED + " INTEGER DEFAULT 1" +
                " );";
        sqLiteDatabase.execSQL(SQL_CREATE_REPOSITORIES_TABLE);

        final String SQL_CREATE_TRAFFIC_VIEWS_TABLE = "CREATE TABLE " + ViewsContract.ViewsEntry.TABLE_NAME
                + " (" +
                ViewsContract.ViewsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                ViewsContract.ViewsEntry.COLUMN_REPOSITORY_KEY + " INTEGER NOT NULL, " +
                ViewsContract.ViewsEntry.COLUMN_VIEWS_COUNT + " TEXT NOT NULL ," +
                ViewsContract.ViewsEntry.COLUMN_VIEWS_UNIQUES + " TEXT NOT NULL ," +
                ViewsContract.ViewsEntry.COLUMN_VIEWS_TIMESTAMP + " DATE NOT NULL," +
                " FOREIGN KEY (" + ViewsContract.ViewsEntry.COLUMN_REPOSITORY_KEY + ") REFERENCES " +
                RepositoryContract.RepositoryEntry.TABLE_NAME + " (" + RepositoryContract.RepositoryEntry._ID + ")" +
                " );";
        sqLiteDatabase.execSQL(SQL_CREATE_TRAFFIC_VIEWS_TABLE);

        final String SQL_CREATE_TRAFFIC_CLONES_TABLE = "CREATE TABLE " + ClonesContract.ClonesEntry.TABLE_NAME
                + " (" +
                ClonesContract.ClonesEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                ClonesContract.ClonesEntry.COLUMN_REPOSITORY_KEY + " INTEGER NOT NULL, " +
                ClonesContract.ClonesEntry.COLUMN_CLONES_COUNT + " TEXT NOT NULL ," +
                ClonesContract.ClonesEntry.COLUMN_CLONES_UNIQUES + " TEXT NOT NULL ," +
                ClonesContract.ClonesEntry.COLUMN_CLONES_TIMESTAMP + " DATE NOT NULL," +
                " FOREIGN KEY (" + ClonesContract.ClonesEntry.COLUMN_REPOSITORY_KEY + ") REFERENCES " +
                RepositoryContract.RepositoryEntry.TABLE_NAME + " (" + RepositoryContract.RepositoryEntry._ID + ")" +
                " );";
        sqLiteDatabase.execSQL(SQL_CREATE_TRAFFIC_CLONES_TABLE);

        final String SQL_CREATE_TRAFFIC_REFERRER_TABLE = "CREATE TABLE " + ReferrerContract.ReferrerEntry.TABLE_NAME
                + " (" +
                ReferrerContract.ReferrerEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                ReferrerContract.ReferrerEntry.COLUMN_REPOSITORY_KEY + " INTEGER NOT NULL, " +
                ReferrerContract.ReferrerEntry.COLUMN_REFERRER_REFERRER + " TEXT NOT NULL ," +
                ReferrerContract.ReferrerEntry.COLUMN_REFERRER_COUNT + " INT NOT NULL ," +
                ReferrerContract.ReferrerEntry.COLUMN_REFERRER_UNIQUES + " INT NOT NULL ," +
                " FOREIGN KEY (" + ReferrerContract.ReferrerEntry.COLUMN_REPOSITORY_KEY + ") REFERENCES " +
                RepositoryContract.RepositoryEntry.TABLE_NAME + " (" + RepositoryContract.RepositoryEntry._ID + ")" +
                " );";
        sqLiteDatabase.execSQL(SQL_CREATE_TRAFFIC_REFERRER_TABLE);

        final String SQL_CREATE_TRENDING_TABLE = "CREATE TABLE " + TrendingContract.TrendingEntry.TABLE_NAME
                + " (" +
                TrendingContract.TrendingEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                TrendingContract.TrendingEntry.COLUMN_HTML_URL + " TEXT, " +
                TrendingContract.TrendingEntry.COLUMN_DESCRIPTION + " TEXT," +
                TrendingContract.TrendingEntry.COLUMN_LANGUAGE + " TEXT ," +
                TrendingContract.TrendingEntry.COLUMN_AVATAR + " DATE ," +
                TrendingContract.TrendingEntry.COLUMN_NAME + " TEXT ," +
                TrendingContract.TrendingEntry.COLUMN_WATCHER_COUNT + " INTEGER ," +
                TrendingContract.TrendingEntry.COLUMN_PERIOD + " TEXT" +
                " );";
        sqLiteDatabase.execSQL(SQL_CREATE_TRENDING_TABLE);

        final String SQL_CREATE_STARGAZERS_TABLE = "CREATE TABLE " + StargazersContract.Entry.TABLE_NAME
                + " (" +
                StargazersContract.Entry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                StargazersContract.Entry.COLUMN_REPOSITORY_KEY + " INTEGER NOT NULL, " +
                StargazersContract.Entry.COLUMN_TIMESTAMP + " DATE NOT NULL ," +
                " FOREIGN KEY (" + StargazersContract.Entry.COLUMN_REPOSITORY_KEY + ") REFERENCES " +
                RepositoryContract.RepositoryEntry.TABLE_NAME + " (" + RepositoryContract.RepositoryEntry._ID + ")" +
                " );";
        sqLiteDatabase.execSQL(SQL_CREATE_STARGAZERS_TABLE);

        createUsersTable(sqLiteDatabase);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            createUsersTable(sqLiteDatabase);
        }

        if (oldVersion < 3) {
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + RepositoryContract.RepositoryEntry.TABLE_NAME);
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ViewsContract.ViewsEntry.TABLE_NAME);
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ReferrerContract.ReferrerEntry.TABLE_NAME);
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ClonesContract.ClonesEntry.TABLE_NAME);
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TrendingContract.TrendingEntry.TABLE_NAME);
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + StargazersContract.Entry.TABLE_NAME);
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + UserContract.UsersEntry.TABLE_NAME);
            onCreate(sqLiteDatabase);
        }

        if (oldVersion < 4) {
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + RepositoryContract.RepositoryEntry.TABLE_NAME);
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ViewsContract.ViewsEntry.TABLE_NAME);
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ReferrerContract.ReferrerEntry.TABLE_NAME);
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ClonesContract.ClonesEntry.TABLE_NAME);
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TrendingContract.TrendingEntry.TABLE_NAME);
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + StargazersContract.Entry.TABLE_NAME);
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + UserContract.UsersEntry.TABLE_NAME);
            onCreate(sqLiteDatabase);
        }
    }

    private void createUsersTable(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_USERS_TABLE = "CREATE TABLE " + UserContract.UsersEntry.TABLE_NAME
                + " (" +
                UserContract.UsersEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                UserContract.UsersEntry.COLUMN_NAME + " TEXT NULL, " +
                UserContract.UsersEntry.COLUMN_LOGIN + " TEXT NULL, " +
                UserContract.UsersEntry.COLUMN_AVATAR + " TEXT NULL, " +
                UserContract.UsersEntry.COLUMN_FOLLOWERS + " TEXT NULL " +
                " );";
        sqLiteDatabase.execSQL(SQL_CREATE_USERS_TABLE);
    }
}