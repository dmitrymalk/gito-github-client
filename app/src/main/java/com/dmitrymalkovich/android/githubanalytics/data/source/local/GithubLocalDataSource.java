package com.dmitrymalkovich.android.githubanalytics.data.source.local;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.OperationApplicationException;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.annotation.StringDef;
import android.util.Log;

import com.dmitrymalkovich.android.githubanalytics.data.source.GithubDataSource;
import com.dmitrymalkovich.android.githubanalytics.data.source.local.contract.ClonesContract;
import com.dmitrymalkovich.android.githubanalytics.data.source.local.contract.ReferrerContract;
import com.dmitrymalkovich.android.githubanalytics.data.source.local.contract.RepositoryContract;
import com.dmitrymalkovich.android.githubanalytics.data.source.local.contract.StargazersContract;
import com.dmitrymalkovich.android.githubanalytics.data.source.local.contract.TrendingContract;
import com.dmitrymalkovich.android.githubanalytics.data.source.local.contract.ViewsContract;
import com.dmitrymalkovich.android.githubanalytics.data.source.remote.gson.ResponseClones;
import com.dmitrymalkovich.android.githubanalytics.data.source.remote.gson.ResponseReferrer;
import com.dmitrymalkovich.android.githubanalytics.data.source.remote.gson.ResponseStargazers;
import com.dmitrymalkovich.android.githubanalytics.data.source.remote.gson.ResponseTrending;
import com.dmitrymalkovich.android.githubanalytics.data.source.remote.gson.ResponseViews;

import org.eclipse.egit.github.core.Repository;

import java.io.IOException;
import java.lang.annotation.Retention;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.annotation.RetentionPolicy.SOURCE;

public class GithubLocalDataSource implements GithubDataSource {

    public static String LOG_TAG = GithubLocalDataSource.class.getSimpleName();
    private static final String PREFERENCES_TOKEN = "PREFERENCES_TOKEN";
    private static final String PREFERENCES_TOKEN_TYPE = "PREFERENCES_TOKEN_TYPE";

    @Retention(SOURCE)
    @StringDef({TRENDING_PERIOD_DAILY, TRENDING_PERIOD_WEEKLY, TRENDING_PERIOD_MONTHLY})
    public @interface TrendingPeriod {}
    public static final String TRENDING_PERIOD_DAILY = "daily";
    public static final String TRENDING_PERIOD_WEEKLY = "weekly";
    public static final String TRENDING_PERIOD_MONTHLY = "monthly";

    private static final String PREFERENCES_TRENDING_PERIOD = "PREFERENCES_TRENDING_PERIOD";

    @Retention(SOURCE)
    @StringDef({TRENDING_LANGUAGE_JAVA,
            TRENDING_LANGUAGE_C,
            TRENDING_LANGUAGE_RUBY,
            TRENDING_LANGUAGE_JAVASCRIPT,
            TRENDING_LANGUAGE_SWIFT,
            TRENDING_LANGUAGE_OBJECTIVE_C,
            TRENDING_LANGUAGE_C_PLUS_PLUS,
            TRENDING_LANGUAGE_PYTHON,
            TRENDING_LANGUAGE_C_SHARP,
            TRENDING_LANGUAGE_HTML})
    public @interface TrendingLanguage {}
    public static final String TRENDING_LANGUAGE_JAVA = "Java";
    public static final String TRENDING_LANGUAGE_C = "C";
    public static final String TRENDING_LANGUAGE_RUBY = "Ruby";
    public static final String TRENDING_LANGUAGE_JAVASCRIPT = "Javascript";
    public static final String TRENDING_LANGUAGE_SWIFT = "Swift";
    public static final String TRENDING_LANGUAGE_OBJECTIVE_C = "Objective-C";
    public static final String TRENDING_LANGUAGE_C_PLUS_PLUS = "C++";
    public static final String TRENDING_LANGUAGE_PYTHON = "Python";
    public static final String TRENDING_LANGUAGE_C_SHARP = "C#";
    public static final String TRENDING_LANGUAGE_HTML = "Html";

