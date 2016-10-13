package com.dmitrymalkovich.android.githubanalytics.data.source.remote;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

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
}