package com.dmitrymalkovich.android.githubanalytics.welcome;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.util.Log;

import com.dmitrymalkovich.android.githubanalytics.data.source.GithubRepository;
import com.dmitrymalkovich.android.githubanalytics.data.source.LoaderProvider;
import com.dmitrymalkovich.android.githubanalytics.data.source.remote.oauth.AccessToken;
import com.dmitrymalkovich.android.githubanalytics.data.source.remote.oauth.GithubLoginService;
import com.dmitrymalkovich.android.githubanalytics.data.source.remote.oauth.GithubServiceGenerator;
import com.dmitrymalkovich.android.githubanalytics.data.source.remote.oauth.OAuthConstants;

import java.io.IOException;

import retrofit2.Call;

import static com.google.common.base.Preconditions.checkNotNull;

public class WelcomePresenter implements WelcomeContract.Presenter {

    public static String LOG_TAG = WelcomePresenter.class.getSimpleName();

    @NonNull
    private final LoaderProvider mLoaderProvider;

    @NonNull
    private final LoaderManager mLoaderManager;

    @NonNull
    private GithubRepository mGithubRepository;

    @NonNull
    private WelcomeContract.View mWelcomeView;

    public WelcomePresenter(@NonNull GithubRepository tasksRepository,
                            @NonNull WelcomeContract.View addTaskView,
                            @NonNull LoaderProvider loaderProvider,
                            @NonNull LoaderManager loaderManager) {
        mGithubRepository = checkNotNull(tasksRepository);
        mWelcomeView = checkNotNull(addTaskView);
        mLoaderProvider = checkNotNull(loaderProvider);
        mLoaderManager = checkNotNull(loaderManager, "loaderManager cannot be null!");

        mWelcomeView.setPresenter(this);
    }

    @Override
    public void start() {

    }

    @Override
    public void signIn() {
        mWelcomeView.startBasicAuthorizationActivity();
    }

    @Override
    public void oauthSignIn() {
        Uri uri = Uri.parse(GithubServiceGenerator.API_BASE_URL
                + "?client_id=" + OAuthConstants.clientId
                + "&redirect_uri=" + OAuthConstants.redirectUri);
        mWelcomeView.startOAuthIntent(uri);
    }

    @Override
    public void handleIntent(Intent intent) {
        try {
            Uri uri = intent.getData();
            if (uri != null && uri.toString().startsWith(OAuthConstants.redirectUri)) {
                // Use the parameter your API exposes for the code (mostly it's "code")
                String code = uri.getQueryParameter("code");
                if (code != null) {
                    // Get access token
                    GithubLoginService loginService =
                            GithubServiceGenerator.createService(GithubLoginService.class,
                                    OAuthConstants.clientId, OAuthConstants.clientSecret);
                    Call<AccessToken> call = loginService.getAccessToken(code, "authorization_code");
                    AccessToken accessToken = call.execute().body();
                    Log.d(LOG_TAG, "accessToken=" + accessToken.getAccessToken());
                    // TODO : Save token
                }
                // TODO : Else if (uri.getQueryParameter("error") != null) Show an error message here
            }
        }
        catch (IOException e)
        {
            Log.e(LOG_TAG, e.getMessage(), e);
        }
    }
}
