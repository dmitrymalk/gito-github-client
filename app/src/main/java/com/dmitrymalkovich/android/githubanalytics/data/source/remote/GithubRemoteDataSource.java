package com.dmitrymalkovich.android.githubanalytics.data.source.remote;

import android.util.Log;

import com.dmitrymalkovich.android.githubanalytics.data.source.GithubDataSource;
import com.dmitrymalkovich.android.githubanalytics.data.source.remote.oauth.AccessToken;
import com.dmitrymalkovich.android.githubanalytics.data.source.remote.oauth.GithubLoginService;
import com.dmitrymalkovich.android.githubanalytics.data.source.remote.oauth.GithubServiceGenerator;
import com.dmitrymalkovich.android.githubanalytics.data.source.remote.oauth.OAuthConstants;

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
    public void requestTokenFromCode(String code) throws IOException {
        GithubLoginService loginService = GithubServiceGenerator.createService(GithubLoginService.class);
        Call<AccessToken> call = loginService.getAccessToken(code,
                OAuthConstants.clientId, OAuthConstants.clientSecret);
        AccessToken accessToken = call.execute().body();
        Log.d(LOG_TAG, "accessToken=" + accessToken.getAccessToken());
    }
}