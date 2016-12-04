package com.dmitrymalkovich.android.githubanalytics.data.source.remote;

import android.content.ContentResolver;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.annotation.WorkerThread;
import android.util.Log;

import com.dmitrymalkovich.android.githubapi.GitHubAPI;
import com.dmitrymalkovich.android.githubapi.core.pagination.Pagination;
import com.dmitrymalkovich.android.githubapi.core.service.GithubService;
import com.dmitrymalkovich.android.githubapi.core.service.ThirdPartyGithubServiceGenerator;
import com.dmitrymalkovich.android.githubapi.core.gson.Clones;
import com.dmitrymalkovich.android.githubapi.core.gson.ReferringSite;
import com.dmitrymalkovich.android.githubapi.core.gson.Star;
import com.dmitrymalkovich.android.githubapi.core.gson.Views;
import com.dmitrymalkovich.android.githubanalytics.data.source.GithubDataSource;
import com.dmitrymalkovich.android.githubanalytics.data.source.local.GithubLocalDataSource;
import com.dmitrymalkovich.android.githubapi.core.gson.AccessToken;
import com.dmitrymalkovich.android.githubapi.core.gson.TrendingRepository;
import com.dmitrymalkovich.android.githubapi.core.gson.User;
import com.dmitrymalkovich.android.githubanalytics.util.ActivityUtils;
import com.dmitrymalkovich.android.githubanalytics.util.TimeUtils;
import com.google.firebase.crash.FirebaseCrash;

import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.client.RequestException;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import retrofit2.Call;

public class GithubRemoteDataSource implements GithubDataSource {
    private static String LOG_TAG = GithubRemoteDataSource.class.getSimpleName();

    private static GithubRemoteDataSource INSTANCE;
    private final GithubLocalDataSource mLocalDataSource;
    @SuppressWarnings("unused")
    private ContentResolver mContentResolver;
    private SharedPreferences mPreferences;

    private GithubRemoteDataSource(ContentResolver contentResolver, SharedPreferences preferences) {
        mContentResolver = contentResolver;
        mPreferences = preferences;
        mLocalDataSource = GithubLocalDataSource.getInstance(mContentResolver, mPreferences);
    }

    public static GithubRemoteDataSource getInstance(ContentResolver contentResolver, SharedPreferences preferences) {
        if (INSTANCE == null) {
            INSTANCE = new GithubRemoteDataSource(contentResolver, preferences);
        }
        return INSTANCE;
    }

    @Override
    public void logout() {
        GithubLocalDataSource.getInstance(mContentResolver, mPreferences).logout();
    }

    @WorkerThread
    public List<Repository> getRepositoriesSync() {
        try {
            List<Repository> repositories = GitHubAPI.repository()
                    .setToken(getToken())
                    .getRepositories();
            mLocalDataSource.saveRepositories(repositories);
            return repositories;
        } catch (IOException e) {
            if (e instanceof RequestException) {
                if (((RequestException) e).getStatus() == 401) {
                    saveToken(null, null);
                }
            }
            if (ActivityUtils.isNetworkAvailable()) {
                FirebaseCrash.report(e);
            }
            return null;
        }
    }

