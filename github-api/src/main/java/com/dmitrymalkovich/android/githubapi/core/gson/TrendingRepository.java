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
package com.dmitrymalkovich.android.githubapi.core.gson;

import com.google.gson.annotations.SerializedName;

@SuppressWarnings("all")
public class TrendingRepository {

    @SerializedName("avatar")
    String mAvatar;

    @SerializedName("repo")
    String mRepo;

    @SerializedName("desc")
    String description;

    @SerializedName("owner")
    String mOwner;

    @SerializedName("stars")
    String mStars;

    @SerializedName("link")
    String mHtmlUrl;

    String mLanguage;

    String mPeriod;

    public String getName() {
        return mRepo;
    }

    public String getDescription() {
        return description;
    }

    public String getOwner() {
        return mOwner;
    }

    public String getWatchersCount() {
        return mStars;
    }

    public String getHtmlUrl() {
        return mHtmlUrl;
    }

    public String getAvatar() {
        return mAvatar;
    }

    public String getLanguage() {
        return mLanguage;
    }

    public void setLanguage(String language) {
        mLanguage = language;
    }

    public String getPeriod() {
        return mPeriod;
    }

    public void setPeriod(String period) {
        mPeriod = period;
    }
}
