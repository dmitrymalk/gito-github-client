package com.dmitrymalkovich.android.githubanalytics.traffic;

import android.os.Bundle;

import android.support.v4.widget.SwipeRefreshLayout;

import com.dmitrymalkovich.android.githubanalytics.BasePresenter;
import com.dmitrymalkovich.android.githubanalytics.BaseView;

class TrafficContract {

    interface View extends BaseView<Presenter> {
        void setLoadingIndicator(boolean active);
    }

    interface Presenter extends BasePresenter, SwipeRefreshLayout.OnRefreshListener {

        @Override
        void start(Bundle savedInstanceState, long repositoryId);

        @Override
        void start(Bundle savedInstanceState);
    }
}
