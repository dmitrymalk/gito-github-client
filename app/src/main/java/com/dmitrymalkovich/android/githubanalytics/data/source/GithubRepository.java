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

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.util.Log;

import com.dmitrymalkovich.android.githubanalytics.data.source.local.GithubLocalDataSource;
import com.dmitrymalkovich.android.githubanalytics.data.source.local.contract.ReferrerContract;
import com.dmitrymalkovich.android.githubanalytics.data.source.local.contract.TrendingContract;
import com.dmitrymalkovich.android.githubanalytics.data.source.remote.GithubRemoteDataSource;
import com.dmitrymalkovich.android.githubapi.core.gson.Clones;
import com.dmitrymalkovich.android.githubapi.core.gson.ReferringSite;
import com.dmitrymalkovich.android.githubapi.core.gson.Star;
import com.dmitrymalkovich.android.githubapi.core.gson.TrendingRepository;
import com.dmitrymalkovich.android.githubapi.core.gson.User;
import com.dmitrymalkovich.android.githubapi.core.gson.Views;
import com.dmitrymalkovich.android.githubanalytics.data.sync.SyncSettings;

import org.eclipse.egit.github.core.Repository;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * This class is used for the access of information regarding Github repositories. In order to use
 * it initialize it through the {@link Injection} subclass with the provideGithubRepository method
 */
public class GithubRepository implements GithubDataSource {
    @SuppressWarnings("unused")
    private static final String LOG_TAG = GithubRepository.class.getSimpleName();
    public static final String PREFERENCES = "GITHUB_ANALYTICS_PREFERENCES";
    private static GithubRepository INSTANCE = null; // a static reference to the repository instance

    private final GithubDataSource mGithubRemoteDataSource;
    private final GithubDataSource mGithubLocalDataSource;
    private SyncSettings mSyncSettings;

    /**
     * This class is used to access the instance of the {@link GithubRepository} class
     * and the data sources
     */
    public static class Injection {

        /**
         * Used to access the {@link GithubRepository} static instance
         * @return the static instance of the {@link GithubRepository} class
         */
        public static GithubRepository provideGithubRepository(@NonNull Context context) {
            checkNotNull(context);
            SharedPreferences sharedPreferences =
                    context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
            return GithubRepository.getInstance(GithubRemoteDataSource.getInstance(
                    context.getContentResolver(), sharedPreferences), provideLocalDataSource(context),
                    sharedPreferences);
        }

        /**
         * Used to access the {@link GithubLocalDataSource} static instance
         * @return the static instance of the {@link GithubLocalDataSource} class
         */
        private static GithubLocalDataSource provideLocalDataSource(@NonNull Context context) {
            checkNotNull(context);
            return GithubLocalDataSource.getInstance(context.getContentResolver(),
                    context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE));
        }

