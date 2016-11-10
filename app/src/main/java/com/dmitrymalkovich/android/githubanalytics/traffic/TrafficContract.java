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
