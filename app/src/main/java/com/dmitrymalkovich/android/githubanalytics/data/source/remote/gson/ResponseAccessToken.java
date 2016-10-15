package com.dmitrymalkovich.android.githubanalytics.data.source.remote.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Credit to https://futurestud.io/tutorials/oauth-2-on-android-with-retrofit
 * <p>
 * OAuth GitHub API: https://developer.github.com/v3/oauth/
 */
@SuppressWarnings("unused")
public class ResponseAccessToken {

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

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }
}