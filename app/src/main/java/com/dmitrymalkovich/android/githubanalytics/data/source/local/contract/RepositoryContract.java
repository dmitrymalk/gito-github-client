package com.dmitrymalkovich.android.githubanalytics.data.source.local.contract;

import android.content.ContentUris;
import android.content.ContentValues;
import android.net.Uri;
import android.provider.BaseColumns;

import android.content.ContentResolver;

import org.eclipse.egit.github.core.Repository;

import java.io.File;

/**
 * GitHub API: https://developer.github.com/v3/repos/
 */
@SuppressWarnings("unused")
public class RepositoryContract {

    public static final String CONTENT_AUTHORITY = "com.dmitrymalkovich.android.githubanalytics.data";
    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_REPOSITORY = "repository";
    public static final String PATH_REPOSITORY_STARGAZERS = "repository_stargazers";

    public static final class RepositoryEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_REPOSITORY).build();

        public static final Uri CONTENT_URI_REPOSITORY_STARGAZERS =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_REPOSITORY_STARGAZERS).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_REPOSITORY;

        public static final String CONTENT_TYPE_REPOSITORY_STARGAZERS =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_REPOSITORY_STARGAZERS;

        public static final String TABLE_NAME = "repository";
        public static final String COLUMN_REPOSITORY_ID = "repository_id";
        public static final String COLUMN_REPOSITORY_NAME = "repository_name";
        public static final String COLUMN_REPOSITORY_FULL_NAME = "repository_full_name";
        public static final String COLUMN_REPOSITORY_DESCRIPTION = "repository_description";
        public static final String COLUMN_REPOSITORY_PRIVATE = "repository_private";
        public static final String COLUMN_REPOSITORY_FORK = "repository_fork";
        public static final String COLUMN_REPOSITORY_URL = "repository_url";
        public static final String COLUMN_REPOSITORY_HTML_URL = "repository_html_url";
        public static final String COLUMN_REPOSITORY_FORKS = "repository_forks";
        public static final String COLUMN_REPOSITORY_WATCHERS = "repository_watchers";
        public static final String COLUMN_REPOSITORY_LANGUAGE = "repository_language";

        public static Uri buildRepositoryUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static final String[] REPOSITORY_COLUMNS = {
                TABLE_NAME + "." + _ID,
                TABLE_NAME + "." + COLUMN_REPOSITORY_ID,
                TABLE_NAME + "." + COLUMN_REPOSITORY_NAME,
                TABLE_NAME + "." + COLUMN_REPOSITORY_FULL_NAME,
                TABLE_NAME + "." + COLUMN_REPOSITORY_DESCRIPTION,
                TABLE_NAME + "." + COLUMN_REPOSITORY_PRIVATE,
                TABLE_NAME + "." + COLUMN_REPOSITORY_FORK,
                TABLE_NAME + "." + COLUMN_REPOSITORY_URL,
                TABLE_NAME + "." + COLUMN_REPOSITORY_HTML_URL,
                TABLE_NAME + "." + COLUMN_REPOSITORY_FORKS,
                TABLE_NAME + "." + COLUMN_REPOSITORY_WATCHERS,
                TABLE_NAME + "." + COLUMN_REPOSITORY_LANGUAGE
        };

        public static final String[] REPOSITORY_COLUMNS_WITH_ADDITIONAL_INFO = {
                TABLE_NAME + "." + _ID,
                TABLE_NAME + "." + COLUMN_REPOSITORY_ID,
                TABLE_NAME + "." + COLUMN_REPOSITORY_NAME,
                TABLE_NAME + "." + COLUMN_REPOSITORY_FULL_NAME,
                TABLE_NAME + "." + COLUMN_REPOSITORY_DESCRIPTION,
                TABLE_NAME + "." + COLUMN_REPOSITORY_PRIVATE,
                TABLE_NAME + "." + COLUMN_REPOSITORY_FORK,
                TABLE_NAME + "." + COLUMN_REPOSITORY_URL,
                TABLE_NAME + "." + COLUMN_REPOSITORY_HTML_URL,
                TABLE_NAME + "." + COLUMN_REPOSITORY_FORKS,
                TABLE_NAME + "." + COLUMN_REPOSITORY_WATCHERS,
                TABLE_NAME + "." + COLUMN_REPOSITORY_LANGUAGE,
                StargazersContract.Entry.TABLE_NAME + "." + StargazersContract.Entry.COLUMN_REPOSITORY_KEY,
                StargazersContract.Entry.TABLE_NAME + "." + "stars",
                StargazersContract.Entry.TABLE_NAME + "_yesterday" + "." + "stars",
                StargazersContract.Entry.TABLE_NAME + "_two_weeks" + "." + "stars",
                ViewsContract.ViewsEntry.TABLE_NAME + "." + ViewsContract.ViewsEntry.COLUMN_REPOSITORY_KEY,
                ViewsContract.ViewsEntry.TABLE_NAME + "." + ViewsContract.ViewsEntry.COLUMN_VIEWS_UNIQUES,
                ViewsContract.ViewsEntry.TABLE_NAME + "." + ViewsContract.ViewsEntry.COLUMN_VIEWS_COUNT,
                ViewsContract.ViewsEntry.TABLE_NAME + "_yesterday" + "." + ViewsContract.ViewsEntry.COLUMN_REPOSITORY_KEY,
                ViewsContract.ViewsEntry.TABLE_NAME + "_yesterday" + "." + ViewsContract.ViewsEntry.COLUMN_VIEWS_UNIQUES,
                ViewsContract.ViewsEntry.TABLE_NAME + "_yesterday" + "." + ViewsContract.ViewsEntry.COLUMN_VIEWS_COUNT,
                ViewsContract.ViewsEntry.TABLE_NAME + "_two_weeks" + "." + ViewsContract.ViewsEntry.COLUMN_REPOSITORY_KEY,
                ViewsContract.ViewsEntry.TABLE_NAME + "_two_weeks" + "." + ViewsContract.ViewsEntry.COLUMN_VIEWS_UNIQUES,
                ViewsContract.ViewsEntry.TABLE_NAME + "_two_weeks" + "." + ViewsContract.ViewsEntry.COLUMN_VIEWS_COUNT,
                ClonesContract.ClonesEntry.TABLE_NAME + "." + ViewsContract.ViewsEntry.COLUMN_REPOSITORY_KEY,
                ClonesContract.ClonesEntry.TABLE_NAME + "." + ViewsContract.ViewsEntry.COLUMN_VIEWS_UNIQUES,
                ClonesContract.ClonesEntry.TABLE_NAME + "." + ViewsContract.ViewsEntry.COLUMN_VIEWS_COUNT,
                ClonesContract.ClonesEntry.TABLE_NAME + "_yesterday" + "." + ViewsContract.ViewsEntry.COLUMN_REPOSITORY_KEY,
                ClonesContract.ClonesEntry.TABLE_NAME + "_yesterday" + "." + ViewsContract.ViewsEntry.COLUMN_VIEWS_UNIQUES,
                ClonesContract.ClonesEntry.TABLE_NAME + "_yesterday" + "." + ViewsContract.ViewsEntry.COLUMN_VIEWS_COUNT,
                ClonesContract.ClonesEntry.TABLE_NAME + "_two_weeks" + "." + ViewsContract.ViewsEntry.COLUMN_REPOSITORY_KEY,
                ClonesContract.ClonesEntry.TABLE_NAME + "_two_weeks" + "." + ViewsContract.ViewsEntry.COLUMN_VIEWS_UNIQUES,
                ClonesContract.ClonesEntry.TABLE_NAME + "_two_weeks" + "." + ViewsContract.ViewsEntry.COLUMN_VIEWS_COUNT,
                ReferrerContract.ReferrerEntry.TABLE_NAME + "_1"+ "." + ReferrerContract.ReferrerEntry._ID,
                ReferrerContract.ReferrerEntry.TABLE_NAME + "_1" + "." + ReferrerContract.ReferrerEntry.COLUMN_REPOSITORY_KEY,
                ReferrerContract.ReferrerEntry.TABLE_NAME + "_1" + "." + ReferrerContract.ReferrerEntry.COLUMN_REFERRER_REFERRER,
                ReferrerContract.ReferrerEntry.TABLE_NAME + "_1" + "." + ReferrerContract.ReferrerEntry.COLUMN_REFERRER_COUNT,
                ReferrerContract.ReferrerEntry.TABLE_NAME + "_1" + "." + ReferrerContract.ReferrerEntry.COLUMN_REFERRER_UNIQUES,
                ReferrerContract.ReferrerEntry.TABLE_NAME + "_2"+ "." + ReferrerContract.ReferrerEntry._ID,
                ReferrerContract.ReferrerEntry.TABLE_NAME + "_2" + "." + ReferrerContract.ReferrerEntry.COLUMN_REPOSITORY_KEY,
                ReferrerContract.ReferrerEntry.TABLE_NAME + "_2" + "." + ReferrerContract.ReferrerEntry.COLUMN_REFERRER_REFERRER,
                ReferrerContract.ReferrerEntry.TABLE_NAME + "_2" + "." + ReferrerContract.ReferrerEntry.COLUMN_REFERRER_COUNT,
                ReferrerContract.ReferrerEntry.TABLE_NAME + "_2" + "." + ReferrerContract.ReferrerEntry.COLUMN_REFERRER_UNIQUES
        };

        public static final int COL_ID = 0;
        public static final int COL_REPOSITORY_ID = 1;
        public static final int COL_REPOSITORY_NAME = 2;
        public static final int COL_REPOSITORY_FULL_NAME = 3;
        public static final int COL_REPOSITORY_DESCRIPTION = 4;
        public static final int COL_REPOSITORY_PRIVATE = 5;
        public static final int COL_REPOSITORY_FORK = 6;
        public static final int COL_REPOSITORY_URL = 7;
        public static final int COL_REPOSITORY_HTML_URL = 8;
        public static final int COL_REPOSITORY_FORKS = 9;
        public static final int COL_REPOSITORY_WATCHERS = 10;
        public static final int COL_REPOSITORY_LANGUAGE = 11;
        public static final int COL_STARGAZERS_ID = 12;
        public static final int COL_STARGAZERS_STARS = 13;
        public static final int COL_STARGAZERS_STARS_YESTERDAY = 14;
        public static final int COL_STARGAZERS_STARS_TWO_WEEKS = 15;
        public static final int COL_VIEWS_ID = 16;
        public static final int COL_VIEWS_UNIQUES = 17;
        public static final int COL_VIEWS_COUNT = 18;
        public static final int COL_VIEWS_ID_YESTERDAY = 19;
        public static final int COL_VIEWS_UNIQUES_YESTERDAY = 20;
        public static final int COL_VIEWS_COUNT_YESTERDAY = 21;
        public static final int COL_VIEWS_ID_TWO_WEEKS = 22;
        public static final int COL_VIEWS_UNIQUES_TWO_WEEKS = 23;
        public static final int COL_VIEWS_COUNT_TWO_WEEKS = 24;
        public static final int COL_CLONES_ID = 25;
        public static final int COL_CLONES_UNIQUES = 26;
        public static final int COL_CLONES_COUNT = 27;
        public static final int COL_CLONES_ID_YESTERDAY = 28;
        public static final int COL_CLONES_UNIQUES_YESTERDAY = 29;
        public static final int COL_CLONES_COUNT_YESTERDAY = 30;
        public static final int COL_CLONES_ID_TWO_WEEKS = 31;
        public static final int COL_CLONES_UNIQUES_TWO_WEEKS = 32;
        public static final int COL_CLONES_COUNT_TWO_WEEKS = 33;
        public static final int COL_REFERRER_1_ID = 34;
        public static final int COL_REFERRER_1_REPOSITORY_KEY = 35;
        public static final int COL_REFERRER_1_PATHS_REFERRER = 36;
        public static final int COL_REFERRER_1_PATHS_COUNT = 37;
        public static final int COL_REFERRER_1_PATHS_UNIQUES = 38;
        public static final int COL_REFERRER_2_ID = 39;
        public static final int COL_REFERRER_2_REPOSITORY_KEY = 40;
        public static final int COL_REFERRER_2_PATHS_REFERRER = 41;
        public static final int COL_REFERRER_2_PATHS_COUNT = 42;
        public static final int COL_REFERRER_2_PATHS_UNIQUES = 43;

        public static ContentValues buildContentValues(Repository repo) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(RepositoryContract.RepositoryEntry.COLUMN_REPOSITORY_ID,
                    repo.getId());
            contentValues.put(RepositoryContract.RepositoryEntry.COLUMN_REPOSITORY_NAME,
                    repo.getName());
            contentValues.put(RepositoryContract.RepositoryEntry.COLUMN_REPOSITORY_FULL_NAME,
                    repo.getOwner().getName() + File.separator + repo.getName());
            contentValues.put(RepositoryContract.RepositoryEntry.COLUMN_REPOSITORY_DESCRIPTION,
                    repo.getDescription());
            contentValues.put(RepositoryContract.RepositoryEntry.COLUMN_REPOSITORY_PRIVATE,
                    repo.isPrivate());
            contentValues.put(RepositoryContract.RepositoryEntry.COLUMN_REPOSITORY_FORK,
                    repo.isFork());
            contentValues.put(RepositoryContract.RepositoryEntry.COLUMN_REPOSITORY_URL,
                    repo.getUrl());
            contentValues.put(RepositoryContract.RepositoryEntry.COLUMN_REPOSITORY_HTML_URL,
                    repo.getHtmlUrl());
            contentValues.put(RepositoryContract.RepositoryEntry.COLUMN_REPOSITORY_FORKS,
                    repo.getForks());
            contentValues.put(RepositoryContract.RepositoryEntry.COLUMN_REPOSITORY_WATCHERS,
                    repo.getWatchers());
            contentValues.put(RepositoryContract.RepositoryEntry.COLUMN_REPOSITORY_LANGUAGE,
                    repo.getLanguage());
            return contentValues;
        }
    }
}