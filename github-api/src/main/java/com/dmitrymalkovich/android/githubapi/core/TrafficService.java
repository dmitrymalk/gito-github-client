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

import com.dmitrymalkovich.android.githubapi.core.data.Clones;
import com.dmitrymalkovich.android.githubapi.core.data.ReferringSite;
import com.dmitrymalkovich.android.githubapi.core.data.Views;

import org.eclipse.egit.github.core.Repository;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class TrafficService extends Service {
    private String mLogin;
    private String mRepositoryName;
    private String mPeriod;

    public TrafficService setToken(String token) {
        return (TrafficService) super.setToken(token);
    }

    public TrafficService setTokenType(String tokenType) {
        return (TrafficService) super.setTokenType(tokenType);
    }

    public TrafficService setRepository(Repository repository) {
        mRepositoryName = repository.getName();
        mLogin = repository.getOwner().getLogin();
        return this;
    }

    public TrafficService setPeriod(String period) {
        mPeriod = period;
        return this;
    }

    public Clones getClones() throws IOException {
        Call<Clones> call = createGithubService().getRepositoryClones(
                mLogin, mRepositoryName, mPeriod);

        Response<Clones> response = call.execute();
        if (response.isSuccessful()) {
            Clones clones = response.body();
            if (clones != null && clones.asList() != null) {
                return clones;
            } else {
                throw new IOException();
            }
        } else {
            APIError error = APIError.parseError(response);
            throw new IOException(error.getMessage());
        }
    }

    public List<ReferringSite> getReferringSites() throws IOException {
        Call<List<ReferringSite>> call = createGithubService().getTopReferrers(
                mLogin, mRepositoryName);
        Response<List<ReferringSite>> response = call.execute();
        if (response.isSuccessful()) {
            List<ReferringSite> referringSites = response.body();
            if (referringSites != null) {
                return referringSites;
            } else {
                throw new IOException();
            }
        } else {
            APIError error = APIError.parseError(response);
            throw new IOException(error.getMessage());
        }
    }

    public Views getViews() throws IOException {
        Call<Views> call = createGithubService().getRepositoryViews(
                mLogin, mRepositoryName, mPeriod);
        Response<Views> response = call.execute();
        if (response.isSuccessful()) {
            Views views = response.body();
            if (views != null && views.getViews() != null) {
                return views;
            } else {
                throw new IOException();
            }
        } else {
            APIError error = APIError.parseError(response);
            throw new IOException(error.getMessage());
        }
    }

    private GithubService createGithubService() {
        String mUrl = API_HTTPS_BASE_URL;
        String mHeader = "application/vnd.github.spiderman-preview+json";
        return createService(
                GithubService.class, getAccessToken(), mUrl, mHeader);
    }
}
