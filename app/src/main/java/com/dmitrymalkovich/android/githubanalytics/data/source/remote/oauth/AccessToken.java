package com.dmitrymalkovich.android.githubanalytics.data.source.remote.oauth;

import com.google.gson.annotations.SerializedName;

/**
 * Credit to https://futurestud.io/tutorials/oauth-2-on-android-with-retrofit
 *
 * OAuth GitHub API: https://developer.github.com/v3/oauth/
 */
public class AccessToken {

    @SerializedName("access_token")
    private String accessToken;
    @SerializedName("token_type")
    private String tokenType;

    public String getAccessToken() {
        return accessToken;
    }

    public String getTokenType() {
        return tokenType;
    }
}