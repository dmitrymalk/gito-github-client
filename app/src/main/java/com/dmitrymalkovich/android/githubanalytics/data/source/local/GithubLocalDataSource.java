package com.dmitrymalkovich.android.githubanalytics.data.source.local;

import android.content.ContentResolver;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import com.dmitrymalkovich.android.githubanalytics.data.source.GithubDataSource;

import org.eclipse.egit.github.core.Repository;

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
}
