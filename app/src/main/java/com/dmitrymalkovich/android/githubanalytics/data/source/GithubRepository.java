package com.dmitrymalkovich.android.githubanalytics.data.source;

import android.database.Cursor;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

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
            public void onRepositoriesLoaded() {
                callback.onRepositoriesLoaded();
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
            public void onTokenLoaded(String token) {
                mGithubLocalDataSource.saveToken(token);
                callback.onTokenLoaded(token);
            }

            @Override
            public void onDataNotAvailable() {
                callback.onDataNotAvailable();
            }
        });
    }

    @Override
    public void saveToken(String token) {
        mGithubLocalDataSource.saveToken(token);
    }

    @Override
    public String getToken() {
        String token = mGithubLocalDataSource.getToken();
        return token != null && !token.isEmpty() ? token : null;
    }

    public interface LoadDataCallback {
        void onDataLoaded(Cursor data);

        void onDataEmpty();

        void onDataNotAvailable();

        void onDataReset();
    }
}
