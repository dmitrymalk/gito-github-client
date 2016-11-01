package com.dmitrymalkovich.android.githubanalytics.dashboard;


import android.database.Cursor;
import android.support.v4.widget.SwipeRefreshLayout;

import com.dmitrymalkovich.android.githubanalytics.BasePresenter;
import com.dmitrymalkovich.android.githubanalytics.BaseView;

class DashboardContract {

    interface View extends BaseView<Presenter> {
        void setLoadingIndicator(boolean active);

        void setRefreshIndicator(boolean active);

        void showRepositories(Cursor data);

        void signOut();
    }

    interface Presenter extends BasePresenter, SwipeRefreshLayout.OnRefreshListener {
        @Override
        void onRefresh();

        @Override
        void start();
    }
}
