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
package com.dmitrymalkovich.android.githubapi.core.service;

import com.dmitrymalkovich.android.githubapi.core.gson.AccessToken;
import com.dmitrymalkovich.android.githubapi.core.gson.Clones;
import com.dmitrymalkovich.android.githubapi.core.gson.ReferringSite;
import com.dmitrymalkovich.android.githubapi.core.gson.Star;
import com.dmitrymalkovich.android.githubapi.core.gson.TrendingRepository;
import com.dmitrymalkovich.android.githubapi.core.gson.Views;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Credit to https://futurestud.io/tutorials/oauth-2-on-android-with-retrofit
 * <p>
 * OAuth GitHub API: https://developer.github.com/v3/oauth/
 */
public interface GithubService {

    String clientId = "";
    String clientSecret = "";
    String redirectUri = "githubanalytics://auth";

    @FormUrlEncoded
    @POST("/login/oauth//access_token")
    Call<AccessToken> getAccessToken(
            @Field("code") String code,
            @Field("client_id") String clientId,
            @Field("client_secret") String clientSecret);

    /**
     * Get the top 10 referrers over the last 14 days.
     * <p>
     * https://developer.github.com/v3/repos/traffic/
     */
    @GET("/repos/{owner}/{repo}/traffic/popular/referrers")
    Call<List<ReferringSite>> getTopReferrers(
            @Path("owner") String owner,
            @Path("repo") String repo
    );

    /**
     * Get the total number of views and breakdown per day or week for the last 14 days.
     * <p>
     * https://developer.github.com/v3/repos/traffic/
     */
    @GET("/repos/{owner}/{repo}/traffic/views")
    Call<Views> getRepositoryViews(
            @Path("owner") String owner,
            @Path("repo") String repo,
            @Query("per") String per
    );

    /**
     * Get the total number of clones and breakdown per day or week for the last 14 days.
     */
    @GET("/repos/{owner}/{repo}/traffic/clones")
    Call<Clones> getRepositoryClones(
            @Path("owner") String owner,
            @Path("repo") String repo,
            @Query("per") String per
    );

    /**
     * Get list of trending github repositories by github api:
     * https://github.com/mingjunli/GithubTrending
     * <p>
     * Period: daily,weekly,monthly
     */
    @GET("api/github/trending/{language}")
    Call<List<TrendingRepository>> getTrendingRepositories(
            @Path("language") String language,
            @Query("since") String period
    );

    /**
     * Get list of stargazers:
     * https://developer.github.com/v3/activity/starring/
     */
    @GET("/repos/{owner}/{repo}/stargazers")
    Call<List<Star>> getStargazers(
            @Path("owner") String owner,
            @Path("repo") String repo,
            @Query("page") String page
    );

    // TODO : Get list forks: https://developer.github.com/v3/repos/forks/
}