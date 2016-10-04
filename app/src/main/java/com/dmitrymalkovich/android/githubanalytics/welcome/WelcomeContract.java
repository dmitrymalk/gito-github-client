package com.dmitrymalkovich.android.githubanalytics.welcome;

import android.content.Intent;
import android.net.Uri;

import com.dmitrymalkovich.android.githubanalytics.BasePresenter;
import com.dmitrymalkovich.android.githubanalytics.BaseView;

public class WelcomeContract {

    interface View extends BaseView<Presenter> {
        void startBasicAuthorizationActivity();

        void startOAuthIntent(Uri uri);
    }

    interface Presenter extends BasePresenter {

        void signIn();

        void oauthSignIn();

        void handleIntent(Intent intent);
    }
}
