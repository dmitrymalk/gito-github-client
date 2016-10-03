package com.dmitrymalkovich.android.githubanalytics.data.source;

public interface GithubDataSource {
    void login(String login, String password);
    void getRepositories();
}
