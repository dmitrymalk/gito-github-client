package com.dmitrymalkovich.android.githubanalytics.welcome;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.dmitrymalkovich.android.githubanalytics.data.source.GithubDataSource;
import com.dmitrymalkovich.android.githubanalytics.data.source.GithubRepository;
import com.dmitrymalkovich.android.githubanalytics.data.source.remote.GithubService;
import com.dmitrymalkovich.android.githubanalytics.data.source.remote.GithubServiceGenerator;

import static com.google.common.base.Preconditions.checkNotNull;

class WelcomePresenter implements WelcomeContract.Presenter {

    @SuppressWarnings("unused")
    private static String LOG_TAG = WelcomePresenter.class.getSimpleName();
    private GithubRepository mGithubRepository;
    private WelcomeContract.View mWelcomeView;

    WelcomePresenter(@NonNull GithubRepository githubRepository,
                     @NonNull WelcomeContract.View view) {
        mGithubRepository = checkNotNull(githubRepository);
        mWelcomeView = checkNotNull(view);
        mWelcomeView.setPresenter(this);
    }

    @Override
    public void start() {
        if (mGithubRepository.getToken() != null) {
            mWelcomeView.startDashboard();
        }
    }

    @Override
    public void oauthSignIn() {
        Uri uri = Uri.parse(GithubServiceGenerator.API_URL_AUTH
                + "?client_id=" + GithubService.clientId
                + "&redirect_uri=" + GithubService.redirectUri
                // https://developer.github.com/v3/oauth/#scopes
                + "&scope=public_repo"
        );
        mWelcomeView.startOAuthIntent(uri);
    }

    @Override
    public void handleIntent(Intent intent) {
        Uri uri = intent.getData();
        if (uri != null && uri.toString().startsWith(GithubService.redirectUri)) {
            String code = uri.getQueryParameter("code");
            if (code != null) {
                mWelcomeView.setLoadingIndicator(true);

                mGithubRepository.requestTokenFromCode(code, new GithubDataSource.RequestTokenFromCodeCallback() {
                    @Override
                    public void onTokenLoaded(String token, String tokenType) {
                        mWelcomeView.startDashboard();
                    }

                    @Override
                    public void onDataNotAvailable() {
                        mWelcomeView.setLoadingIndicator(false);
                        mWelcomeView.authorizationFailed();
                    }
                });

            } else {
                mWelcomeView.authorizationFailed();
            }
        } else {
            mWelcomeView.authorizationFailed();
        }

    }
}
