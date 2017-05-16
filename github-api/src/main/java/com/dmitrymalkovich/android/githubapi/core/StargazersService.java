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

import com.dmitrymalkovich.android.githubapi.core.time.TimeConverter;
import com.dmitrymalkovich.android.githubapi.core.data.Star;

import org.eclipse.egit.github.core.Repository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class StargazersService extends Service {
    private String mLogin;
    private String mRepositoryName;
    private String mPage;
    private long mDate;

    public StargazersService() {
    }

    public StargazersService setToken(String token) {
        return (StargazersService) super.setToken(token);
    }

    public StargazersService setTokenType(String tokenType) {
        return (StargazersService) super.setTokenType(tokenType);
    }

    public StargazersService setRepository(Repository repository) {
        mRepositoryName = repository.getName();
        mLogin = repository.getOwner().getLogin();
        return this;
    }

    public StargazersService setPage(String page) {
        mPage = page;
        return this;
    }

    public StargazersService setDate(long date) {
        mDate = date;
        return this;
    }

    @WorkerThread
    public List<Star> getStars() throws IOException {
        Call<List<Star>> dummyCall = createGithubService().getStargazers(
                mLogin, mRepositoryName, mPage);
        Response<List<Star>> paginationResponse = dummyCall.execute();
        if (paginationResponse.isSuccessful()) {

            Pagination pagination = new Pagination();
            pagination.parse(paginationResponse);
            int page = pagination.getLastPage();

            List<Star> stars = new ArrayList<>();
            for (int i = page; page > 0; i--) {
                Call<List<Star>> call = createGithubService().getStargazers(
                        mLogin, mRepositoryName, String.valueOf(i));
                Response<List<Star>> response = call.execute();
                if (response.isSuccessful()) {
                    List<Star> starsForPage = response.body();
                    if (starsForPage != null) {
                        stars.addAll(starsForPage);

                        if (starsForPage.size() > 0
                                && TimeConverter.iso8601ToMilliseconds(
                                starsForPage.get(0).getStarredAt()) < mDate) {
                            break;
                        }

                    } else {
                        throw new IOException();
                    }
                } else {
                    APIError error = APIError.parseError(paginationResponse);
                    throw new IOException(error.getMessage());
                }
            }
            return stars;
        } else {
            APIError error = APIError.parseError(paginationResponse);
            throw new IOException(error.getMessage());
        }
    }

    private GithubService createGithubService() {
        String mUrl = API_HTTPS_BASE_URL;
        String mHeader = "application/vnd.github.v3.star+json";
        return createService(GithubService.class, getAccessToken(), mUrl, mHeader);
    }
}
