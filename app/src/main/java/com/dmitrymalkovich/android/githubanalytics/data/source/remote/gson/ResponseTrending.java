package com.dmitrymalkovich.android.githubanalytics.data.source.remote.gson;

import com.google.gson.annotations.SerializedName;

@SuppressWarnings("all")
public class ResponseTrending {

    @SerializedName("title")
    String name;

    @SerializedName("description")
    String description;

    @SerializedName("language")
    String language;

    @SerializedName("watchers_count")
    int watchersCount;

    @SerializedName("readme")
    String htmlUrl;

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public void setLanguage(String language) {
        this.language = language;
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