    private static final String PREFERENCES_TRENDING_LANGUAGE = "PREFERENCES_TRENDING_LANGUAGE";

    private static GithubLocalDataSource INSTANCE;
    @SuppressWarnings("all")
    private ContentResolver mContentResolver;
    private SharedPreferences mPreferences;

    public static GithubLocalDataSource getInstance(ContentResolver contentResolver,
                                                    SharedPreferences preferences) {
        if (INSTANCE == null) {
            INSTANCE = new GithubLocalDataSource(contentResolver, preferences);
        }
        return INSTANCE;
    }

    private GithubLocalDataSource(@NonNull ContentResolver contentResolver,
                                  SharedPreferences preferences) {
        checkNotNull(contentResolver);
        checkNotNull(preferences);
        mContentResolver = contentResolver;
        mPreferences = preferences;
    }

    @Override
    public void login(String login, String password) {
    }

    @Override
    public void getRepositories(GetRepositoriesCallback callback) {
    }

    @Override
    public void getRepositoryReferrers(Repository repository, GetRepositoryReferrersCallback callback) {

    }

    @Override
    public void requestTokenFromCode(String code, RequestTokenFromCodeCallback callback) {
    }

    @Override
    public void getRepositoryClones(Repository repository, String period,GetRepositoryClonesCallback callback) {
    }

    @Override
    public void getRepositoryViews(Repository repository, String period, GetRepositoryViewsCallback callback) {
    }

    @Override
    public void getStargazers(Repository repository, GetStargazersCallback callback) {
    }

    @Override
    public void getTrendingRepositories(String period, String language, GetTrendingRepositories callback) {
    }

    @Override
    public void getUser(GerUserCallback callback) {

    }

