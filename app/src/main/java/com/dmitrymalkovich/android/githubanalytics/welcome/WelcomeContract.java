package com.dmitrymalkovich.android.githubanalytics.welcome;

import android.content.Intent;
import android.net.Uri;

import com.dmitrymalkovich.android.githubanalytics.BasePresenter;
import com.dmitrymalkovich.android.githubanalytics.BaseView;

class WelcomeContract {

    interface View extends BaseView<Presenter> {

        void startOAuthIntent(Uri uri);

        void startDashboard();

        void setLoadingIndicator(boolean active);

        void authorizationFailed();
    }

    interface Presenter extends BasePresenter {

        void oauthSignIn();

        void handleIntent(Intent intent);
    }
}
