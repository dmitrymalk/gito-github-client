package com.dmitrymalkovich.android.githubanalytics.data.source;


public interface GithubDataSource {

    interface RequestTokenFromCodeCallback {

        void onTokenLoaded(String token);

        void onDataNotAvailable();
    }

    interface GetRepositoriesCallback {

        void onRepositoriesLoaded();

        void onDataNotAvailable();
    }

    void login(String login, String password);

    void getRepositories(GetRepositoriesCallback callback);

    void requestTokenFromCode(String code, RequestTokenFromCodeCallback callback);

    void saveToken(String token);

    String getToken();
}
