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

public class GithubRepository implements GithubDataSource {
    @SuppressWarnings("unused")
    private static final String LOG_TAG = GithubRepository.class.getSimpleName();
    public static final String PREFERENCES = "GITHUB_ANALYTICS_PREFERENCES";
    private static GithubRepository INSTANCE = null;

    private final GithubDataSource mGithubRemoteDataSource;
    private final GithubDataSource mGithubLocalDataSource;
    private SyncSettings mSyncSettings;

    public static class Injection {

        public static GithubRepository provideGithubRepository(@NonNull Context context) {
            checkNotNull(context);
            SharedPreferences sharedPreferences =
                    context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
            return GithubRepository.getInstance(GithubRemoteDataSource.getInstance(
                    context.getContentResolver(), sharedPreferences), provideLocalDataSource(context),
                    sharedPreferences);
        }

        private static GithubLocalDataSource provideLocalDataSource(@NonNull Context context) {
            checkNotNull(context);
            return GithubLocalDataSource.getInstance(context.getContentResolver(),
                    context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE));
        }

        public static GithubRemoteDataSource provideRemoteDataSource(@NonNull Context context) {
            checkNotNull(context);
            return GithubRemoteDataSource.getInstance(context.getContentResolver(),
                    context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE));
        }
    }

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

    @Override
    public void logout() {
        mGithubLocalDataSource.logout();
    }

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

    @Override
    public void saveToken(String token, String tokenType) {
        mGithubLocalDataSource.saveToken(token, tokenType);
    }

    @Override
    public String getToken() {
        String token = mGithubLocalDataSource.getToken();
        return token != null && !token.isEmpty() ? token : null;
    }

    @Override
    public String getTokenType() {
        String tokenType = mGithubLocalDataSource.getTokenType();
        return tokenType != null && !tokenType.isEmpty() ? tokenType : null;
    }

    @Override
    public String getDefaultLanguageForTrending() {
        return mGithubLocalDataSource.getDefaultLanguageForTrending();
    }

    @Override
    public void setDefaultLanguageForTrending(@GithubLocalDataSource.TrendingLanguage String language) {
        mGithubLocalDataSource.setDefaultLanguageForTrending(language);
    }

    @Override
    public String getDefaultPeriodForTrending() {
        return mGithubLocalDataSource.getDefaultPeriodForTrending();
    }

    @Override
    public void setDefaultPeriodForTrending(@GithubLocalDataSource.TrendingPeriod String period) {
        mGithubLocalDataSource.setDefaultPeriodForTrending(period);
    }

    @Override
    public void setPinned(boolean active, long id) {
        mGithubLocalDataSource.setPinned(active, id);
    }

    public interface LoadDataCallback {
        void onDataLoaded(Cursor data, int id);

        void onDataEmpty(int id);

        void onDataNotAvailable(int id);

        void onDataReset();
    }
}
