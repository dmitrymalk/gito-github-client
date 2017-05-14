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
package com.dmitrymalkovich.android.githubapi.core.pagination;

import com.dmitrymalkovich.android.githubapi.core.gson.Star;

import java.util.List;

import retrofit2.Response;

public class Pagination {

    public static final String LAST_PAGE = "last";
    private static final String HEADER_LINK = "Link";
    private int mLastPage;

    public void parse(Response<List<Star>> response) {
        String headerLink = response.headers().get(HEADER_LINK);
        if (headerLink != null) {
            headerLink = headerLink.replace(
                    headerLink.substring(headerLink.lastIndexOf(">")), "");
            mLastPage = Integer.valueOf(headerLink.replace(
                    headerLink.substring(0, headerLink.lastIndexOf("=") + 1), ""));
        }
    }

    public int getLastPage() {
        return mLastPage;
    }
}
