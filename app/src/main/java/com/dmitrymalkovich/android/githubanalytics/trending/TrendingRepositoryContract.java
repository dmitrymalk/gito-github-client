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
package com.dmitrymalkovich.android.githubanalytics.trending;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;

import com.dmitrymalkovich.android.githubanalytics.BasePresenter;
import com.dmitrymalkovich.android.githubanalytics.BaseView;

class TrendingRepositoryContract {

    interface View extends BaseView<Presenter> {
        void setLoadingIndicator(boolean active);

        void setRefreshIndicator(boolean active);

        void showRepositories(Cursor data);

        void openUrl(@NonNull String htmlUrl);

        void selectTab(int position);

        void setEmptyState(boolean active);
    }

    interface Presenter extends BasePresenter, SwipeRefreshLayout.OnRefreshListener {
        @Override
        void onRefresh();

        @Override
        void start(Bundle savedInstanceState);

        void onTabSelected(int position);

        String getTitle(Context context);

        void changeLanguage(String language);


        boolean isLanguageSelected(String language);
    }
}
