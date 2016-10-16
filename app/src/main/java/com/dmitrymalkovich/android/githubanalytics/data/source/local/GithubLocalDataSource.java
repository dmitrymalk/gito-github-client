package com.dmitrymalkovich.android.githubanalytics.data.source.local;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.support.annotation.NonNull;

import com.dmitrymalkovich.android.githubanalytics.data.source.GithubDataSource;
import com.dmitrymalkovich.android.githubanalytics.data.source.local.contract.ClonesContract;
import com.dmitrymalkovich.android.githubanalytics.data.source.local.contract.ReferrerContract;
import com.dmitrymalkovich.android.githubanalytics.data.source.local.contract.RepositoryContract;
import com.dmitrymalkovich.android.githubanalytics.data.source.local.contract.TrendingContract;
import com.dmitrymalkovich.android.githubanalytics.data.source.local.contract.ViewsContract;
import com.dmitrymalkovich.android.githubanalytics.data.source.remote.gson.ResponseClones;
import com.dmitrymalkovich.android.githubanalytics.data.source.remote.gson.ResponseReferrer;
import com.dmitrymalkovich.android.githubanalytics.data.source.remote.gson.ResponseTrending;
import com.dmitrymalkovich.android.githubanalytics.data.source.remote.gson.ResponseViews;

import org.eclipse.egit.github.core.Repository;

import java.io.IOException;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class GithubLocalDataSource implements GithubDataSource {

    private static final String PREFERENCES_TOKEN = "PREFERENCES_TOKEN";
    private static final String PREFERENCES_TOKEN_TYPE = "PREFERENCES_TOKEN_TYPE";
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
    public void getRepositoryClones(Repository repository, GetRepositoryClonesCallback callback) {
    }

    @Override
    public void getRepositoryViews(Repository repository, GetRepositoryViewsCallback callback) {
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

            String selection = ClonesContract.ClonesEntry.COLUMN_REPOSITORY_KEY + " = "
                    + repositoryId + " AND "
                    + ClonesContract.ClonesEntry.COLUMN_CLONES_TIMESTAMP
                    + " = " + clone.getTimestamp();

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

            String selection = ViewsContract.ViewsEntry.COLUMN_REPOSITORY_KEY + " = "
                    + repositoryId + " AND "
                    + ViewsContract.ViewsEntry.COLUMN_VIEWS_TIMESTAMP
                    + " = " + view.getTimestamp();

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
        mContentResolver.delete(TrendingContract.TrendingEntry.CONTENT_URI,
                null,
                null);

        for (ResponseTrending repository : responseTrendingList) {
            ContentValues contentValues = TrendingContract.TrendingEntry
                    .buildContentValues(repository, period, language);
            mContentResolver.insert(
                    TrendingContract.TrendingEntry.CONTENT_URI,
                    contentValues);
        }
    }
}
