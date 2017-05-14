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
package com.dmitrymalkovich.android.githubapi;

import com.dmitrymalkovich.android.githubapi.core.TimeConverter;
import com.dmitrymalkovich.android.githubapi.core.gson.ReferringSite;
import com.dmitrymalkovich.android.githubapi.core.gson.Star;
import com.dmitrymalkovich.android.githubapi.core.gson.TrendingRepository;
import com.dmitrymalkovich.android.githubapi.core.gson.User;
import com.dmitrymalkovich.android.githubapi.core.gson.Views;
import com.dmitrymalkovich.android.githubapi.core.pagination.Pagination;
import com.dmitrymalkovich.android.githubapi.core.service.GithubService;
import com.dmitrymalkovich.android.githubapi.core.service.GithubServiceGenerator;
import com.dmitrymalkovich.android.githubapi.core.error.APIError;
import com.dmitrymalkovich.android.githubapi.core.gson.AccessToken;
import com.dmitrymalkovich.android.githubapi.core.gson.Clones;
import com.dmitrymalkovich.android.githubapi.core.service.ThirdPartyGithubServiceGenerator;

import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.service.RepositoryService;
import org.eclipse.egit.github.core.service.UserService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Android SDK for GitHub API
 * <p>
 * https://developer.github.com
 * <p>
 * https://developer.github.com/libraries/
 * <p>
 * https://platform.github.community/t/welcome-to-github-platform-forum/979
 * <p>
 * https://github.com/eclipse/egit-github/tree/master/org.eclipse.egit.github.core
 */
public class GitHubAPI {

    private AccessToken mAccessToken = new AccessToken();

    private GitHubAPI() {
    }

    public static TrafficBuilder traffic() {
        return new GitHubAPI().new TrafficBuilder();
    }

    public static RepositoryBuilder repository() {
        return new GitHubAPI().new RepositoryBuilder();
    }

    public static UserBuilder user() {
        return new GitHubAPI().new UserBuilder();
    }

    public static StargazersBuilder stargazers() {
        return new GitHubAPI().new StargazersBuilder();
    }

    public static AuthBuilder auth() {
        return new GitHubAPI().new AuthBuilder();
    }

    public static TrendingBuilder trending() {
        return new GitHubAPI().new TrendingBuilder();
    }

    public class StargazersBuilder extends Builder {
        private String mUrl = GithubServiceGenerator.API_HTTPS_BASE_URL;
        private String mHeader = "application/vnd.github.v3.star+json";
        private String mLogin;
        private String mRepositoryName;
        private String mPage;
        private long mDate;

        private StargazersBuilder() {
        }

        public StargazersBuilder setToken(String token) {
            return (StargazersBuilder) super.setToken(token);
        }

        public StargazersBuilder setTokenType(String tokenType) {
            return (StargazersBuilder) super.setTokenType(tokenType);
        }

        public StargazersBuilder setRepository(Repository repository) {
            mRepositoryName = repository.getName();
            mLogin = repository.getOwner().getLogin();
            return this;
        }

        public StargazersBuilder setPage(String page) {
            mPage = page;
            return this;
        }

        public StargazersBuilder setDate(long date) {
            mDate = date;
            return this;
        }

        public List<Star> getStars() throws IOException {
            Call<List<Star>> dummyCall = createGithubService().getStargazers(
                    mLogin, mRepositoryName, mPage);
            Response<List<Star>> paginationResponse = dummyCall.execute();
            if (paginationResponse.isSuccessful()) {

                Pagination pagination = new Pagination();
                pagination.parse(paginationResponse);
                int page = pagination.getLastPage();

                List<Star> stars = new ArrayList<>();
                for (int i = page; page > 0; i--) {
                    Call<List<Star>> call = createGithubService().getStargazers(
                            mLogin, mRepositoryName, String.valueOf(i));
                    Response<List<Star>> response = call.execute();
                    if (response.isSuccessful()) {
                        List<Star> starsForPage = response.body();
                        if (starsForPage != null) {
                            stars.addAll(starsForPage);

                            if (starsForPage.size() > 0
                                    && TimeConverter.iso8601ToMilliseconds(
                                    starsForPage.get(0).getStarredAt()) < mDate) {
                                break;
                            }

                        } else {
                            throw new IOException();
                        }
                    } else {
                        APIError error = APIError.parseError(paginationResponse);
                        throw new IOException(error.getMessage());
                    }
                }
                return stars;
            } else {
                APIError error = APIError.parseError(paginationResponse);
                throw new IOException(error.getMessage());
            }
        }

        private GithubService createGithubService() {
            return GithubServiceGenerator.createService(
                    GithubService.class, mAccessToken, mUrl, mHeader);
        }
    }

    public class TrafficBuilder extends Builder {
        private String mUrl = GithubServiceGenerator.API_HTTPS_BASE_URL;
        private String mHeader = "application/vnd.github.spiderman-preview+json";
        private String mLogin;
        private String mRepositoryName;
        private String mPeriod;

