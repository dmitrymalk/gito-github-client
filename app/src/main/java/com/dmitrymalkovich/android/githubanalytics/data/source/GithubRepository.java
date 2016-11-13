package com.dmitrymalkovich.android.githubanalytics.data.source;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.dmitrymalkovich.android.githubanalytics.data.source.local.GithubLocalDataSource;
import com.dmitrymalkovich.android.githubanalytics.data.source.local.contract.TrendingContract;
import com.dmitrymalkovich.android.githubanalytics.data.source.remote.GithubRemoteDataSource;
import com.dmitrymalkovich.android.githubanalytics.data.source.remote.gson.ResponseClones;
import com.dmitrymalkovich.android.githubanalytics.data.source.remote.gson.ResponseReferrer;
import com.dmitrymalkovich.android.githubanalytics.data.source.remote.gson.ResponseStargazers;
import com.dmitrymalkovich.android.githubanalytics.data.source.remote.gson.ResponseTrending;
import com.dmitrymalkovich.android.githubanalytics.data.source.remote.gson.ResponseUser;
import com.dmitrymalkovich.android.githubanalytics.data.source.remote.gson.ResponseViews;

import org.eclipse.egit.github.core.Repository;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class GithubRepository implements GithubDataSource {
    @SuppressWarnings("unused")
    private static final String LOG_TAG = GithubRepository.class.getSimpleName();
    private static GithubRepository INSTANCE = null;

    private final GithubDataSource mGithubRemoteDataSource;
    private final GithubDataSource mGithubLocalDataSource;
    private final SharedPreferences mPreferences;
    private Settings mSettings = new Settings();

    public static class Injection {

        public static GithubRepository provideGithubRepository(@NonNull Context context) {
            checkNotNull(context);
            SharedPreferences sharedPreferences =
                    context.getSharedPreferences("GITHUB_ANALYTICS_PREFERENCES", Context.MODE_PRIVATE);
            return GithubRepository.getInstance(GithubRemoteDataSource.getInstance(
                    context.getContentResolver(), sharedPreferences), provideLocalDataSource(context),
                    sharedPreferences);
        }

        private static GithubLocalDataSource provideLocalDataSource(@NonNull Context context) {
            checkNotNull(context);
            return GithubLocalDataSource.getInstance(context.getContentResolver(),
                    context.getSharedPreferences("GITHUB_ANALYTICS_PREFERENCES", Context.MODE_PRIVATE));
        }

        public static GithubRemoteDataSource provideRemoteDataSource(@NonNull Context context) {
            checkNotNull(context);
            return GithubRemoteDataSource.getInstance(context.getContentResolver(),
                    context.getSharedPreferences("GITHUB_ANALYTICS_PREFERENCES", Context.MODE_PRIVATE));
        }
    }

    private GithubRepository(@NonNull GithubDataSource githubRemoteDataSource,
                             @NonNull GithubDataSource githubLocalDataSource,
                             @NonNull SharedPreferences preferences) {
        mGithubRemoteDataSource = checkNotNull(githubRemoteDataSource);
        mGithubLocalDataSource = checkNotNull(githubLocalDataSource);
        mPreferences = preferences;
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
    public void login(final String username, final String password) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                mGithubRemoteDataSource.login(username, password);
                return null;
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void logout() {
        mGithubLocalDataSource.logout();
    }

    @Override
    public void getRepositoriesWithAdditionalInfo(final GetRepositoriesCallback callback) {
        mGithubRemoteDataSource.getRepositoriesWithAdditionalInfo(new GetRepositoriesCallback() {
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
    public void getRepositoriesWithAdditionalInfo(long repositoryId, final GetRepositoriesCallback callback) {
            mGithubRemoteDataSource.getRepositoriesWithAdditionalInfo(repositoryId,
                    new GetRepositoriesCallback() {
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
            public void onRepositoryReferrersLoaded(List<ResponseReferrer> responseReferrerList) {
                callback.onRepositoryReferrersLoaded(responseReferrerList);
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
            public void onRepositoryClonesLoaded(ResponseClones responseClones) {
                callback.onRepositoryClonesLoaded(responseClones);
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
            public void onRepositoryViewsLoaded(ResponseViews responseViews) {
                callback.onRepositoryViewsLoaded(responseViews);
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
            public void onStargazersLoaded(List<ResponseStargazers> responseStargazersList) {
                callback.onStargazersLoaded(responseStargazersList);
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
        if (useCache && mSettings.isSynced(key)) {

            Log.i(LOG_TAG, "Cache was used for " + key);

            callback.onTrendingRepositoriesLoaded(new ArrayList<ResponseTrending>(), language, period);
            return;
        }

        mGithubRemoteDataSource.getTrendingRepositories(period, language, new GetTrendingRepositories() {
            @Override
            public void onTrendingRepositoriesLoaded(List<ResponseTrending> repositories,
                                                     String language, String period) {
                if (repositories.size() > 0) {
                    mSettings.synced(key);
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
            public void onUserLoaded(ResponseUser user) {
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

    public interface LoadDataCallback {
        void onDataLoaded(Cursor data, int id);

        void onDataEmpty(int id);

        void onDataNotAvailable(int id);

        void onDataReset();
    }

    private class Settings {

        long SYNC_INTERVAL = 1000 * 60 * 60;

        boolean isSynced(String key) {
            long lastSyncTimeMillis = mPreferences.getLong(key, 0);
            return lastSyncTimeMillis != 0 &&
                    lastSyncTimeMillis - System.currentTimeMillis() < SYNC_INTERVAL;
        }

        void synced(String key) {
            SharedPreferences.Editor editor = mPreferences.edit();
            editor.putLong(key, System.currentTimeMillis());
            editor.apply();
        }
    }
}