    @Override
    public void getRepositories(final GetRepositoriesCallback callback) {
        new AsyncTask<Void, Void, List<Repository>>() {
            @Override
            protected List<Repository> doInBackground(Void... params) {
                return getRepositoriesSync();
            }

            @Override
            protected void onPostExecute(List<Repository> repositoryList) {
                if (repositoryList != null) {
                    callback.onRepositoriesLoaded(repositoryList);
                } else {
                    callback.onDataNotAvailable();
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @WorkerThread
    public void getRepositoriesWithAdditionalInfoSync(List<Repository> repositories) {
        if (repositories != null) {
            Collections.sort(repositories, new Comparator<Repository>() {
                @Override
                public int compare(Repository repository, Repository t1) {
                    return t1.getWatchers() - repository.getWatchers();
                }
            });
            if (repositories.size() > 0) {
                Repository mostPopularRepository = repositories.get(0);
                if (mostPopularRepository != null) {
                    getRepositoryReferrersSync(mostPopularRepository);
                    getRepositoryViewsSync(mostPopularRepository, "day");
                    getRepositoryClonesSync(mostPopularRepository, "day");
                    getStargazersSync(mostPopularRepository);
                }
            }
            if (repositories.size() > 1) {
                Repository mostPopularRepository = repositories.get(1);
                if (mostPopularRepository != null) {
                    getRepositoryReferrersSync(mostPopularRepository);
                    getRepositoryViewsSync(mostPopularRepository, "day");
                    getRepositoryClonesSync(mostPopularRepository, "day");
                    getStargazersSync(mostPopularRepository);
                }
            }
            if (repositories.size() > 2) {
                Repository mostPopularRepository = repositories.get(2);
                if (mostPopularRepository != null) {
                    getRepositoryReferrersSync(mostPopularRepository);
                    getRepositoryViewsSync(mostPopularRepository, "day");
                    getRepositoryClonesSync(mostPopularRepository, "day");
                    getStargazersSync(mostPopularRepository);
                }
            }
        }
    }

    @WorkerThread
    private void getRepositoriesWithAdditionalInfoSync(long repositoryId,
                                                       List<Repository> repositories) {
        if (repositories != null) {
            for (Repository repository : repositories) {
                if (repository.getId() == repositoryId) {
                    getRepositoryReferrersSync(repository);
                    getRepositoryViewsSync(repository, "day");
                    getRepositoryClonesSync(repository, "day");
                    getStargazersSync(repository);
                }
            }
        }
    }

    @Override
    public void getRepositoriesWithAdditionalInfo(final GetRepositoriesCallback callback, boolean useCache) {
        new AsyncTask<Void, Void, List<Repository>>() {
            @Override
            protected List<Repository> doInBackground(Void... params) {
                List<Repository> repositories = getRepositoriesSync();
                getRepositoriesWithAdditionalInfoSync(repositories);
                return repositories;
            }

            @Override
            protected void onPostExecute(List<Repository> repositoryList) {
                if (repositoryList != null) {
                    callback.onRepositoriesLoaded(repositoryList);
                } else {
                    callback.onDataNotAvailable();
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void getRepositoriesWithAdditionalInfo(final long repositoryId,
                                                  final GetRepositoriesCallback callback, boolean useCache) {
        new AsyncTask<Void, Void, List<Repository>>() {
            @Override
            protected List<Repository> doInBackground(Void... params) {
                List<Repository> repositories = getRepositoriesSync();
                getRepositoriesWithAdditionalInfoSync(repositoryId, repositories);
                return repositories;
            }

            @Override
            protected void onPostExecute(List<Repository> repositoryList) {
                if (repositoryList != null) {
                    callback.onRepositoriesLoaded(repositoryList);
                } else {
                    callback.onDataNotAvailable();
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @WorkerThread
    private Clones getRepositoryClonesSync(final Repository repository, String period) {
        try {
            Clones clones = GitHubAPI.traffic()
                    .setToken(getToken())
                    .setTokenType(getTokenType())
                    .setRepository(repository)
                    .setPeriod(period)
                    .getClones();

            mLocalDataSource.saveClones(repository.getId(), clones);

            return clones;

        } catch (IOException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            return null;
        }
    }

    @Override
    public void getRepositoryClones(final Repository repository, final String period,
                                    final GetRepositoryClonesCallback callback) {
        new AsyncTask<Void, Void, Clones>() {
            @Override
            protected Clones doInBackground(Void... params) {
                return getRepositoryClonesSync(repository, period);
            }

            @Override
            protected void onPostExecute(Clones clones) {
                if (clones != null) {
                    callback.onRepositoryClonesLoaded(clones);
                } else {
                    callback.onDataNotAvailable();
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @WorkerThread
    private Views getRepositoryViewsSync(final Repository repository, String period) {
        try {
            Views views = GitHubAPI.traffic()
                    .setToken(getToken())
                    .setTokenType(getTokenType())
                    .setRepository(repository)
                    .setPeriod(period)
                    .getViews();

            mLocalDataSource.saveViews(repository.getId(), views);
            return views;

        } catch (IOException e) {
            if (ActivityUtils.isNetworkAvailable()) {
                FirebaseCrash.report(e);
            }
            return null;
        }
    }

    @WorkerThread
    public void getUserSync() {
        try {
            User user = GitHubAPI.user()
                    .setToken(getToken())
                    .getUser();

            mLocalDataSource.saveUser(user);

        } catch (IOException e) {
            if (ActivityUtils.isNetworkAvailable()) {
                FirebaseCrash.report(e);
            }
        }
    }

    @Override
    public void getRepositoryViews(final Repository repository, final String period,
                                   final GetRepositoryViewsCallback callback) {
        new AsyncTask<Void, Void, Views>() {
            @Override
            protected Views doInBackground(Void... params) {
                return getRepositoryViewsSync(repository, period);
            }

            @Override
            protected void onPostExecute(Views views) {
                if (views != null) {
                    callback.onRepositoryViewsLoaded(views);
                } else {
                    callback.onDataNotAvailable();
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @SuppressWarnings("unused")
    @WorkerThread
    public List<TrendingRepository> getTrendingRepositoriesSync(final String period,
                                                                final String language,
                                                                boolean useCache) {
        try {
            AccessToken accessToken = new AccessToken();
            accessToken.setAccessToken(getToken());
            accessToken.setTokenType(getTokenType());

            GithubService githubService = ThirdPartyGithubServiceGenerator.createService(
                    GithubService.class);
            Call<List<TrendingRepository>> call = githubService.getTrendingRepositories(language,
                    period);

            List<TrendingRepository> trendingRepositoryList = call.execute().body();

            if (trendingRepositoryList != null) {
                GithubLocalDataSource localDataSource =
                        GithubLocalDataSource.getInstance(mContentResolver, mPreferences);
                localDataSource.saveTrendingRepositories(period, language, trendingRepositoryList);

                return trendingRepositoryList;
            } else {
                return null;
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            return null;
        }
    }

    @Override
    public void getTrendingRepositories(final String period, final String language,
                                        final GetTrendingRepositories callback, final boolean useCache) {
        new AsyncTask<Void, Void, List<TrendingRepository>>() {
            @Override
            protected List<TrendingRepository> doInBackground(Void... params) {
                return getTrendingRepositoriesSync(period, language, useCache);
            }

            @Override
            protected void onPostExecute(List<TrendingRepository> trendingRepositoryList) {
                if (trendingRepositoryList != null) {
                    callback.onTrendingRepositoriesLoaded(trendingRepositoryList, language, period);
                } else {
                    callback.onDataNotAvailable();
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @SuppressWarnings("unused")
    @WorkerThread
    private List<ReferringSite> getRepositoryReferrersSync(final Repository repository) {
        try {
            List<ReferringSite> referringSites = GitHubAPI.traffic()
                    .setToken(getToken())
                    .setTokenType(getTokenType())
                    .setRepository(repository)
                    .getReferringSites();
            mLocalDataSource.saveReferringSites(repository.getId(), referringSites);
            return referringSites;
        } catch (IOException e) {
            if (ActivityUtils.isNetworkAvailable()) {
                FirebaseCrash.report(e);
            }
            return null;
        }
    }

    @Override
    public void getRepositoryReferrers(final Repository repository,
                                       final GetRepositoryReferrersCallback callback) {
        new AsyncTask<Void, Void, List<ReferringSite>>() {
            @Override
            protected List<ReferringSite> doInBackground(Void... params) {
                return getRepositoryReferrersSync(repository);
            }

            @Override
            protected void onPostExecute(List<ReferringSite> referringSiteList) {
                if (referringSiteList != null) {
                    callback.onRepositoryReferrersLoaded(referringSiteList);
                } else {
                    callback.onDataNotAvailable();
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void requestTokenFromCode(final String code,
                                     final RequestTokenFromCodeCallback callback) {
        new AsyncTask<Void, Void, AccessToken>() {
            @Override
            protected AccessToken doInBackground(Void... params) {
                try {
                    return GitHubAPI.auth()
                            .setCode(code)
                            .requestAccessToken();
                } catch (IOException e) {
                    Log.e(LOG_TAG, e.getMessage(), e);
                }
                return null;
            }

            @Override
            protected void onPostExecute(AccessToken accessToken) {
                if (accessToken != null && accessToken.getToken() != null
                        && !accessToken.getToken().isEmpty()) {
                    callback.onTokenLoaded(accessToken.getToken(), accessToken.getTokenType());
                } else {
                    callback.onDataNotAvailable();
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void saveToken(String token, String tokenType) {
        GithubLocalDataSource.getInstance(mContentResolver, mPreferences)
                .saveToken(token, tokenType);
    }

    @Override
    public String getToken() {
        return mLocalDataSource.getToken();
    }

    @Override
    public String getTokenType() {
        return mLocalDataSource.getTokenType();
    }


    @Override
    public String getDefaultLanguageForTrending() {
        return mLocalDataSource.getDefaultLanguageForTrending();
    }

    @Override
    public void setDefaultLanguageForTrending(@GithubLocalDataSource.TrendingLanguage String
                                                      language) {
        mLocalDataSource.setDefaultLanguageForTrending(language);
    }

    @Override
    public String getDefaultPeriodForTrending() {
        return mLocalDataSource.getDefaultPeriodForTrending();
    }

    @Override
    public void setDefaultPeriodForTrending(@GithubLocalDataSource.TrendingPeriod String
                                                    period) {
        mLocalDataSource.setDefaultPeriodForTrending(period);
    }

    @Override
    public void getUser(final GerUserCallback callback) {
        new AsyncTask<Void, Void, User>() {
            @Override
            protected User doInBackground(Void... params) {
                try {
                    return GitHubAPI.user()
                            .setToken(getToken())
                            .getUser();
                } catch (IOException e) {
                    if (ActivityUtils.isNetworkAvailable()) {
                        FirebaseCrash.report(e);
                    }
                }
                return null;
            }

            @Override
            protected void onPostExecute(User user) {
                if (user != null) {
                    callback.onUserLoaded(user);
                } else {
                    callback.onDataNotAvailable();
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void getStargazers(final Repository repository, final GetStargazersCallback callback) {
        new AsyncTask<Void, Void, List<Star>>() {
            @Override
            protected List<Star> doInBackground(Void... params) {
                return getStargazersSync(repository);
            }

            @Override
            protected void onPostExecute(List<Star> starList) {
                if (starList != null) {
                    callback.onStargazersLoaded(starList);
                } else {
                    callback.onDataNotAvailable();
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @WorkerThread
    private List<Star> getStargazersSync(Repository repository) {
        try {
            List<Star> stars = GitHubAPI.stargazers()
                    .setToken(getToken())
                    .setTokenType(getTokenType())
                    .setRepository(repository)
                    .setPage(Pagination.LAST_PAGE)
                    .setDate(TimeUtils.twoWeeksAgo())
                    .getStars();
            mLocalDataSource.saveStargazers(repository, stars);
            return stars;
        } catch (IOException e) {
            if (ActivityUtils.isNetworkAvailable()) {
                FirebaseCrash.report(e);
            }
            return null;
        }
    }
}
