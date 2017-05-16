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
package com.dmitrymalkovich.android.githubapi.core;

import android.support.annotation.WorkerThread;

import com.dmitrymalkovich.android.githubapi.core.data.AccessToken;

import java.io.IOException;

import retrofit2.Call;

public class AuthenticationBuilder extends Service{

    private String mCode;

    public AuthenticationBuilder() {
    }

    public AuthenticationBuilder setCode(String code) {
        mCode = code;
        return this;
    }

    @WorkerThread
    public AccessToken requestAccessToken() throws IOException {
        GithubService loginService = createService(
                GithubService.class);
        Call<AccessToken> call = loginService.getAccessToken(mCode,
                GithubService.clientId, GithubService.clientSecret);
        return call.execute().body();
    }
}

