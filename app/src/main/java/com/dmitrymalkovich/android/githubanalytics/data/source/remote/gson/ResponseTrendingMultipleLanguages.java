package com.dmitrymalkovich.android.githubanalytics.data.source.remote.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

@SuppressWarnings("all")
public class ResponseTrendingMultipleLanguages {

    @SerializedName("java")
    List<ResponseTrending> java;

    public List<ResponseTrending> getJava() {
        return java;
    }
}
