package com.dmitrymalkovich.android.githubanalytics.data.source.remote.gson;

import com.google.gson.annotations.SerializedName;

public class ResponseStargazers {

    @SerializedName("starred_at")
    String starredAt;

    @SerializedName("user")
    User user;

    public String getStarredAt() {
        return starredAt;
    }

    public class User {
        @SerializedName("id")
        long id;

        @SerializedName("login")
        String login;

        @SerializedName("url")
        String url;

        public long getId() {
            return id;
        }

        public String getLogin() {
            return login;
        }

        public String getUrl() {
            return url;
        }
    }
}
