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
package com.dmitrymalkovich.android.githubanalytics.welcome;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

import com.dmitrymalkovich.android.githubanalytics.data.source.GithubDataSource;
import com.dmitrymalkovich.android.githubanalytics.data.source.GithubRepository;
import com.dmitrymalkovich.android.githubapi.core.GithubService;

import static com.dmitrymalkovich.android.githubapi.core.Service.API_URL_AUTH;

class WelcomePresenter implements WelcomeContract.Presenter {

    @SuppressWarnings("unused")
    private static String LOG_TAG = WelcomePresenter.class.getSimpleName();
    private GithubRepository mGithubRepository;
    private WelcomeContract.View mWelcomeView;

    WelcomePresenter(@NonNull GithubRepository githubRepository,
                     @NonNull WelcomeContract.View view) {
        mGithubRepository = githubRepository;
        mWelcomeView = view;
        mWelcomeView.setPresenter(this);
    }

    @Override
    public void start(Bundle savedInstanceState) {
        if (mGithubRepository.getToken() != null) {
            mWelcomeView.startDashboard();
        }
    }

    @Override
    public void oauthSignIn() {
        Uri uri = Uri.parse(API_URL_AUTH
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
                Log.d(LOG_TAG, "Code=" + code);
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
