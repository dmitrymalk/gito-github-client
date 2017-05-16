/*
 * Copyright 2017.  Dmitry Malkovich
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.dmitrymalkovich.android.githubapi.core.data;

import com.google.gson.annotations.SerializedName;

import java.util.List;

@SuppressWarnings("all")
public class Views {
    @SerializedName("count")
    private String mCount;

    @SerializedName("uniques")
    private String mUniques;

    @SerializedName("views")
    private List<View> mViews;

    public List<View> getViews() {
        return mViews;
    }

    public static class View {
        @SerializedName("timestamp")
        private String mTimestamp;

        @SerializedName("count")
        private String mCount;

        @SerializedName("uniques")
        private String mUniques;

        public String getTimestamp() {
            return mTimestamp;
        }

        public String getCount() {
            return mCount;
        }

        public String getUniques() {
            return mUniques;
        }
    }
}