        private TrafficBuilder() {
        }

        public TrafficBuilder setToken(String token) {
            return (TrafficBuilder) super.setToken(token);
        }

        public TrafficBuilder setTokenType(String tokenType) {
            return (TrafficBuilder) super.setTokenType(tokenType);
        }

        public TrafficBuilder setRepository(Repository repository) {
            mRepositoryName = repository.getName();
            mLogin = repository.getOwner().getLogin();
            return this;
        }

        public TrafficBuilder setPeriod(String period) {
            mPeriod = period;
            return this;
        }

        public Clones getClones() throws IOException {
            Call<Clones> call = createGithubService().getRepositoryClones(
                    mLogin, mRepositoryName, mPeriod);

            Response<Clones> response = call.execute();
            if (response.isSuccessful()) {
                Clones clones = response.body();
                if (clones != null && clones.asList() != null) {
                    return clones;
                } else {
                    throw new IOException();
                }
            } else {
                APIError error = APIError.parseError(response);
                throw new IOException(error.getMessage());
            }
        }

        public List<ReferringSite> getReferringSites() throws IOException {
            Call<List<ReferringSite>> call = createGithubService().getTopReferrers(
                    mLogin, mRepositoryName);
            Response<List<ReferringSite>> response = call.execute();
            if (response.isSuccessful()) {
                List<ReferringSite> referringSites = response.body();
                if (referringSites != null) {
                    return referringSites;
                } else {
                    throw new IOException();
                }
            } else {
                APIError error = APIError.parseError(response);
                throw new IOException(error.getMessage());
            }
        }

        public Views getViews() throws IOException {
            Call<Views> call = createGithubService().getRepositoryViews(
                    mLogin, mRepositoryName, mPeriod);
            Response<Views> response = call.execute();
            if (response.isSuccessful()) {
                Views views = response.body();
                if (views != null && views.getViews() != null) {
                    return views;
                } else {
                    throw new IOException();
                }
            } else {
                APIError error = APIError.parseError(response);
                throw new IOException(error.getMessage());
            }
        }

        private GithubService createGithubService() {
            return GithubServiceGenerator.createService(
                    GithubService.class, mAccessToken, mUrl, mHeader);
        }
    }

    public class RepositoryBuilder extends Builder {

        private RepositoryBuilder() {
        }

        public RepositoryBuilder setToken(String token) {
            return (RepositoryBuilder) super.setToken(token);
        }

        public List<Repository> getRepositories() throws IOException {
            RepositoryService service = new RepositoryService();
            service.getClient().setOAuth2Token(mAccessToken.getToken());
            return service.getRepositories();
        }
    }

    public class UserBuilder extends Builder {

        private UserBuilder() {
        }

        public UserBuilder setToken(String token) {
            return (UserBuilder) super.setToken(token);
        }

        public User getUser() throws IOException {
            UserService service = new UserService();
            service.getClient().setOAuth2Token(mAccessToken.getToken());
            org.eclipse.egit.github.core.User eGitUser = service.getUser();
            User user = new User();
            user.setName(eGitUser.getName());
            user.setLogin(eGitUser.getLogin());
            user.setAvatarUrl(eGitUser.getAvatarUrl());
            user.setFollowers(String.valueOf(eGitUser.getFollowers()));
            return user;
        }
    }

    public class Builder {

        private Builder() {
        }

        public Builder setToken(String token) {
            GitHubAPI.this.mAccessToken.setAccessToken(token);
            return this;
        }

        public Builder setTokenType(String tokenType) {
            GitHubAPI.this.mAccessToken.setTokenType(tokenType);
            return this;
        }
    }

    public class AuthBuilder {

        private String mCode;

        private AuthBuilder() {
        }

        public AuthBuilder setCode(String code) {
            mCode = code;
            return this;
        }

        public AccessToken requestAccessToken() throws IOException {
            GithubService loginService = GithubServiceGenerator.createService(
                    GithubService.class);
            Call<AccessToken> call = loginService.getAccessToken(mCode,
                    GithubService.clientId, GithubService.clientSecret);
            return call.execute().body();
        }
    }

    public class TrendingBuilder {

        private String mLanguage;
        private String mPeriod;

        private TrendingBuilder() {
        }

        public TrendingBuilder setLanguage(String language) {
            mLanguage = language;
            return this;
        }

        public TrendingBuilder setPeriod(String period) {
            mPeriod = period;
            return this;
        }

        public List<TrendingRepository> getRepositories() throws IOException {

            GithubService githubService = ThirdPartyGithubServiceGenerator.createService(
                    GithubService.class);
            Call<List<TrendingRepository>> call = githubService.getTrendingRepositories(mLanguage,
                    mPeriod);

            List<TrendingRepository> repositories = call.execute().body();

            if (repositories != null) {
                return repositories;
            } else {
                throw new IOException();
            }
        }
    }
}
