package com.dmitrymalkovich.android.githubanalytics.data.source.remote;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;

import com.dmitrymalkovich.android.githubanalytics.data.source.GithubDataSource;
import com.dmitrymalkovich.android.githubanalytics.data.source.local.GithubLocalDataSource;
import com.dmitrymalkovich.android.githubanalytics.data.source.local.contract.RepositoryContract;
import com.dmitrymalkovich.android.githubanalytics.data.source.local.contract.ClonesContract;
import com.dmitrymalkovich.android.githubanalytics.data.source.local.contract.ReferrerContract;
import com.dmitrymalkovich.android.githubanalytics.data.source.local.contract.ViewsContract;
import com.dmitrymalkovich.android.githubanalytics.data.source.remote.gson.ResponseAccessToken;
import com.dmitrymalkovich.android.githubanalytics.data.source.remote.gson.ResponseClones;
import com.dmitrymalkovich.android.githubanalytics.data.source.remote.gson.ResponseReferrer;
import com.dmitrymalkovich.android.githubanalytics.data.source.remote.gson.ResponseViews;

import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.service.RepositoryService;

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

    @Override
    public void getRepositories(final GetRepositoriesCallback callback) {
        new AsyncTask<Void, Void, List<Repository>>()
        {
            @Override
            protected List<Repository> doInBackground(Void... params) {
                try {
                    String token = getToken();
                    if (token != null)
                    {
                        RepositoryService service = new RepositoryService();
                        service.getClient().setOAuth2Token(token);
                        List<Repository> repositoryList = service.getRepositories();
                        saveRepositories(repositoryList);
                        return repositoryList;
                    }
                    else
                    {
                        return null;
                    }
                }
                catch (IOException e)
                {
                    Log.e(LOG_TAG, e.getMessage(), e);
                    return null;
                }
            }

            @Override
            protected void onPostExecute(List<Repository> repositoryList) {
                if (repositoryList != null) {
                    callback.onRepositoriesLoaded(repositoryList);
                }
                else {
                    callback.onDataNotAvailable();
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void getRepositoryClones(final Repository repository, final GetRepositoryClonesCallback callback) {
        new AsyncTask<Void, Void, ResponseClones>()
        {
            @Override
            protected ResponseClones doInBackground(Void... params) {
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
                        Log.d("!!", "1!!");
                        saveClones(repository.getId(), responseClones);
                    }

                    return responseClones;
                }
                catch (IOException e)
                {
                    Log.e(LOG_TAG, e.getMessage(), e);
                    return null;
                }
            }

            @Override
            protected void onPostExecute(ResponseClones responseClones) {
                if (responseClones != null) {
                    callback.onRepositoryClonesLoaded(responseClones);
                }
                else {
                    callback.onDataNotAvailable();
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void getRepositoryViews(final Repository repository, final GetRepositoryViewsCallback callback) {
        new AsyncTask<Void, Void, ResponseViews>()
        {
            @Override
            protected ResponseViews doInBackground(Void... params) {
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
                        Log.d("!!", "2!!");
                        saveViews(repository.getId(), responseViews);
                    }

                    return responseViews;
                }
                catch (IOException e)
                {
                    Log.e(LOG_TAG, e.getMessage(), e);
                    return null;
                }
            }

            @Override
            protected void onPostExecute(ResponseViews responseViews) {
                if (responseViews != null) {
                    callback.onRepositoryViewsLoaded(responseViews);
                }
                else {
                    callback.onDataNotAvailable();
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void getRepositoryReferrers(final Repository repository,
                                       final GetRepositoryReferrersCallback callback) {
        new AsyncTask<Void, Void, List<ResponseReferrer>>()
        {
            @Override
            protected List<ResponseReferrer> doInBackground(Void... params) {
                try {
                    ResponseAccessToken accessToken = new ResponseAccessToken();
                    accessToken.setAccessToken(getToken());
                    accessToken.setTokenType(getTokenType());

                    GithubService loginService = GithubServiceGenerator.createService(
                            GithubService.class, accessToken);
                    Call<List<ResponseReferrer>> call = loginService.getTopReferrers(
                            repository.getOwner().getLogin(), repository.getName());
                    List<ResponseReferrer> responseReferrerList = call.execute().body();
                    saveReferrers(repository.getId(), responseReferrerList);
                    return responseReferrerList;
                }
                catch (IOException e)
                {
                    Log.e(LOG_TAG, e.getMessage(), e);
                    return null;
                }
            }

            @Override
            protected void onPostExecute(List<ResponseReferrer> responseReferrerList) {
                if (responseReferrerList != null) {
                    callback.onRepositoryReferrersLoaded(responseReferrerList);
                }
                else {
                    callback.onDataNotAvailable();
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void requestTokenFromCode(final String code, final RequestTokenFromCodeCallback callback) {
        new AsyncTask<Void, Void, ResponseAccessToken>()
        {
            @Override
            protected ResponseAccessToken doInBackground(Void... params) {
                try {
                    GithubService loginService = GithubServiceGenerator.createService(
                            GithubService.class);
                    Call<ResponseAccessToken> call = loginService.getAccessToken(code,
                            GithubService.clientId, GithubService.clientSecret);
                    return call.execute().body();
                }
                catch (IOException e)
                {
                    Log.e(LOG_TAG, e.getMessage(), e);
                }
                return null;
            }

            @Override
            protected void onPostExecute(ResponseAccessToken accessToken) {
                if (accessToken != null && accessToken.getAccessToken() != null
                        && !accessToken.getAccessToken().isEmpty()) {
                    callback.onTokenLoaded(accessToken.getAccessToken(), accessToken.getTokenType());
                }
                else {
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

    private void saveReferrers(long repositoryId, List<ResponseReferrer> referrerList) {
        mContentResolver.delete(ReferrerContract.ReferrerEntry.CONTENT_URI,
                ReferrerContract.ReferrerEntry.COLUMN_REPOSITORY_KEY + " =  ? ",
                new String[]{String.valueOf(repositoryId)});

        for (ResponseReferrer referrer : referrerList) {
            ContentValues referrerValues = ReferrerContract.ReferrerEntry
                    .createContentValues(repositoryId, referrer);

                mContentResolver.insert(
                        ReferrerContract.ReferrerEntry.CONTENT_URI,
                        referrerValues);
        }
    }

    private boolean saveRepositories(List<Repository> repositoryList) throws IOException {
        for (Repository repository : repositoryList) {
            ContentValues repositoryValues = RepositoryContract.RepositoryEntry.
                    buildContentValues(repository);

            Cursor cursor = mContentResolver.query(RepositoryContract.RepositoryEntry.CONTENT_URI,
                    new String[]{RepositoryContract.RepositoryEntry.COLUMN_REPOSITORY_ID},
                    RepositoryContract.RepositoryEntry.COLUMN_REPOSITORY_ID + " = " + repository.getId(),
                    null,
                    null);

            if (cursor != null && cursor.moveToFirst()) {
                mContentResolver.update(
                        RepositoryContract.RepositoryEntry.CONTENT_URI,
                        repositoryValues,
                        RepositoryContract.RepositoryEntry.COLUMN_REPOSITORY_ID + " = " + repository.getId(),
                        null);
            } else {
                mContentResolver.insert(
                        RepositoryContract.RepositoryEntry.CONTENT_URI,
                        repositoryValues);
            }

            if (cursor != null) {
                cursor.close();
            }
        }
        return true;
    }

    private void saveClones(long repositoryId, ResponseClones responseClones) {
        for (ResponseClones.Clone clone : responseClones.getClones()) {
            ContentValues contentValues = ClonesContract.ClonesEntry
                    .buildContentValues(repositoryId, clone);

            String selection = ClonesContract.ClonesEntry.COLUMN_REPOSITORY_KEY + " = "
                    + repositoryId + " AND "
                    + ClonesContract.ClonesEntry.COLUMN_CLONES_TIMESTAMP
                    + " = " + clone.getTimestamp();

            Cursor cursor = mContentResolver.query(ClonesContract.ClonesEntry.CONTENT_URI,
                    new String[]{ClonesContract.ClonesEntry.COLUMN_REPOSITORY_KEY},
                    selection,
                    null,
                    null);

            if (cursor != null && cursor.moveToFirst()) {
                mContentResolver.update(
                        ClonesContract.ClonesEntry.CONTENT_URI,
                        contentValues,
                        selection,
                        null);
            } else {
                mContentResolver.insert(
                        ClonesContract.ClonesEntry.CONTENT_URI,
                        contentValues);
            }

            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private void saveViews(long repositoryId, ResponseViews responseViews) {
        for (ResponseViews.View view : responseViews.getViews()) {
            ContentValues contentValues = ViewsContract.ViewsEntry
                    .buildContentValues(repositoryId, view);

            String selection = ViewsContract.ViewsEntry.COLUMN_REPOSITORY_KEY + " = "
                    + repositoryId + " AND "
                    + ViewsContract.ViewsEntry.COLUMN_VIEWS_TIMESTAMP
                    + " = " + view.getTimestamp();

            Cursor cursor = mContentResolver.query(ViewsContract.ViewsEntry.CONTENT_URI,
                    new String[]{ViewsContract.ViewsEntry.COLUMN_REPOSITORY_KEY},
                    selection,
                    null,
                    null);

            if (cursor != null && cursor.moveToFirst()) {
                mContentResolver.update(
                        ViewsContract.ViewsEntry.CONTENT_URI,
                        contentValues,
                        selection,
                        null);
            } else {
                mContentResolver.insert(
                        ViewsContract.ViewsEntry.CONTENT_URI,
                        contentValues);
            }

            if (cursor != null) {
                cursor.close();
            }
        }
    }
}
