package com.dmitrymalkovich.android.githubanalytics.data.source.remote.oauth;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Credit to https://futurestud.io/tutorials/oauth-2-on-android-with-retrofit
 *
 * OAuth GitHub API: https://developer.github.com/v3/oauth/
 */
public interface GithubLoginService {
    @FormUrlEncoded
    @POST("/login/oauth//access_token")
    Call<AccessToken> getAccessToken(
            @Field("code") String code,
            @Field("client_id") String clientId,
            @Field("client_secret") String clientSecret);
}