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
package com.dmitrymalkovich.android.githubanalytics.data.source.local;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.OperationApplicationException;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.annotation.StringDef;

import com.dmitrymalkovich.android.githubapi.core.TimeConverter;
import com.dmitrymalkovich.android.githubapi.core.gson.Clones;
import com.dmitrymalkovich.android.githubapi.core.gson.Star;
import com.dmitrymalkovich.android.githubapi.core.gson.User;
import com.dmitrymalkovich.android.githubapi.core.gson.Views;
import com.dmitrymalkovich.android.githubanalytics.data.source.GithubDataSource;
import com.dmitrymalkovich.android.githubanalytics.data.source.local.contract.ClonesContract;
import com.dmitrymalkovich.android.githubanalytics.data.source.local.contract.ReferrerContract;
import com.dmitrymalkovich.android.githubanalytics.data.source.local.contract.RepositoryContract;
import com.dmitrymalkovich.android.githubanalytics.data.source.local.contract.StargazersContract;
import com.dmitrymalkovich.android.githubanalytics.data.source.local.contract.TrendingContract;
import com.dmitrymalkovich.android.githubanalytics.data.source.local.contract.UserContract;
import com.dmitrymalkovich.android.githubanalytics.data.source.local.contract.ViewsContract;
import com.dmitrymalkovich.android.githubapi.core.gson.ReferringSite;
import com.dmitrymalkovich.android.githubapi.core.gson.TrendingRepository;
import com.google.firebase.crash.FirebaseCrash;

import org.eclipse.egit.github.core.Repository;

import java.io.IOException;
import java.lang.annotation.Retention;
import java.util.ArrayList;
import java.util.List;

import static java.lang.annotation.RetentionPolicy.SOURCE;

public class GithubLocalDataSource implements GithubDataSource {

    public static String LOG_TAG = GithubLocalDataSource.class.getSimpleName();
    private static final String PREFERENCES_TOKEN = "PREFERENCES_TOKEN";
    private static final String PREFERENCES_TOKEN_TYPE = "PREFERENCES_TOKEN_TYPE";

    private static final String PREFERENCES_TRENDING_PERIOD = "PREFERENCES_TRENDING_PERIOD";
    private static final String PREFERENCES_TRENDING_LANGUAGE = "PREFERENCES_TRENDING_LANGUAGE";

    @Retention(SOURCE)
    @StringDef({TrendingPeriod.DAILY, TrendingPeriod.WEEKLY, TrendingPeriod.MONTHLY})
    public @interface TrendingPeriod {
        String DAILY = "daily";
        String WEEKLY = "weekly";
        String MONTHLY = "monthly";
    }