        /**
         * Used to access the {@link GithubRemoteDataSource} static instance
         * @return the static instance of the {@link GithubRemoteDataSource} class
         */
        public static GithubRemoteDataSource provideRemoteDataSource(@NonNull Context context) {
            checkNotNull(context);
            return GithubRemoteDataSource.getInstance(context.getContentResolver(),
                    context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE));
        }
    }

    /**
     * Initialize the static instance for the Github repository
     * @param githubRemoteDataSource the remote datasource to retrieve data from
     * @param githubLocaldataSource the local datasource to retrieve data from
     * @param preferences the application's set preferences
     */
    private GithubRepository(@NonNull GithubDataSource githubRemoteDataSource,
                             @NonNull GithubDataSource githubLocalDataSource,
                             @NonNull SharedPreferences preferences) {
        mGithubRemoteDataSource = checkNotNull(githubRemoteDataSource);
        mGithubLocalDataSource = checkNotNull(githubLocalDataSource);
        mSyncSettings = new SyncSettings(preferences);
    }

    /**
     * Returns the single instance of this class, creating it if necessary.
     *
     * @param githubRemoteDataSource the backend data source
     * @param githubLocalDataSource  the device storage data source
     * @return the {@link GithubRepository} instance
     */
    private static GithubRepository getInstance(GithubDataSource githubRemoteDataSource,
                                                GithubDataSource githubLocalDataSource,
                                                SharedPreferences preferences) {
        if (INSTANCE == null) {
            INSTANCE = new GithubRepository(githubRemoteDataSource, githubLocalDataSource, preferences);
        }
        return INSTANCE;
    }

    /**
     * Logout of your github account and remove the repositories associated
     * with your account from the cache
     */
    @Override
    public void logout() {
        mGithubLocalDataSource.logout();
    }

    /**
     * Get the user's' repositories 
     * @param callback called when the repositories are loaded
     * @param useCache if false the list of repositories are updated from online
     */
    @Override
    public void getRepositoriesWithAdditionalInfo(final GetRepositoriesCallback callback, boolean useCache) {
        final String key = ReferrerContract.ReferrerEntry.TABLE_NAME;
        if (useCache && mSyncSettings.isSynced(key)) {
            Log.i(LOG_TAG, "Cache was used for " + key);
            callback.onRepositoriesLoaded(new ArrayList<Repository>());
            return;
        }
        mGithubRemoteDataSource.getRepositoriesWithAdditionalInfo(new GetRepositoriesCallback() {
            @Override
            public void onRepositoriesLoaded(List<Repository> repositories) {
                if (repositories.size() > 0) {
                    mSyncSettings.synced(key);
                }
                callback.onRepositoriesLoaded(repositories);
            }

            @Override
            public void onDataNotAvailable() {
                callback.onDataNotAvailable();
            }
        }, useCache);
    }

    /**
     * Get the repository of the provided repository id
     * @param repositoryId the id of the repository to find
     * @param callback called when the repository is found or not found
     * @param useCache whether or not the cache should be used to search for the repository
     */
    @Override
    public void getRepositoriesWithAdditionalInfo(long repositoryId, final GetRepositoriesCallback callback,
                                                  boolean useCache) {
        final String key = ReferrerContract.ReferrerEntry.TABLE_NAME + repositoryId;
        if (useCache && mSyncSettings.isSynced(key)) {
            Log.i(LOG_TAG, "Cache was used for " + key);
            callback.onRepositoriesLoaded(new ArrayList<Repository>());
            return;
        }
        mGithubRemoteDataSource.getRepositoriesWithAdditionalInfo(repositoryId,
                new GetRepositoriesCallback() {
                    @Override
                    public void onRepositoriesLoaded(List<Repository> repositories) {
                        if (repositories.size() > 0) {
                            mSyncSettings.synced(key);
                        }
                        callback.onRepositoriesLoaded(repositories);
                    }

                    @Override
                    public void onDataNotAvailable() {
                        callback.onDataNotAvailable();
                    }
                }, useCache);
    }

    /**
     * Returns a list of all the user repositories
     * @param callback called when the repositories are all retrieved
     */
    @Override
    public void getRepositories(final GetRepositoriesCallback callback) {
        mGithubRemoteDataSource.getRepositories(new GetRepositoriesCallback() {
            @Override
            public void onRepositoriesLoaded(List<Repository> repositoryList) {
                callback.onRepositoriesLoaded(repositoryList);
            }

            @Override
            public void onDataNotAvailable() {
                callback.onDataNotAvailable();
            }
        });
    }

    /**
     * Retrieve a list of sites that link to this repository
     * @param repository the repository to find the referers for
     * @param callback called with the list of referring sites
     */
    @Override
    public void getRepositoryReferrers(Repository repository, final GetRepositoryReferrersCallback callback) {
        mGithubRemoteDataSource.getRepositoryReferrers(repository, new GetRepositoryReferrersCallback() {
            @Override
            public void onRepositoryReferrersLoaded(List<ReferringSite> referringSiteList) {
                callback.onRepositoryReferrersLoaded(referringSiteList);
            }

            @Override
            public void onDataNotAvailable() {
                callback.onDataNotAvailable();
            }
        });
    }

    /**
     * Retrieve the number of cloans the repository has had
     * @param repository the repository we are analyzing
     * @param period the period of time to look in 
     * @param callback called with the resulting number of clones
     */
    @Override
    public void getRepositoryClones(Repository repository, String period, final GetRepositoryClonesCallback callback) {
        mGithubRemoteDataSource.getRepositoryClones(repository, period, new GetRepositoryClonesCallback() {
            @Override
            public void onRepositoryClonesLoaded(Clones clones) {
                callback.onRepositoryClonesLoaded(clones);
            }

            @Override
            public void onDataNotAvailable() {
                callback.onDataNotAvailable();
            }
        });
    }

    /**
     * Retrive the number of views for the respository
     * @param repository the repository for which to find the number of views
     * @param period the period in which the views should have taken place
     * @param callback called with the number of views
     */
    @Override
    public void getRepositoryViews(Repository repository, String period, final GetRepositoryViewsCallback callback) {
        mGithubRemoteDataSource.getRepositoryViews(repository, period, new GetRepositoryViewsCallback() {
            @Override
            public void onRepositoryViewsLoaded(Views views) {
                callback.onRepositoryViewsLoaded(views);
            }

            @Override
            public void onDataNotAvailable() {
                callback.onDataNotAvailable();
            }
        });
    }

    /**
     * Retrieve a list of users who starred the repository
     * @param repository the repository which users have starred
     * @param callback called with the list of users who starred the repository
     */
    @Override
    public void getStargazers(Repository repository, final GetStargazersCallback callback) {
        mGithubRemoteDataSource.getStargazers(repository, new GetStargazersCallback() {
            @Override
            public void onStargazersLoaded(List<Star> starList) {
                callback.onStargazersLoaded(starList);
            }

            @Override
            public void onDataNotAvailable() {
                callback.onDataNotAvailable();
            }
        });
    }

    /**
     * Retrieve a list of the trending repositories
     * @param period the period in which the repositories were trending
     * @param language specify the programming language 
     * @param callback called with the list of trending repositories
     * @param useCache whether or not to use the cached trending repository information
     */
    @Override
    public void getTrendingRepositories(String period, String language,
                                        final GetTrendingRepositories callback, boolean useCache) {
        final String key = TrendingContract.TrendingEntry.TABLE_NAME + language.hashCode() + period.hashCode();
        if (useCache && mSyncSettings.isSynced(key)) {

            Log.i(LOG_TAG, "Cache was used for " + key);

            callback.onTrendingRepositoriesLoaded(new ArrayList<TrendingRepository>(), language, period);
            return;
        }

        mGithubRemoteDataSource.getTrendingRepositories(period, language, new GetTrendingRepositories() {
            @Override
            public void onTrendingRepositoriesLoaded(List<TrendingRepository> repositories,
                                                     String language, String period) {
                if (repositories.size() > 0) {
                    mSyncSettings.synced(key);
                }
                callback.onTrendingRepositoriesLoaded(repositories, language, period);
            }

            @Override
            public void onDataNotAvailable() {
                callback.onDataNotAvailable();
            }
        }, useCache);
    }

    /**
     * Request an access token 
     * @param code the passcode to get the token
     * @param callback retrieves the requested token and token type
     */
    @Override
    public void requestTokenFromCode(final String code, final RequestTokenFromCodeCallback callback) {
        mGithubRemoteDataSource.requestTokenFromCode(code, new RequestTokenFromCodeCallback() {
            @Override
            public void onTokenLoaded(String token, String tokenType) {
                mGithubLocalDataSource.saveToken(token, tokenType);
                callback.onTokenLoaded(token, tokenType);
            }

            @Override
            public void onDataNotAvailable() {
                callback.onDataNotAvailable();
            }
        });
    }

    /**
     * Get the current user
     * @param callback called with the user information
     */
    @Override
    public void getUser(final GerUserCallback callback) {
        mGithubRemoteDataSource.getUser(new GerUserCallback() {
            @Override
            public void onUserLoaded(User user) {
                callback.onUserLoaded(user);
            }

            @Override
            public void onDataNotAvailable() {
                callback.onDataNotAvailable();
            }
        });
    }

    /**
     * save the token to local storage
     * @param token the token to save
     * @param tokenType the type of the token
     */
    @Override
    public void saveToken(String token, String tokenType) {
        mGithubLocalDataSource.saveToken(token, tokenType);
    }

    /**
     * Get the saved token. 
     * @return null if no token is saved
     */
    @Override
    public String getToken() {
        String token = mGithubLocalDataSource.getToken();
        return token != null && !token.isEmpty() ? token : null;
    }

    /**
     * Get the type of the saved token
     * @return null if no token is saved
     */
    @Override
    public String getTokenType() {
        String tokenType = mGithubLocalDataSource.getTokenType();
        return tokenType != null && !tokenType.isEmpty() ? tokenType : null;
    }

    /**
     * Get the default language for trending repositories
     * @return a String representation of a programming language
     */
    @Override
    public String getDefaultLanguageForTrending() {
        return mGithubLocalDataSource.getDefaultLanguageForTrending();
    }

    /**
     * Set the default language for trending repository search
     * @param language a string representation of the desired programming language
     */
    @Override
    public void setDefaultLanguageForTrending(@GithubLocalDataSource.TrendingLanguage String language) {
        mGithubLocalDataSource.setDefaultLanguageForTrending(language);
    }

    /**
     * Get the default period of time for the trending repsoitories
     * @return a string representation of the default period of time
     */
    @Override
    public String getDefaultPeriodForTrending() {
        return mGithubLocalDataSource.getDefaultPeriodForTrending();
    }

    /**
     * Set the default period of time for the trending repositories
     * @param period a string representation of a period of time
     */
    @Override
    public void setDefaultPeriodForTrending(@GithubLocalDataSource.TrendingPeriod String period) {
        mGithubLocalDataSource.setDefaultPeriodForTrending(period);
    }

    /**
     * Pin a repository so it can be viewed offline
     * @param active true to pin the repository, false to unpin it
     * @param id the id of the repository
     */
    @Override
    public void setPinned(boolean active, long id) {
        mGithubLocalDataSource.setPinned(active, id);
    }

    /**
     * The callback interface for when the repository data is asynchronously loaded
     */
    public interface LoadDataCallback {
        /**
         * Retrieve the retrieved data
         * @param data the data for the repository
         * @param id the id of the found repository
         */
        void onDataLoaded(Cursor data, int id);

        /**
         * When no data is found for a repository this is called
         * @param id the id of the repository
         */
        void onDataEmpty(int id);

        /**
         * Called when there is no data available for the repository or it cannot
         * be found
         * @param id the id of the repository
         */
        void onDataNotAvailable(int id);

        /**
         * Called when the data for the repository has been reset
         */
        void onDataReset();
    }
}
