package com.dmitrymalkovich.android.githubanalytics.data.source;

import java.io.IOException;

public interface GithubDataSource {
    void login(String login, String password);
    void getRepositories();
    void requestTokenFromCode(String code) throws IOException;
}
