package com.dmitrymalkovich.android.githubanalytics.data.source.remote;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;

import com.dmitrymalkovich.android.githubanalytics.data.source.GithubDataSource;
import com.dmitrymalkovich.android.githubanalytics.data.source.local.GithubLocalDataSource;
import com.dmitrymalkovich.android.githubanalytics.data.source.local.RepositoryContract;

import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.service.RepositoryService;

import java.io.File;
import java.io.IOException;
import java.util.List;

import retrofit2.Call;

public class GithubRemoteDataSource implements GithubDataSource {
    private static String LOG_TAG = GithubRemoteDataSource.class.getSimpleName();

    private static GithubRemoteDataSource INSTANCE;
    @SuppressWarnings("unused")
    private ContentResolver mContentResolver;
    private SharedPreferences mPreferences;

    private GithubRemoteDataSource(ContentResolver contentResolver, SharedPreferences preferences) {
        mContentResolver = contentResolver;
        mPreferences = preferences;
    }

    public static GithubRemoteDataSource getInstance(ContentResolver contentResolver, SharedPreferences preferences) {
        if (INSTANCE == null) {
            INSTANCE = new GithubRemoteDataSource(contentResolver, preferences);
        }
        return INSTANCE;
    }

    @Override
    public void login(final String username, final String password) {
        try {
            RepositoryService service = new RepositoryService();
            service.getClient().setCredentials(username, password);
            service.getRepositories();

        } catch (IOException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
        }
    }

    @Override
    public void getRepositories(final GetRepositoriesCallback callback) {
        new AsyncTask<Void, Void, List<Repository>>()
        {
            @Override
            protected List<Repository> doInBackground(Void... params) {
                try {
                    String token = getToken();
                    if (token != null)
                    {
                        RepositoryService service = new RepositoryService();
                        service.getClient().setOAuth2Token(token);
                        List<Repository> repositoryList = service.getRepositories();
                        saveRepositories(repositoryList);
                        return repositoryList;
                    }
                    else
                    {
                        return null;
                    }
                }
                catch (IOException e)
                {
                    Log.e(LOG_TAG, e.getMessage(), e);
                    return null;
                }
            }

            @Override
            protected void onPostExecute(List<Repository> repositoryList) {
                if (repositoryList != null) {
                    callback.onRepositoriesLoaded(repositoryList);
                }
                else {
                    callback.onDataNotAvailable();
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void getRepositoryReferrers(final Repository repository,
                                       final GetRepositoryReferrersCallback callback) {
        new AsyncTask<Void, Void, List<ResponseReferrer>>()
        {
            @Override
            protected List<ResponseReferrer> doInBackground(Void... params) {
                try {
                    ResponseAccessToken accessToken = new ResponseAccessToken();
                    accessToken.setAccessToken(getToken());
                    accessToken.setTokenType(getTokenType());

                    GithubService loginService = GithubServiceGenerator.createService(
                            GithubService.class, accessToken);
                    Call<List<ResponseReferrer>> call = loginService.getTopReferrers(
                            repository.getOwner().getLogin(), repository.getName());
                    return call.execute().body();
                }
                catch (IOException e)
                {
                    Log.e(LOG_TAG, e.getMessage(), e);
                    return null;
                }
            }

            @Override
            protected void onPostExecute(List<ResponseReferrer> responseReferrerList) {
                if (responseReferrerList != null) {
                    callback.onRepositoryReferrersLoaded(responseReferrerList);
                }
                else {
                    callback.onDataNotAvailable();
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void requestTokenFromCode(final String code, final RequestTokenFromCodeCallback callback) {
        new AsyncTask<Void, Void, ResponseAccessToken>()
        {
            @Override
            protected ResponseAccessToken doInBackground(Void... params) {
                try {
                    GithubService loginService = GithubServiceGenerator.createService(
                            GithubService.class);
                    Call<ResponseAccessToken> call = loginService.getAccessToken(code,
                            GithubService.clientId, GithubService.clientSecret);
                    return call.execute().body();
                }
                catch (IOException e)
                {
                    Log.e(LOG_TAG, e.getMessage(), e);
                }
                return null;
            }

            @Override
            protected void onPostExecute(ResponseAccessToken accessToken) {
                if (accessToken != null && accessToken.getAccessToken() != null
                        && !accessToken.getAccessToken().isEmpty()) {
                    callback.onTokenLoaded(accessToken.getAccessToken(), accessToken.getTokenType());
                }
                else {
                    callback.onDataNotAvailable();
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void saveToken(String token, String tokenType) {
        GithubLocalDataSource.getInstance(mContentResolver, mPreferences)
                .saveToken(token, tokenType);
    }

    @Override
    public String getToken() {
        return GithubLocalDataSource.getInstance(mContentResolver, mPreferences).getToken();
    }

    @Override
    public String getTokenType() {
        return GithubLocalDataSource.getInstance(mContentResolver, mPreferences).getTokenType();
    }

    private boolean saveRepositories(List<Repository> repositoryList) throws IOException {
        for (Repository repo : repositoryList) {

            ContentValues repositoryValues = new ContentValues();
            repositoryValues.put(RepositoryContract.RepositoryEntry.COLUMN_REPOSITORY_ID,
                    repo.getId());
            repositoryValues.put(RepositoryContract.RepositoryEntry.COLUMN_REPOSITORY_NAME,
                    repo.getName());
            repositoryValues.put(RepositoryContract.RepositoryEntry.COLUMN_REPOSITORY_FULL_NAME,
                    repo.getOwner().getName() + File.separator + repo.getName());
            repositoryValues.put(RepositoryContract.RepositoryEntry.COLUMN_REPOSITORY_DESCRIPTION,
                    repo.getDescription());
            repositoryValues.put(RepositoryContract.RepositoryEntry.COLUMN_REPOSITORY_PRIVATE,
                    repo.isPrivate());
            repositoryValues.put(RepositoryContract.RepositoryEntry.COLUMN_REPOSITORY_FORK,
                    repo.isFork());
            repositoryValues.put(RepositoryContract.RepositoryEntry.COLUMN_REPOSITORY_URL,
                    repo.getUrl());
            repositoryValues.put(RepositoryContract.RepositoryEntry.COLUMN_REPOSITORY_HTML_URL,
                    repo.getHtmlUrl());
            repositoryValues.put(RepositoryContract.RepositoryEntry.COLUMN_REPOSITORY_FORKS,
                    repo.getForks());
            repositoryValues.put(RepositoryContract.RepositoryEntry.COLUMN_REPOSITORY_WATCHERS,
                    repo.getWatchers());
            repositoryValues.put(RepositoryContract.RepositoryEntry.COLUMN_REPOSITORY_LANGUAGE,
                    repo.getLanguage());

            Cursor cursor = mContentResolver.query(RepositoryContract.RepositoryEntry.CONTENT_URI,
                    new String[]{RepositoryContract.RepositoryEntry.COLUMN_REPOSITORY_ID},
                    RepositoryContract.RepositoryEntry.COLUMN_REPOSITORY_ID + " = " + repo.getId(),
                    null,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                mContentResolver.update(
                        RepositoryContract.RepositoryEntry.CONTENT_URI,
                        repositoryValues,
                        RepositoryContract.RepositoryEntry.COLUMN_REPOSITORY_ID + " = " + repo.getId(),
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
}
