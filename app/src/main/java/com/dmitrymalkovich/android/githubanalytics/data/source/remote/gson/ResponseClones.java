package com.dmitrymalkovich.android.githubanalytics.data.source.remote.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ResponseClones {
    @SerializedName("count")
    private String count;

    @SerializedName("uniques")
    private String uniques;

    @SerializedName("clones")
    private List<Clone> clones;

    public List<Clone> getClones() {
        return clones;
    }

    public static class Clone {
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
