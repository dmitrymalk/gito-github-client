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
        void start(Bundle savedInstanceState, long repositoryId);

        void onTabSelected(int position);

        String getTitle(Context context);

        void changeLanguage(String language);


        boolean isLanguageSelected(String language);
    }
}
