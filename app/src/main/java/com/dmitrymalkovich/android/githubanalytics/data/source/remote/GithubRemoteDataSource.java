package com.dmitrymalkovich.android.githubanalytics.data.source.remote;

import android.os.AsyncTask;
import android.util.Log;

import com.dmitrymalkovich.android.githubanalytics.data.source.GithubDataSource;
import com.dmitrymalkovich.android.githubanalytics.data.source.remote.oauth.ResponseAccessToken;
import com.dmitrymalkovich.android.githubanalytics.data.source.remote.oauth.GithubService;
import com.dmitrymalkovich.android.githubanalytics.data.source.remote.oauth.GithubServiceGenerator;

import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.service.RepositoryService;

import java.io.IOException;

import retrofit2.Call;

public class GithubRemoteDataSource implements GithubDataSource {
    private static String LOG_TAG = GithubRemoteDataSource.class.getSimpleName();

    private static GithubRemoteDataSource INSTANCE;

    private GithubRemoteDataSource() {
    }

    public static GithubRemoteDataSource getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new GithubRemoteDataSource();
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
    public void getRepositories() {
        try {
            RepositoryService service = new RepositoryService();
            for (Repository repo : service.getRepositories())
                System.out.println(repo.getName() + " Watchers: " + repo.getWatchers());

        } catch (IOException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
        }
    }

    @Override
    public void requestTokenFromCode(final String code, final RequestTokenFromCodeCallback callback) {
        new AsyncTask<Void, Void, String>()
        {
            @Override
            protected String doInBackground(Void... params) {
                try {
                    GithubService loginService = GithubServiceGenerator.createService(
                            GithubService.class);
                    Call<ResponseAccessToken> call = loginService.getAccessToken(code,
                            GithubService.clientId, GithubService.clientSecret);
                    ResponseAccessToken accessToken = call.execute().body();
                    return accessToken.getAccessToken();
                }
                catch (IOException e)
                {
                    Log.e(LOG_TAG, e.getMessage(), e);
                }
                return null;
            }

            @Override
            protected void onPostExecute(String token) {
                if (token != null && !token.isEmpty()) {
                    callback.onTokenLoaded(token);
                }
                else {
                    callback.onDataNotAvailable();
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void saveToken(String token) {
    }

    @Override
    public String getToken() {
        return null;
    }
}
