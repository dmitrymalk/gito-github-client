package com.dmitrymalkovich.android.githubanalytics.basicauthorization;

import com.dmitrymalkovich.android.githubanalytics.BasePresenter;
import com.dmitrymalkovich.android.githubanalytics.BaseView;

public class BasicAuthorizationContract {

    interface View extends BaseView<Presenter> {
    }

    interface Presenter extends BasePresenter {
        void login(String username, String password);
    }
}
