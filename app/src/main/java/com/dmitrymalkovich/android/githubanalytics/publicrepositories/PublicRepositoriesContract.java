package com.dmitrymalkovich.android.githubanalytics.publicrepositories;


import android.database.Cursor;
import android.support.v4.widget.SwipeRefreshLayout;

import com.dmitrymalkovich.android.githubanalytics.BasePresenter;
import com.dmitrymalkovich.android.githubanalytics.BaseView;

class PublicRepositoriesContract {

    interface View extends BaseView<Presenter> {
        void setLoadingIndicator(boolean active);

        void showRepositories(Cursor data);
    }

    interface Presenter extends BasePresenter, SwipeRefreshLayout.OnRefreshListener {
        @Override
        void onRefresh();

        @Override
        void start();
    }
}
