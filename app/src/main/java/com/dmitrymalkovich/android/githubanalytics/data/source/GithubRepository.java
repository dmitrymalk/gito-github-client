package com.dmitrymalkovich.android.githubanalytics.data.source;

import android.database.Cursor;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.dmitrymalkovich.android.githubanalytics.data.source.remote.gson.ResponseClones;
import com.dmitrymalkovich.android.githubanalytics.data.source.remote.gson.ResponseReferrer;
import com.dmitrymalkovich.android.githubanalytics.data.source.remote.gson.ResponseTrending;
import com.dmitrymalkovich.android.githubanalytics.data.source.remote.gson.ResponseUser;
import com.dmitrymalkovich.android.githubanalytics.data.source.remote.gson.ResponseViews;

import org.eclipse.egit.github.core.Repository;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class GithubRepository implements GithubDataSource {
    @SuppressWarnings("unused")
    private static final String LOG_TAG = GithubRepository.class.getSimpleName();
    private static GithubRepository INSTANCE = null;

    private final GithubDataSource mGithubRemoteDataSource;
    private final GithubDataSource mGithubLocalDataSource;

    private GithubRepository(@NonNull GithubDataSource githubRemoteDataSource,
                             @NonNull GithubDataSource githubLocalDataSource) {
        mGithubRemoteDataSource = checkNotNull(githubRemoteDataSource);
        mGithubLocalDataSource = checkNotNull(githubLocalDataSource);
    }

    /**
     * Returns the single instance of this class, creating it if necessary.
     *
     * @param githubRemoteDataSource the backend data source
     * @param githubLocalDataSource  the device storage data source
     * @return the {@link GithubRepository} instance
     */
    static GithubRepository getInstance(GithubDataSource githubRemoteDataSource,
                                        GithubDataSource githubLocalDataSource) {
        if (INSTANCE == null) {
            INSTANCE = new GithubRepository(githubRemoteDataSource, githubLocalDataSource);
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
    public void getRepositoryClones(Repository repository, final GetRepositoryClonesCallback callback) {
        mGithubRemoteDataSource.getRepositoryClones(repository, new GetRepositoryClonesCallback() {
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
    public void getRepositoryViews(Repository repository, final GetRepositoryViewsCallback callback) {
        mGithubRemoteDataSource.getRepositoryViews(repository, new GetRepositoryViewsCallback() {
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
    public void getTrendingRepositories(String period, String language,
                                        final GetTrendingRepositories callback) {
        mGithubRemoteDataSource.getTrendingRepositories(period, language, new GetTrendingRepositories() {
            @Override
            public void onTrendingRepositoriesLoaded(List<ResponseTrending> responseTrendingList) {
                callback.onTrendingRepositoriesLoaded(responseTrendingList);
            }

            @Override
            public void onDataNotAvailable() {
                callback.onDataNotAvailable();
            }
        });
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

    public interface LoadDataCallback {
        void onDataLoaded(Cursor data);

        void onDataEmpty();

        void onDataNotAvailable();

        void onDataReset();
    }
}
