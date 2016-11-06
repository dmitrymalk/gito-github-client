package com.dmitrymalkovich.android.githubanalytics.data.source;

import com.dmitrymalkovich.android.githubanalytics.data.source.local.GithubLocalDataSource;
import com.dmitrymalkovich.android.githubanalytics.data.source.remote.gson.ResponseClones;
import com.dmitrymalkovich.android.githubanalytics.data.source.remote.gson.ResponseReferrer;
import com.dmitrymalkovich.android.githubanalytics.data.source.remote.gson.ResponseStargazers;
import com.dmitrymalkovich.android.githubanalytics.data.source.remote.gson.ResponseTrending;
import com.dmitrymalkovich.android.githubanalytics.data.source.remote.gson.ResponseUser;
import com.dmitrymalkovich.android.githubanalytics.data.source.remote.gson.ResponseViews;

import org.eclipse.egit.github.core.Repository;

import java.util.List;

public interface GithubDataSource {

    interface RequestTokenFromCodeCallback {

        void onTokenLoaded(String token, String tokenType);

        void onDataNotAvailable();
    }

    interface GetRepositoriesCallback {

        void onRepositoriesLoaded(List<Repository> repositoryList);

        void onDataNotAvailable();
    }

    interface GetRepositoryReferrersCallback {

        void onRepositoryReferrersLoaded(List<ResponseReferrer> responseReferrerList);

        void onDataNotAvailable();
    }

    interface GetRepositoryClonesCallback {

        void onRepositoryClonesLoaded(ResponseClones responseClones);

        void onDataNotAvailable();
    }

    interface GetRepositoryViewsCallback {

        void onRepositoryViewsLoaded(ResponseViews responseViews);

        void onDataNotAvailable();
    }

    interface GetTrendingRepositories {

        void onTrendingRepositoriesLoaded(List<ResponseTrending> responseTrendingList,
                                          String language, String period);

        void onDataNotAvailable();
    }

    interface GerUserCallback {
        void onUserLoaded(ResponseUser user);

        void onDataNotAvailable();
    }

    interface GetStargazersCallback {
        void onStargazersLoaded(List<ResponseStargazers> responseStargazersList);
        void onDataNotAvailable();
    }

    void login(String login, String password);

    void logout();

    void getRepositories(GetRepositoriesCallback callback);

    void getRepositoryReferrers(Repository repository, GetRepositoryReferrersCallback callback);

    void getRepositoryClones(Repository repository, String period, GetRepositoryClonesCallback callback);

    void getRepositoryViews(Repository repository, String period, GetRepositoryViewsCallback callback);

    void getRepositoriesWithAdditionalInfo(GetRepositoriesCallback callback);

    void requestTokenFromCode(String code, RequestTokenFromCodeCallback callback);

    void getTrendingRepositories(String period, String language, GetTrendingRepositories callback,
                                 boolean useCache);

    void saveToken(String token, String tokenType);

    String getToken();

    String getTokenType();

    void getUser(GerUserCallback callback);

    @GithubLocalDataSource.TrendingLanguage
    String getDefaultLanguageForTrending();

    void setDefaultLanguageForTrending(@GithubLocalDataSource.TrendingLanguage String language);

    @GithubLocalDataSource.TrendingPeriod
    String getDefaultPeriodForTrending();

    void setDefaultPeriodForTrending(@GithubLocalDataSource.TrendingPeriod String period);

    void getStargazers(Repository repository, GetStargazersCallback callback);
}
