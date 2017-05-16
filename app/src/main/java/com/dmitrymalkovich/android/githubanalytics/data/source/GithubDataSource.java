/*
 * Copyright 2017.  Dmitry Malkovich
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.dmitrymalkovich.android.githubanalytics.data.source;

import com.dmitrymalkovich.android.githubanalytics.data.source.local.GithubLocalDataSource;
import com.dmitrymalkovich.android.githubapi.core.data.Clones;
import com.dmitrymalkovich.android.githubapi.core.data.ReferringSite;
import com.dmitrymalkovich.android.githubapi.core.data.Star;
import com.dmitrymalkovich.android.githubapi.core.data.TrendingRepository;
import com.dmitrymalkovich.android.githubapi.core.data.User;
import com.dmitrymalkovich.android.githubapi.core.data.Views;

import org.eclipse.egit.github.core.Repository;

import java.util.List;

public interface GithubDataSource {

    void setPinned(boolean active, long id);

    void logout();

    void getRepositories(GetRepositoriesCallback callback);

    void getRepositoryReferrers(Repository repository, GetRepositoryReferrersCallback callback);

    void getRepositoryClones(Repository repository, String period, GetRepositoryClonesCallback callback);

    void getRepositoryViews(Repository repository, String period, GetRepositoryViewsCallback callback);

    void getRepositoriesWithAdditionalInfo(GetRepositoriesCallback callback, boolean useCache);

    void getRepositoriesWithAdditionalInfo(long repositoryId, GetRepositoriesCallback callback, boolean useCache);

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

    interface RequestTokenFromCodeCallback {

        void onTokenLoaded(String token, String tokenType);

        void onDataNotAvailable();
    }

    interface GetRepositoriesCallback {

        void onRepositoriesLoaded(List<Repository> repositoryList);

        void onDataNotAvailable();
    }

    interface GetRepositoryReferrersCallback {

        void onRepositoryReferrersLoaded(List<ReferringSite> referringSiteList);

        void onDataNotAvailable();
    }

    interface GetRepositoryClonesCallback {

        void onRepositoryClonesLoaded(Clones clones);

        void onDataNotAvailable();
    }

    interface GetRepositoryViewsCallback {

        void onRepositoryViewsLoaded(Views views);

        void onDataNotAvailable();
    }

    interface GetTrendingRepositories {

        void onTrendingRepositoriesLoaded(List<TrendingRepository> trendingRepositoryList,
                                          String language, String period);

        void onDataNotAvailable();
    }

    interface GerUserCallback {
        void onUserLoaded(User user);

        void onDataNotAvailable();
    }

    interface GetStargazersCallback {
        void onStargazersLoaded(List<Star> starList);

        void onDataNotAvailable();
    }
}
