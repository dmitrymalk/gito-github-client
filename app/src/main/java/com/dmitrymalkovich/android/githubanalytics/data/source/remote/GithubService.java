package com.dmitrymalkovich.android.githubanalytics.data.source.remote;

import com.dmitrymalkovich.android.githubanalytics.data.source.remote.gson.ResponseAccessToken;
import com.dmitrymalkovich.android.githubanalytics.data.source.remote.gson.ResponseClones;
import com.dmitrymalkovich.android.githubanalytics.data.source.remote.gson.ResponseReferrer;
import com.dmitrymalkovich.android.githubanalytics.data.source.remote.gson.ResponseViews;

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
 *
 * OAuth GitHub API: https://developer.github.com/v3/oauth/
 */
public interface GithubService {

    String clientId = "e0bcea9f880637cd0e7a";
    String clientSecret = "3ab8f9e3db083ca80211a48ef13e870b8e6fb78b";
    String redirectUri = "githubanalytics://auth";

    @FormUrlEncoded
    @POST("/login/oauth//access_token")
    Call<ResponseAccessToken> getAccessToken(
            @Field("code") String code,
            @Field("client_id") String clientId,
            @Field("client_secret") String clientSecret);

    /**
     * Get the top 10 referrers over the last 14 days.
     *
     * https://developer.github.com/v3/repos/traffic/
     */
    @GET("/repos/{owner}/{repo}/traffic/popular/referrers")
    Call<List<ResponseReferrer>> getTopReferrers(
            @Path("owner") String owner,
            @Path("repo") String repo
    );

    /**
     * Get the total number of views and breakdown per day or week for the last 14 days.
     *
     * https://developer.github.com/v3/repos/traffic/
     */
    @GET("/repos/{owner}/{repo}/traffic/views")
    Call<ResponseViews> getRepositoryViews(
            @Path("owner") String owner,
            @Path("repo") String repo,
            @Query("per") String per
    );

    /**
     * Get the total number of clones and breakdown per day or week for the last 14 days.
     */
    @GET("/repos/{owner}/{repo}/traffic/clones")
    Call<ResponseClones> getRepositoryClones(
            @Path("owner") String owner,
            @Path("repo") String repo,
            @Query("per") String per
    );
}