    @Override
    public void saveToken(String token, String tokenType) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putString(PREFERENCES_TOKEN, token);
        editor.putString(PREFERENCES_TOKEN_TYPE, tokenType);
        editor.apply();
    }

    @Override
    public String getToken() {
        return mPreferences.getString(PREFERENCES_TOKEN, null);
    }

    @Override
    public String getTokenType() {
        return mPreferences.getString(PREFERENCES_TOKEN_TYPE, null);
    }

    @Override
    public @TrendingLanguage String getDefaultLanguageForTrending() {
        String language = mPreferences.getString(PREFERENCES_TRENDING_LANGUAGE,
                TRENDING_LANGUAGE_JAVA);
        switch (language) {
            case TRENDING_LANGUAGE_C:
                return TRENDING_LANGUAGE_C;
            case TRENDING_LANGUAGE_RUBY:
                return TRENDING_LANGUAGE_RUBY;
            case TRENDING_LANGUAGE_JAVASCRIPT:
                return TRENDING_LANGUAGE_JAVASCRIPT;
            case TRENDING_LANGUAGE_SWIFT:
                return TRENDING_LANGUAGE_SWIFT;
            case TRENDING_LANGUAGE_OBJECTIVE_C:
                return TRENDING_LANGUAGE_OBJECTIVE_C;
            case TRENDING_LANGUAGE_C_PLUS_PLUS:
                return TRENDING_LANGUAGE_C_PLUS_PLUS;
            case TRENDING_LANGUAGE_PYTHON:
                return TRENDING_LANGUAGE_PYTHON;
            case TRENDING_LANGUAGE_C_SHARP:
                return TRENDING_LANGUAGE_C_SHARP;
            case TRENDING_LANGUAGE_HTML:
                return TRENDING_LANGUAGE_HTML;
            default:
            case TRENDING_LANGUAGE_JAVA:
                return TRENDING_LANGUAGE_JAVA;
        }
    }

    @Override
    public void setDefaultLanguageForTrending(@TrendingLanguage String language) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putString(PREFERENCES_TRENDING_LANGUAGE, language);
        editor.apply();
    }

    @Override
    public @TrendingPeriod String getDefaultPeriodForTrending() {
        String language = mPreferences.getString(PREFERENCES_TRENDING_PERIOD,
                TRENDING_PERIOD_DAILY);
        switch (language) {
            case TRENDING_PERIOD_DAILY:
                return TRENDING_PERIOD_DAILY;
            case TRENDING_PERIOD_WEEKLY:
                return TRENDING_PERIOD_WEEKLY;
            case TRENDING_PERIOD_MONTHLY:
                return TRENDING_PERIOD_MONTHLY;
            default:
                return TRENDING_PERIOD_DAILY;
        }
    }

    @Override
    public void setDefaultPeriodForTrending(@TrendingPeriod String period) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putString(PREFERENCES_TRENDING_PERIOD, period);
        editor.apply();
    }

    public void saveReferrers(long repositoryId, List<ResponseReferrer> referrerList) {
        mContentResolver.delete(ReferrerContract.ReferrerEntry.CONTENT_URI,
                ReferrerContract.ReferrerEntry.COLUMN_REPOSITORY_KEY + " =  ? ",
                new String[]{String.valueOf(repositoryId)});

        for (ResponseReferrer referrer : referrerList) {
            ContentValues referrerValues = ReferrerContract.ReferrerEntry
                    .createContentValues(repositoryId, referrer);

            mContentResolver.insert(
                    ReferrerContract.ReferrerEntry.CONTENT_URI,
                    referrerValues);
        }
    }

    public boolean saveRepositories(List<Repository> repositoryList) throws IOException {
        for (Repository repository : repositoryList) {
            ContentValues repositoryValues = RepositoryContract.RepositoryEntry.
                    buildContentValues(repository);

            Cursor cursor = mContentResolver.query(RepositoryContract.RepositoryEntry.CONTENT_URI,
                    new String[]{RepositoryContract.RepositoryEntry.COLUMN_REPOSITORY_ID},
                    RepositoryContract.RepositoryEntry.COLUMN_REPOSITORY_ID + " = " + repository.getId(),
                    null,
                    null);

            if (cursor != null && cursor.moveToFirst()) {
                mContentResolver.update(
                        RepositoryContract.RepositoryEntry.CONTENT_URI,
                        repositoryValues,
                        RepositoryContract.RepositoryEntry.COLUMN_REPOSITORY_ID + " = " + repository.getId(),
                        null);
            } else {
                mContentResolver.insert(
                        RepositoryContract.RepositoryEntry.CONTENT_URI,
                        repositoryValues);
            }

            if (cursor != null) {
                cursor.close();
            }
        }
        return true;
    }

    public void saveClones(long repositoryId, ResponseClones responseClones) {
        for (ResponseClones.Clone clone : responseClones.getClones()) {
            ContentValues contentValues = ClonesContract.ClonesEntry
                    .buildContentValues(repositoryId, clone);

            // ISO 8601 to milliseconds
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
            long timeInMilliseconds = 0;
            try {
                Date date = df.parse(clone.getTimestamp());
                timeInMilliseconds = date.getTime();
            } catch (ParseException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
            }

            String selection = ClonesContract.ClonesEntry.COLUMN_REPOSITORY_KEY + " = "
                    + repositoryId + " AND "
                    + ClonesContract.ClonesEntry.COLUMN_CLONES_TIMESTAMP
                    + " = " + timeInMilliseconds;

            Cursor cursor = mContentResolver.query(ClonesContract.ClonesEntry.CONTENT_URI,
                    new String[]{ClonesContract.ClonesEntry.COLUMN_REPOSITORY_KEY},
                    selection,
                    null,
                    null);

            if (cursor != null && cursor.moveToFirst()) {
                mContentResolver.update(
                        ClonesContract.ClonesEntry.CONTENT_URI,
                        contentValues,
                        selection,
                        null);
            } else {
                mContentResolver.insert(
                        ClonesContract.ClonesEntry.CONTENT_URI,
                        contentValues);
            }

            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public void saveViews(long repositoryId, ResponseViews responseViews) {
        for (ResponseViews.View view : responseViews.getViews()) {
            ContentValues contentValues = ViewsContract.ViewsEntry
                    .buildContentValues(repositoryId, view);

            // ISO 8601 to milliseconds
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
            long timeInMilliseconds = 0;
            try {
                Date date = df.parse(view.getTimestamp());
                timeInMilliseconds = date.getTime();
            } catch (ParseException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
            }

            String selection = ViewsContract.ViewsEntry.COLUMN_REPOSITORY_KEY + " = "
                    + repositoryId + " AND "
                    + ViewsContract.ViewsEntry.COLUMN_VIEWS_TIMESTAMP
                    + " = " + timeInMilliseconds;

            Cursor cursor = mContentResolver.query(ViewsContract.ViewsEntry.CONTENT_URI,
                    new String[]{ViewsContract.ViewsEntry.COLUMN_REPOSITORY_KEY},
                    selection,
                    null,
                    null);

            if (cursor != null && cursor.moveToFirst()) {
                mContentResolver.update(
                        ViewsContract.ViewsEntry.CONTENT_URI,
                        contentValues,
                        selection,
                        null);
            } else {
                mContentResolver.insert(
                        ViewsContract.ViewsEntry.CONTENT_URI,
                        contentValues);
            }

            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public void saveTrendingRepositories(String period, String language,
                                         List<ResponseTrending> responseTrendingList) {
        ArrayList<ContentProviderOperation> ops = new ArrayList<>();
        ops.add(ContentProviderOperation.newDelete(TrendingContract.TrendingEntry.CONTENT_URI)
                .withSelection(TrendingContract.TrendingEntry.COLUMN_LANGUAGE + " = ? AND " +
                                TrendingContract.TrendingEntry.COLUMN_PERIOD + " = ? ",
                        new String[]{language, period}).build());
        for (ResponseTrending repository : responseTrendingList) {
            ContentValues contentValues = TrendingContract.TrendingEntry
                    .buildContentValues(repository, period, language);
            ops.add(ContentProviderOperation
                    .newInsert(TrendingContract.TrendingEntry.CONTENT_URI)
                    .withValues(contentValues).build());
        }
        try {
            mContentResolver.applyBatch(TrendingContract.CONTENT_AUTHORITY, ops);
        } catch (RemoteException | OperationApplicationException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
        }
    }

    public void saveStargazerses(Repository repository, List<ResponseStargazers>
            responseStargazersList) {
        for (ResponseStargazers stargazers : responseStargazersList) {
            if (stargazers != null) {
                ContentValues contentValues = StargazersContract.Entry
                        .buildContentValues(repository.getId(), stargazers);

                // ISO 8601 to milliseconds
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
                long timeInMilliseconds = 0;
                try {
                    Date date = df.parse(stargazers.getStarredAt());
                    timeInMilliseconds = date.getTime();
                } catch (ParseException e) {
                    Log.e(LOG_TAG, e.getMessage(), e);
                }

                String selection = StargazersContract.Entry.COLUMN_REPOSITORY_KEY + " = "
                        + repository.getId() + " AND "
                        + StargazersContract.Entry.COLUMN_TIMESTAMP
                        + " = " + timeInMilliseconds;

                Cursor cursor = mContentResolver.query(ViewsContract.ViewsEntry.CONTENT_URI,
                        new String[]{StargazersContract.Entry.COLUMN_REPOSITORY_KEY},
                        selection,
                        null,
                        null);

                if (cursor == null || !cursor.moveToFirst()) {
                    mContentResolver.insert(
                            StargazersContract.Entry.CONTENT_URI,
                            contentValues);
                }

                if (cursor != null)
                {
                    cursor.close();
                }
            }
        }
    }
}
