package com.dmitrymalkovich.android.githubanalytics.data.source.remote.gson;

import com.google.gson.annotations.SerializedName;

@SuppressWarnings("all")
public class ResponseReferrer {

    @SerializedName("referrer")
    private String referrer;

    @SerializedName("count")
    private int count;

    @SerializedName("uniques")
    private int uniques;

    public String getReferrer() {
        return referrer;
    }

    public int getCount() {
        return count;
    }

    public int getUniques() {
        return uniques;
    }
}
