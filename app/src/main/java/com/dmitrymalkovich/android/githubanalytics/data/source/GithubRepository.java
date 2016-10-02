package com.dmitrymalkovich.android.githubanalytics.data.source;

import android.support.annotation.NonNull;

import static com.google.common.base.Preconditions.checkNotNull;

public class GithubRepository implements GithubDataSource {

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

}
