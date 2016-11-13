package com.dmitrymalkovich.android.githubanalytics.data.source.remote;

import android.content.ContentResolver;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.annotation.WorkerThread;
import android.util.Log;

import com.dmitrymalkovich.android.githubanalytics.data.source.GithubDataSource;
import com.dmitrymalkovich.android.githubanalytics.data.source.local.GithubLocalDataSource;
import com.dmitrymalkovich.android.githubanalytics.data.source.remote.gson.APIError;
import com.dmitrymalkovich.android.githubanalytics.data.source.remote.gson.ResponseAccessToken;
import com.dmitrymalkovich.android.githubanalytics.data.source.remote.gson.ResponseClones;
import com.dmitrymalkovich.android.githubanalytics.data.source.remote.gson.ResponseReferrer;
import com.dmitrymalkovich.android.githubanalytics.data.source.remote.gson.ResponseStargazers;
import com.dmitrymalkovich.android.githubanalytics.data.source.remote.gson.ResponseTrending;
import com.dmitrymalkovich.android.githubanalytics.data.source.remote.gson.ResponseUser;
import com.dmitrymalkovich.android.githubanalytics.data.source.remote.gson.ResponseViews;
import com.google.firebase.crash.FirebaseCrash;

import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.User;
import org.eclipse.egit.github.core.client.RequestException;
import org.eclipse.egit.github.core.service.RepositoryService;
import org.eclipse.egit.github.core.service.UserService;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class GithubRemoteDataSource implements GithubDataSource {
    private static String LOG_TAG = GithubRemoteDataSource.class.getSimpleName();

    private static GithubRemoteDataSource INSTANCE;
    @SuppressWarnings("unused")
    private ContentResolver mContentResolver;
    private SharedPreferences mPreferences;

    private GithubRemoteDataSource(ContentResolver contentResolver, SharedPreferences preferences) {
        mContentResolver = contentResolver;
        mPreferences = preferences;
    }

    public static GithubRemoteDataSource getInstance(ContentResolver contentResolver, SharedPreferences preferences) {
        if (INSTANCE == null) {
            INSTANCE = new GithubRemoteDataSource(contentResolver, preferences);
        }
        return INSTANCE;
    }

    @Override
    public void login(final String username, final String password) {
        try {
            RepositoryService service = new RepositoryService();
            service.getClient().setCredentials(username, password);
            service.getRepositories();

        } catch (IOException e) {
            FirebaseCrash.report(e);
        }
    }

    @Override
    public void logout() {
        GithubLocalDataSource.getInstance(mContentResolver, mPreferences).logout();
    }

    @WorkerThread
    public List<Repository> getRepositoriesSync() {
        try {
            String token = getToken();
            if (token != null) {
                RepositoryService service = new RepositoryService();
                service.getClient().setOAuth2Token(token);
                List<Repository> repositoryList = service.getRepositories();
                GithubLocalDataSource localDataSource =
                        GithubLocalDataSource.getInstance(mContentResolver, mPreferences);
                localDataSource.saveRepositories(repositoryList);
                return repositoryList;
            } else {
                return null;
            }
        } catch (IOException e) {
            if (e instanceof RequestException) {
                if (((RequestException) e).getStatus() == 401) {
                    saveToken(null, null);
                }
            }
            FirebaseCrash.report(e);
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
                    getStargazersSync(mostPopularRepository, "last");
                }
            }
            if (repositories.size() > 1) {
                Repository mostPopularRepository = repositories.get(1);
                if (mostPopularRepository != null) {
                    getRepositoryReferrersSync(mostPopularRepository);
                    getRepositoryViewsSync(mostPopularRepository, "day");
                    getRepositoryClonesSync(mostPopularRepository, "day");
                    getStargazersSync(mostPopularRepository, "last");
                }
            }
            if (repositories.size() > 2) {
                Repository mostPopularRepository = repositories.get(2);
                if (mostPopularRepository != null) {
                    getRepositoryReferrersSync(mostPopularRepository);
                    getRepositoryViewsSync(mostPopularRepository, "day");
                    getRepositoryClonesSync(mostPopularRepository, "day");
                    getStargazersSync(mostPopularRepository, "last");
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
                    getStargazersSync(repository, "last");
                }
            }
        }
    }

    @Override
    public void getRepositoriesWithAdditionalInfo(final GetRepositoriesCallback callback) {
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
                                                  final GetRepositoriesCallback callback) {
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
    private ResponseClones getRepositoryClonesSync(final Repository repository, String period) {
        try {
            ResponseAccessToken accessToken = new ResponseAccessToken();
            accessToken.setAccessToken(getToken());
            accessToken.setTokenType(getTokenType());

            GithubService loginService = GithubServiceGenerator.createService(
                    GithubService.class, accessToken, GithubServiceGenerator.API_HTTPS_BASE_URL,
                    "application/vnd.github.spiderman-preview+json");
            Call<ResponseClones> call = loginService.getRepositoryClones(
                    repository.getOwner().getLogin(), repository.getName(), period);

            Response<ResponseClones> response = call.execute();
            if (response.isSuccessful()) {
                ResponseClones responseClones = response.body();
                if (responseClones != null && responseClones.getClones() != null) {
                    GithubLocalDataSource localDataSource =
                            GithubLocalDataSource.getInstance(mContentResolver, mPreferences);
                    localDataSource.saveClones(repository.getId(), responseClones);
                    return responseClones;
                } else {
                    throw new IOException("responseClones not specified");
                }
            } else {
                APIError error = APIError.parseError(response);
                throw new IOException(error.getMessage());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            return null;
        }
    }

    @Override
    public void getRepositoryClones(final Repository repository, final String period,
                                    final GetRepositoryClonesCallback callback) {
        new AsyncTask<Void, Void, ResponseClones>() {
            @Override
            protected ResponseClones doInBackground(Void... params) {
                return getRepositoryClonesSync(repository, period);
            }

            @Override
            protected void onPostExecute(ResponseClones responseClones) {
                if (responseClones != null) {
                    callback.onRepositoryClonesLoaded(responseClones);
                } else {
                    callback.onDataNotAvailable();
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @WorkerThread
    private ResponseViews getRepositoryViewsSync(final Repository repository, String period) {
        try {
            ResponseAccessToken accessToken = new ResponseAccessToken();
            accessToken.setAccessToken(getToken());
            accessToken.setTokenType(getTokenType());

            GithubService loginService = GithubServiceGenerator.createService(
                    GithubService.class, accessToken, GithubServiceGenerator.API_HTTPS_BASE_URL,
                    "application/vnd.github.spiderman-preview+json");
            Call<ResponseViews> call = loginService.getRepositoryViews(
                    repository.getOwner().getLogin(), repository.getName(), period);

            Response<ResponseViews> response = call.execute();
            if (response.isSuccessful()) {
                ResponseViews responseViews = response.body();
                if (responseViews != null && responseViews.getViews() != null) {
                    GithubLocalDataSource localDataSource =
                            GithubLocalDataSource.getInstance(mContentResolver, mPreferences);
                    localDataSource.saveViews(repository.getId(), responseViews);
                    return responseViews;
                } else {
                    throw new IOException("responseViews not specified");
                }
            } else {
                APIError error = APIError.parseError(response);
                throw new IOException(error.getMessage());
            }

        } catch (IOException e) {
            FirebaseCrash.report(e);
            return null;
        }
    }

    @Override
    public void getRepositoryViews(final Repository repository, final String period, final GetRepositoryViewsCallback callback) {
        new AsyncTask<Void, Void, ResponseViews>() {
            @Override
            protected ResponseViews doInBackground(Void... params) {
                return getRepositoryViewsSync(repository, period);
            }

            @Override
            protected void onPostExecute(ResponseViews responseViews) {
                if (responseViews != null) {
                    callback.onRepositoryViewsLoaded(responseViews);
                } else {
                    callback.onDataNotAvailable();
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @SuppressWarnings("unused")
    @WorkerThread
    public List<ResponseTrending> getTrendingRepositoriesSync(final String period, final String language,
                                                              boolean useCache) {
        try {
            ResponseAccessToken accessToken = new ResponseAccessToken();
            accessToken.setAccessToken(getToken());
            accessToken.setTokenType(getTokenType());

            GithubService githubService = ThirdPartyGithubServiceGenerator.createService(
                    GithubService.class);
            Call<List<ResponseTrending>> call = githubService.getTrendingRepositories(language,
                    period);

            List<ResponseTrending> responseTrendingList = call.execute().body();

            if (responseTrendingList != null) {
                GithubLocalDataSource localDataSource =
                        GithubLocalDataSource.getInstance(mContentResolver, mPreferences);
                localDataSource.saveTrendingRepositories(period, language, responseTrendingList);

                return responseTrendingList;
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
        new AsyncTask<Void, Void, List<ResponseTrending>>() {
            @Override
            protected List<ResponseTrending> doInBackground(Void... params) {
                return getTrendingRepositoriesSync(period, language, useCache);
            }

            @Override
            protected void onPostExecute(List<ResponseTrending> responseTrendingList) {
                if (responseTrendingList != null) {
                    callback.onTrendingRepositoriesLoaded(responseTrendingList, language, period);
                } else {
                    callback.onDataNotAvailable();
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @SuppressWarnings("unused")
    @WorkerThread
    private List<ResponseReferrer> getRepositoryReferrersSync(final Repository repository) {
        GithubLocalDataSource localDataSource =
                GithubLocalDataSource.getInstance(mContentResolver, mPreferences);

        try {
            ResponseAccessToken accessToken = new ResponseAccessToken();
            accessToken.setAccessToken(getToken());
            accessToken.setTokenType(getTokenType());

            GithubService loginService = GithubServiceGenerator.createService(
                    GithubService.class, accessToken, GithubServiceGenerator.API_HTTPS_BASE_URL,
                    "application/vnd.github.spiderman-preview+json");

            Call<List<ResponseReferrer>> call = loginService.getTopReferrers(
                    repository.getOwner().getLogin(), repository.getName());

            Response<List<ResponseReferrer>> response = call.execute();

            if (response.isSuccessful()) {
                List<ResponseReferrer> responseReferrerList = response.body();
                if (responseReferrerList != null) {
                    localDataSource.saveReferrers(repository.getId(), responseReferrerList);
                    return responseReferrerList;
                } else {
                    throw new IOException("responseReferrerList is null");
                }
            } else {
                APIError error = APIError.parseError(response);
                throw new IOException(error.getMessage());
            }
        } catch (IOException e) {
            FirebaseCrash.report(e);
            return null;
        }
    }

    @Override
    public void getRepositoryReferrers(final Repository repository,
                                       final GetRepositoryReferrersCallback callback) {
        new AsyncTask<Void, Void, List<ResponseReferrer>>() {
            @Override
            protected List<ResponseReferrer> doInBackground(Void... params) {
                return getRepositoryReferrersSync(repository);
            }

            @Override
            protected void onPostExecute(List<ResponseReferrer> responseReferrerList) {
                if (responseReferrerList != null) {
                    callback.onRepositoryReferrersLoaded(responseReferrerList);
                } else {
                    callback.onDataNotAvailable();
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void requestTokenFromCode(final String code, final RequestTokenFromCodeCallback callback) {
        new AsyncTask<Void, Void, ResponseAccessToken>() {
            @Override
            protected ResponseAccessToken doInBackground(Void... params) {
                try {
                    GithubService loginService = GithubServiceGenerator.createService(
                            GithubService.class);
                    Call<ResponseAccessToken> call = loginService.getAccessToken(code,
                            GithubService.clientId, GithubService.clientSecret);
                    return call.execute().body();
                } catch (IOException e) {
                    Log.e(LOG_TAG, e.getMessage(), e);
                }
                return null;
            }

            @Override
            protected void onPostExecute(ResponseAccessToken accessToken) {
                if (accessToken != null && accessToken.getAccessToken() != null
                        && !accessToken.getAccessToken().isEmpty()) {
                    callback.onTokenLoaded(accessToken.getAccessToken(), accessToken.getTokenType());
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
        return GithubLocalDataSource.getInstance(mContentResolver, mPreferences).getToken();
    }

    @Override
    public String getTokenType() {
        return GithubLocalDataSource.getInstance(mContentResolver, mPreferences).getTokenType();
    }


    @Override
    public String getDefaultLanguageForTrending() {
        return GithubLocalDataSource.getInstance(mContentResolver, mPreferences)
                .getDefaultLanguageForTrending();
    }

    @Override
    public void setDefaultLanguageForTrending(@GithubLocalDataSource.TrendingLanguage String language) {
        GithubLocalDataSource.getInstance(mContentResolver, mPreferences)
                .setDefaultLanguageForTrending(language);
    }

    @Override
    public String getDefaultPeriodForTrending() {
        return GithubLocalDataSource.getInstance(mContentResolver, mPreferences)
                .getDefaultPeriodForTrending();
    }

    @Override
    public void setDefaultPeriodForTrending(@GithubLocalDataSource.TrendingPeriod String period) {
        GithubLocalDataSource.getInstance(mContentResolver, mPreferences)
                .setDefaultPeriodForTrending(period);
    }

    @Override
    public void getUser(final GerUserCallback callback) {
        new AsyncTask<Void, Void, ResponseUser>() {
            @Override
            protected ResponseUser doInBackground(Void... params) {
                try {
                    UserService service = new UserService();
                    service.getClient().setOAuth2Token(getToken());
                    User user = service.getUser();

                    ResponseUser responseUser = new ResponseUser();
                    responseUser.setName(user.getName());
                    responseUser.setLogin(user.getLogin());
                    responseUser.setAvatarUrl(user.getAvatarUrl());
                    responseUser.setFollowers(String.valueOf(user.getFollowers()));
                    return responseUser;
                } catch (IOException e) {
                    FirebaseCrash.report(e);
                }
                return null;
            }

            @Override
            protected void onPostExecute(ResponseUser user) {
                if (user != null) {
                    callback.onUserLoaded(user);
                } else {
                    callback.onDataNotAvailable();
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void getStargazers(final Repository repository, final GetStargazersCallback callback) {
        new AsyncTask<Void, Void, List<ResponseStargazers>>() {
            @Override
            protected List<ResponseStargazers> doInBackground(Void... params) {
                return getStargazersSync(repository, "last");
            }

            @Override
            protected void onPostExecute(List<ResponseStargazers> responseStargazersList) {
                if (responseStargazersList != null) {
                    callback.onStargazersLoaded(responseStargazersList);
                } else {
                    callback.onDataNotAvailable();
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private List<ResponseStargazers> getStargazersSync(Repository repository, String page) {
        try {
            ResponseAccessToken accessToken = new ResponseAccessToken();
            accessToken.setAccessToken(getToken());
            accessToken.setTokenType(getTokenType());

            GithubService githubService = GithubServiceGenerator.createService(
                    GithubService.class, accessToken, GithubServiceGenerator.API_HTTPS_BASE_URL,
                    "application/vnd.github.v3.star+json");
            Call<List<ResponseStargazers>> call = githubService.getStargazers(repository
                    .getOwner().getLogin(), repository.getName(), page);


            Response<List<ResponseStargazers>> response = call.execute();

            if (response.isSuccessful()) {

                String headerLink = response.headers().get("Link");
                if (headerLink != null) {
                    boolean containsLast = headerLink.contains("last");
                    headerLink = headerLink.replace(
                            headerLink.substring(headerLink.lastIndexOf(">")), "");
                    String lastPage = headerLink = headerLink.replace(
                            headerLink.substring(0, headerLink.lastIndexOf("=") + 1), "");

                    if (headerLink != null && (containsLast && !lastPage.equals(page))) {
                        getStargazersSync(repository, lastPage);
                    }
                }

                List<ResponseStargazers> responseStargazersList = response.body();

                if (responseStargazersList != null) {
                    GithubLocalDataSource localDataSource =
                            GithubLocalDataSource.getInstance(mContentResolver, mPreferences);
                    localDataSource.saveStargazers(repository, responseStargazersList);
                    return responseStargazersList;

                } else {
                    throw new IOException("responseStargazersList is null");
                }

            } else {
                APIError error = APIError.parseError(response);
                throw new IOException(error.getMessage());
            }
        } catch (IOException e) {
            FirebaseCrash.report(e);
            return null;
        }
    }
}
