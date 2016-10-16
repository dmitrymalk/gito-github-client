package com.dmitrymalkovich.android.githubanalytics.data.source.remote;

import android.content.ContentResolver;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.annotation.WorkerThread;
import android.util.Log;

import com.dmitrymalkovich.android.githubanalytics.data.source.GithubDataSource;
import com.dmitrymalkovich.android.githubanalytics.data.source.local.GithubLocalDataSource;
import com.dmitrymalkovich.android.githubanalytics.data.source.remote.gson.ResponseTrendingMultipleLanguages;
import com.dmitrymalkovich.android.githubanalytics.data.source.remote.gson.ResponseAccessToken;
import com.dmitrymalkovich.android.githubanalytics.data.source.remote.gson.ResponseClones;
import com.dmitrymalkovich.android.githubanalytics.data.source.remote.gson.ResponseReferrer;
import com.dmitrymalkovich.android.githubanalytics.data.source.remote.gson.ResponseTrending;
import com.dmitrymalkovich.android.githubanalytics.data.source.remote.gson.ResponseUser;
import com.dmitrymalkovich.android.githubanalytics.data.source.remote.gson.ResponseViews;

import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.User;
import org.eclipse.egit.github.core.service.RepositoryService;
import org.eclipse.egit.github.core.service.UserService;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;

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
            Log.e(LOG_TAG, e.getMessage(), e);
        }
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
            Log.e(LOG_TAG, e.getMessage(), e);
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
    public ResponseClones getRepositoryClonesSync(final Repository repository) {
        try {
            ResponseAccessToken accessToken = new ResponseAccessToken();
            accessToken.setAccessToken(getToken());
            accessToken.setTokenType(getTokenType());

            GithubService loginService = GithubServiceGenerator.createService(
                    GithubService.class, accessToken);
            Call<ResponseClones> call = loginService.getRepositoryClones(
                    repository.getOwner().getLogin(), repository.getName(), "week");

            ResponseClones responseClones = call.execute().body();

            if (responseClones != null && responseClones.getClones() != null) {
                GithubLocalDataSource localDataSource =
                        GithubLocalDataSource.getInstance(mContentResolver, mPreferences);
                localDataSource.saveClones(repository.getId(), responseClones);
            }

            return responseClones;
        } catch (IOException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            return null;
        }
    }

    @Override
    public void getRepositoryClones(final Repository repository, final GetRepositoryClonesCallback callback) {
        new AsyncTask<Void, Void, ResponseClones>() {
            @Override
            protected ResponseClones doInBackground(Void... params) {
                return getRepositoryClonesSync(repository);
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
    public ResponseViews getRepositoryViewsSync(final Repository repository) {
        try {
            ResponseAccessToken accessToken = new ResponseAccessToken();
            accessToken.setAccessToken(getToken());
            accessToken.setTokenType(getTokenType());

            GithubService loginService = GithubServiceGenerator.createService(
                    GithubService.class, accessToken);
            Call<ResponseViews> call = loginService.getRepositoryViews(
                    repository.getOwner().getLogin(), repository.getName(), "week");

            ResponseViews responseViews = call.execute().body();

            if (responseViews != null && responseViews.getViews() != null) {
                GithubLocalDataSource localDataSource =
                        GithubLocalDataSource.getInstance(mContentResolver, mPreferences);
                localDataSource.saveViews(repository.getId(), responseViews);
            }

            return responseViews;
        } catch (IOException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            return null;
        }
    }

    @Override
    public void getRepositoryViews(final Repository repository, final GetRepositoryViewsCallback callback) {
        new AsyncTask<Void, Void, ResponseViews>() {
            @Override
            protected ResponseViews doInBackground(Void... params) {
                return getRepositoryViewsSync(repository);
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

    @WorkerThread
    public List<ResponseTrending> getTrendingRepositoriesSync(final String period, final String language) {
        try {
            ResponseAccessToken accessToken = new ResponseAccessToken();
            accessToken.setAccessToken(getToken());
            accessToken.setTokenType(getTokenType());

            GithubService githubService = GithubServiceGenerator.createThirdPartyService(
                    GithubService.class);
            Call<ResponseTrendingMultipleLanguages> call = githubService.getTrendingRepositories(language);

            ResponseTrendingMultipleLanguages responseRepositorySearch = call.execute().body();

            if (responseRepositorySearch != null) {
                GithubLocalDataSource localDataSource =
                        GithubLocalDataSource.getInstance(mContentResolver, mPreferences);
                localDataSource.saveTrendingRepositories(period, language, responseRepositorySearch.getJava());
                return responseRepositorySearch.getJava();
            } else {
                return null;
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            return null;
        }
    }

    @Override
    public void getTrendingRepositories(final String period, final String language, final GetTrendingRepositories callback) {
        new AsyncTask<Void, Void, List<ResponseTrending>>() {
            @Override
            protected List<ResponseTrending> doInBackground(Void... params) {
                return getTrendingRepositoriesSync(period, language);
            }

            @Override
            protected void onPostExecute(List<ResponseTrending> responseTrendingList) {
                if (responseTrendingList != null) {
                    callback.onTrendingRepositoriesLoaded(responseTrendingList);
                } else {
                    callback.onDataNotAvailable();
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @WorkerThread
    public List<ResponseReferrer> getRepositoryReferrersSync(final Repository repository) {
        try {
            ResponseAccessToken accessToken = new ResponseAccessToken();
            accessToken.setAccessToken(getToken());
            accessToken.setTokenType(getTokenType());

            GithubService loginService = GithubServiceGenerator.createService(
                    GithubService.class, accessToken);
            Call<List<ResponseReferrer>> call = loginService.getTopReferrers(
                    repository.getOwner().getLogin(), repository.getName());
            List<ResponseReferrer> responseReferrerList = call.execute().body();
            GithubLocalDataSource localDataSource =
                    GithubLocalDataSource.getInstance(mContentResolver, mPreferences);
            localDataSource.saveReferrers(repository.getId(), responseReferrerList);
            return responseReferrerList;
        } catch (IOException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
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
                    return responseUser;
                } catch (IOException e) {
                    Log.e(LOG_TAG, e.getMessage(), e);
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
}
