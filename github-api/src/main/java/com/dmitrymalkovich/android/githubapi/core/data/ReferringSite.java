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

@SuppressWarnings("all")
public class ReferringSite {

    @SerializedName("referrer")
    private String mReferrer;

    @SerializedName("count")
    private int mCount;

    @SerializedName("uniques")
    private int mUniques;

    public String getReferrer() {
        return mReferrer;
    }

    public int getCount() {
        return mCount;
    }

    public int getUniques() {
        return mUniques;
    }
}
