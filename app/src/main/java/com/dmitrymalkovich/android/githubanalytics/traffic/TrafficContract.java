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
package com.dmitrymalkovich.android.githubanalytics.traffic;

import android.database.Cursor;
import android.os.Bundle;

import com.dmitrymalkovich.android.githubanalytics.BasePresenter;
import com.dmitrymalkovich.android.githubanalytics.BaseView;

class TrafficContract {

    interface View extends BaseView<Presenter> {
        void setLoadingIndicator(boolean active);

        void showRepository(Cursor data);

        void setEmptyState(boolean active);

        void showReferrers(Cursor data);

        void showClones(Cursor data);

        void showViews(Cursor data);

        void openUrl(String url);
    }

    interface Presenter extends BasePresenter {

        void start(Bundle savedInstanceState, long repositoryId);

        @Override
        void start(Bundle savedInstanceState);
    }
}
