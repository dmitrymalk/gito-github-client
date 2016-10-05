package com.dmitrymalkovich.android.githubanalytics.data.source;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.IOException;

import static com.google.common.base.Preconditions.checkNotNull;

public class GithubRepository implements GithubDataSource {
    public static final String LOG_TAG = GithubRepository.class.getSimpleName();
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
    public static GithubRepository getInstance(GithubDataSource githubRemoteDataSource,
                                              GithubDataSource githubLocalDataSource) {
        if (INSTANCE == null) {
            INSTANCE = new GithubRepository(githubRemoteDataSource, githubLocalDataSource);
        }
        return INSTANCE;
    }

    @Override
    public void login(final String username, final String password) {
        new AsyncTask<Void, Void, Void>()
        {
            @Override
            protected Void doInBackground(Void... params) {
                mGithubRemoteDataSource.login(username, password);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                getRepositories();
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void getRepositories() {
        new AsyncTask<Void, Void, Void>()
        {
            @Override
            protected Void doInBackground(Void... params) {
                mGithubRemoteDataSource.getRepositories();
                return null;
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void requestTokenFromCode(final String code) throws IOException {
        new AsyncTask<Void, Void, Void>()
        {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    mGithubRemoteDataSource.requestTokenFromCode(code);
                }
                catch (IOException e)
                {
                    Log.e(LOG_TAG, e.getMessage(), e);
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                getRepositories();
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }
}