    @Retention(SOURCE)
    @StringDef({TrendingLanguage.JAVA,
            TrendingLanguage.C,
            TrendingLanguage.RUBY,
            TrendingLanguage.JAVASCRIPT,
            TrendingLanguage.SWIFT,
            TrendingLanguage.OBJECTIVE_C,
            TrendingLanguage.C_PLUS_PLUS,
            TrendingLanguage.PYTHON,
            TrendingLanguage.C_SHARP,
            TrendingLanguage.HTML})
    public @interface TrendingLanguage {
        String JAVA = "Java";
        String C = "C";
        String RUBY = "Ruby";
        String JAVASCRIPT = "Javascript";
        String SWIFT = "Swift";
        String OBJECTIVE_C = "Objective-C";
        String C_PLUS_PLUS = "C++";
        String PYTHON = "Python";
        String C_SHARP = "C#";
        String HTML = "Html";
    }

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
        mContentResolver = contentResolver;
        mPreferences = preferences;
    }

    @Override
    public void logout() {
        mContentResolver.delete(RepositoryContract.RepositoryEntry.CONTENT_URI, null, null);
        mPreferences.edit().clear().apply();
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
    public void getRepositoryClones(Repository repository, String period, GetRepositoryClonesCallback callback) {
    }

    @Override
    public void getRepositoryViews(Repository repository, String period, GetRepositoryViewsCallback callback) {
    }

    @Override
    public void getStargazers(Repository repository, GetStargazersCallback callback) {
    }

    @Override
    public void getRepositoriesWithAdditionalInfo(GetRepositoriesCallback callback, boolean useCache) {
    }

    @Override
    public void getRepositoriesWithAdditionalInfo(long repositoryId, GetRepositoriesCallback callback,
                                                  boolean useCache) {
    }

    @Override
    public void getTrendingRepositories(String period, String language, GetTrendingRepositories callback,
                                        boolean useCache) {
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
    public
    @TrendingLanguage
    String getDefaultLanguageForTrending() {
        String language = mPreferences.getString(PREFERENCES_TRENDING_LANGUAGE,
                TrendingLanguage.JAVA);
        switch (language) {
            case TrendingLanguage.C:
                return TrendingLanguage.C;
            case TrendingLanguage.RUBY:
                return TrendingLanguage.RUBY;
            case TrendingLanguage.JAVASCRIPT:
                return TrendingLanguage.JAVASCRIPT;
            case TrendingLanguage.SWIFT:
                return TrendingLanguage.SWIFT;
            case TrendingLanguage.OBJECTIVE_C:
                return TrendingLanguage.OBJECTIVE_C;
            case TrendingLanguage.C_PLUS_PLUS:
                return TrendingLanguage.C_PLUS_PLUS;
            case TrendingLanguage.PYTHON:
                return TrendingLanguage.PYTHON;
            case TrendingLanguage.C_SHARP:
                return TrendingLanguage.C_SHARP;
            case TrendingLanguage.HTML:
                return TrendingLanguage.HTML;
            default:
            case TrendingLanguage.JAVA:
                return TrendingLanguage.JAVA;
        }
    }

    @Override
    public void setDefaultLanguageForTrending(@TrendingLanguage String language) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putString(PREFERENCES_TRENDING_LANGUAGE, language);
        editor.apply();
    }

    @Override
    public
    @TrendingPeriod
    String getDefaultPeriodForTrending() {
        String language = mPreferences.getString(PREFERENCES_TRENDING_PERIOD,
                TrendingPeriod.DAILY);
        switch (language) {
            case TrendingPeriod.DAILY:
                return TrendingPeriod.DAILY;
            case TrendingPeriod.WEEKLY:
                return TrendingPeriod.WEEKLY;
            case TrendingPeriod.MONTHLY:
                return TrendingPeriod.MONTHLY;
            default:
                return TrendingPeriod.DAILY;
        }
    }

    @Override
    public void setDefaultPeriodForTrending(@TrendingPeriod String period) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putString(PREFERENCES_TRENDING_PERIOD, period);
        editor.apply();
    }

    public void saveReferringSites(long repositoryId, List<ReferringSite> referrers) {
        Uri uri = ReferrerContract.ReferrerEntry.CONTENT_URI;

        mContentResolver.delete(uri,
                ReferrerContract.ReferrerEntry.COLUMN_REPOSITORY_KEY + " =  ? ",
                new String[]{String.valueOf(repositoryId)});

        for (ReferringSite referrer : referrers) {
            ContentValues referrerValues = ReferrerContract.ReferrerEntry
                    .createContentValues(repositoryId, referrer);

            mContentResolver.insert(uri, referrerValues);
        }
    }

    @Override
    public void setPinned(boolean active, long id) {
        Uri uri = RepositoryContract.RepositoryEntry.CONTENT_URI;
        ContentValues contentValues = new ContentValues();
        contentValues.put(RepositoryContract.RepositoryEntry.COLUMN_REPOSITORY_PINNED, active ? 1 : 0);
        mContentResolver.update(uri, contentValues,
                RepositoryContract.RepositoryEntry.COLUMN_REPOSITORY_ID + " = "
                        + id, null);
    }

    public boolean saveRepositories(List<Repository> repositories) throws IOException {
        Uri uri = RepositoryContract.RepositoryEntry.CONTENT_URI;
        for (Repository repository : repositories) {
            ContentValues repositoryValues = RepositoryContract.RepositoryEntry.
                    buildContentValues(repository);

            Cursor cursor = mContentResolver.query(uri,
                    new String[]{RepositoryContract.RepositoryEntry.COLUMN_REPOSITORY_ID},
                    RepositoryContract.RepositoryEntry.COLUMN_REPOSITORY_ID + " = "
                            + repository.getId(), null, null);

            if (cursor != null && cursor.moveToFirst()) {
                mContentResolver.update(uri, repositoryValues,
                        RepositoryContract.RepositoryEntry.COLUMN_REPOSITORY_ID + " = "
                                + repository.getId(), null);
            } else {
                mContentResolver.insert(uri, repositoryValues);
            }

            if (cursor != null) {
                cursor.close();
            }
        }
        return true;
    }

    public void saveClones(long repositoryId, Clones clones) {
        Uri uri = ClonesContract.ClonesEntry.CONTENT_URI;

        for (Clones.Clone clone : clones.asList()) {
            ContentValues contentValues = ClonesContract.ClonesEntry
                    .buildContentValues(repositoryId, clone);

            String timestamp = clone.getTimestamp();
            long timeInMilliseconds = TimeConverter.iso8601ToMilliseconds(timestamp);

            String selection = ClonesContract.ClonesEntry.COLUMN_REPOSITORY_KEY + " = "
                    + repositoryId + " AND "
                    + ClonesContract.ClonesEntry.COLUMN_CLONES_TIMESTAMP
                    + " = " + timeInMilliseconds;

            Cursor cursor = mContentResolver.query(uri,
                    new String[]{ClonesContract.ClonesEntry.COLUMN_REPOSITORY_KEY},
                    selection, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                mContentResolver.update(uri, contentValues, selection, null);
            } else {
                mContentResolver.insert(uri, contentValues);
            }

            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public void saveViews(long repositoryId, Views views) {
        Uri uri = ViewsContract.ViewsEntry.CONTENT_URI;

        for (Views.View view : views.getViews()) {
            ContentValues contentValues = ViewsContract.ViewsEntry
                    .buildContentValues(repositoryId, view);

            String timestamp = view.getTimestamp();
            long timeInMilliseconds = TimeConverter.iso8601ToMilliseconds(timestamp);

            String selection = ViewsContract.ViewsEntry.COLUMN_REPOSITORY_KEY + " = "
                    + repositoryId + " AND "
                    + ViewsContract.ViewsEntry.COLUMN_VIEWS_TIMESTAMP
                    + " = " + timeInMilliseconds;

            Cursor cursor = mContentResolver.query(uri,
                    new String[]{ViewsContract.ViewsEntry.COLUMN_REPOSITORY_KEY},
                    selection, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                mContentResolver.update(uri, contentValues, selection, null);
            } else {
                mContentResolver.insert(uri, contentValues);
            }

            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public void saveUser(User user) {
        if (user != null) {
            Uri uri = UserContract.UsersEntry.CONTENT_URI;
            ContentValues contentValues = UserContract.UsersEntry.buildContentValues(user);
            mContentResolver.delete(uri, null, null);
            mContentResolver.insert(uri, contentValues);
        }
    }

    public void saveTrendingRepositories(String period, String language,
                                         List<TrendingRepository> repositories) {
        Uri uri = TrendingContract.TrendingEntry.CONTENT_URI;

        ArrayList<ContentProviderOperation> ops = new ArrayList<>();

        ops.add(ContentProviderOperation.newDelete(uri)
                .withSelection(TrendingContract.TrendingEntry.COLUMN_LANGUAGE + " = ? AND " +
                                TrendingContract.TrendingEntry.COLUMN_PERIOD + " = ? ",
                        new String[]{language, period}).build());

        for (TrendingRepository repository : repositories) {

            repository.setLanguage(language);
            repository.setPeriod(period);

            ContentValues contentValues = TrendingContract.TrendingEntry
                    .buildContentValues(repository, period, language);
            ops.add(ContentProviderOperation.newInsert(uri)
                    .withValues(contentValues).build());
        }
        try {
            mContentResolver.applyBatch(TrendingContract.CONTENT_AUTHORITY, ops);
            mContentResolver.notifyChange(uri, null);
        } catch (RemoteException | OperationApplicationException e) {
            FirebaseCrash.report(e);
        }
    }

    public void saveStargazers(Repository repository, List<Star>
            starList) {
        Uri uri = StargazersContract.Entry.CONTENT_URI;

        for (Star stargazers : starList) {
            if (stargazers != null) {

                ContentValues contentValues = StargazersContract.Entry
                        .buildContentValues(repository.getId(), stargazers);

                String starredAd = stargazers.getStarredAt();
                long timeInMilliseconds = TimeConverter.iso8601ToMilliseconds(starredAd);

                String selection = StargazersContract.Entry.COLUMN_REPOSITORY_KEY + " = "
                        + repository.getId() + " AND "
                        + StargazersContract.Entry.COLUMN_TIMESTAMP
                        + " = " + timeInMilliseconds;

                Cursor cursor = mContentResolver.query(uri,
                        new String[]{StargazersContract.Entry.COLUMN_REPOSITORY_KEY},
                        selection, null, null);

                if (cursor == null || !cursor.moveToFirst()) {
                    mContentResolver.insert(uri, contentValues);
                }

                if (cursor != null) {
                    cursor.close();
                }
            }
        }
    }
}
