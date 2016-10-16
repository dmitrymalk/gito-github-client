package com.dmitrymalkovich.android.githubanalytics.data.source.remote.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ResponseRepositorySearch {

    @SerializedName("items")
    List<ResponseTrending> items;

    public List<ResponseTrending> getItems() {
        return items;
    }
}
