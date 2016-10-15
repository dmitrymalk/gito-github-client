package com.dmitrymalkovich.android.githubanalytics.data.source.remote.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ResponseViews {
    @SerializedName("count")
    private String count;

    @SerializedName("uniques")
    private String uniques;

    @SerializedName("views")
    private List<View> views;

    public List<View> getViews() {
        return views;
    }

    public static class View {
        @SerializedName("timestamp")
        private String timestamp;

        @SerializedName("count")
        private String count;

        @SerializedName("uniques")
        private String uniques;

        public String getTimestamp() {
            return timestamp;
        }

        public String getCount() {
            return count;
        }

        public String getUniques() {
            return uniques;
        }
    }
}
