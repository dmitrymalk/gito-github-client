package com.dmitrymalkovich.android.githubanalytics.data.source.remote.gson;

import com.google.gson.annotations.SerializedName;

@SuppressWarnings("all")
public class ResponseTrending {

    @SerializedName("avatar")
    String avatar;

    @SerializedName("repo")
    String repo;

    @SerializedName("desc")
    String description;

    @SerializedName("owner")
    String owner;

    @SerializedName("stars")
    String stars;

    @SerializedName("link")
    String htmlUrl;

    public String getName() {
        return repo;
    }

    public String getDescription() {
        return description;
    }

    public String getOwner() {
        return owner;
    }

    public String getWatchersCount() {
        return stars;
    }

    public String getHtmlUrl() {
        return htmlUrl;
    }

    public String getAvatar() {
        return avatar;
    }
}
