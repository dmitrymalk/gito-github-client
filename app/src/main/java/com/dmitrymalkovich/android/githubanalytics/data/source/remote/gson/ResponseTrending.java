package com.dmitrymalkovich.android.githubanalytics.data.source.remote.gson;

import com.google.gson.annotations.SerializedName;

public class ResponseTrending {

    @SerializedName("name")
    String name;

    @SerializedName("description")
    String description;

    @SerializedName("language")
    String language;

    @SerializedName("watchers_count")
    int watchersCount;

    @SerializedName("html_url")
    String htmlUrl;

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getLanguage() {
        return language;
    }

    public int getWatchersCount() {
        return watchersCount;
    }

    public String getHtmlUrl() {
        return htmlUrl;
    }
}
