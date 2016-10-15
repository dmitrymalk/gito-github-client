package com.dmitrymalkovich.android.githubanalytics.data.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.dmitrymalkovich.android.githubanalytics.R;
import com.dmitrymalkovich.android.githubanalytics.data.source.GithubDataSource;
import com.dmitrymalkovich.android.githubanalytics.data.source.GithubRepository;
import com.dmitrymalkovich.android.githubanalytics.data.source.Injection;
import com.dmitrymalkovich.android.githubanalytics.data.source.remote.gson.ResponseClones;
import com.dmitrymalkovich.android.githubanalytics.data.source.remote.gson.ResponseReferrer;
import com.dmitrymalkovich.android.githubanalytics.data.source.remote.gson.ResponseViews;

import org.eclipse.egit.github.core.Repository;

import java.util.List;

/**
 * Handle the transfer of data between a server and an
 * app, using the Android sync adapter framework.
 * <p>
 * https://developer.android.com/training/sync-adapters/creating-sync-adapter.html
 */
public class SyncAdapter extends AbstractThreadedSyncAdapter {

    private static String LOG_TAG = SyncAdapter.class.getSimpleName();
    private static final int SECONDS_PER_MINUTE = 60;
    private static final int SYNC_INTERVAL_IN_MINUTES = 15;
    private static final int SYNC_INTERVAL =
            SYNC_INTERVAL_IN_MINUTES *
                    SECONDS_PER_MINUTE;
    private static final int SYNC_FLEXTIME = SYNC_INTERVAL / 3;
    private static Account mAccount;
    @SuppressWarnings("all")
    ContentResolver mContentResolver;

    /**
     * Set up the sync adapter
     */
    SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        mContentResolver = context.getContentResolver();
    }

    /**
     * Set up the sync adapter. This form of the
     * constructor maintains compatibility with Android 3.0
     * and later platform versions
     */
    @SuppressWarnings("unused")
    public SyncAdapter(
            Context context,
            boolean autoInitialize,
            boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
        mContentResolver = context.getContentResolver();
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority,
                              ContentProviderClient provider, SyncResult syncResult) {
        Log.d(LOG_TAG, "onPerformSync");

        final GithubRepository githubRepository = Injection.provideGithubRepository(getContext());

        // Get user's repositories and save to db
        githubRepository.getRepositories(new GithubDataSource.GetRepositoriesCallback() {
            @Override
            public void onRepositoriesLoaded(List<Repository> repositoryList) {
                for (Repository repository : repositoryList) {

                    // Get repository top referrers and save to db
                    githubRepository.getRepositoryReferrers(repository,
                            new GithubDataSource.GetRepositoryReferrersCallback() {
                        @Override
                        public void onRepositoryReferrersLoaded(
                                List<ResponseReferrer> responseReferrerList) {
                        }

                        @Override
                        public void onDataNotAvailable() {

                        }
                    });

                    // Get repository visitors and save to db
                    githubRepository.getRepositoryViews(repository, new GithubDataSource.GetRepositoryViewsCallback() {
                        @Override
                        public void onRepositoryViewsLoaded(ResponseViews responseViews) {

                        }

                        @Override
                        public void onDataNotAvailable() {

                        }
                    });

                    // Get repository clones and save to db
                    githubRepository.getRepositoryClones(repository, new GithubDataSource.GetRepositoryClonesCallback() {
                        @Override
                        public void onRepositoryClonesLoaded(ResponseClones responseClones) {

                        }

                        @Override
                        public void onDataNotAvailable() {

                        }
                    });
                }
            }

            @Override
            public void onDataNotAvailable() {
            }
        });
    }

    public static void initializeSyncAdapter(Context context) {
        Log.d(LOG_TAG, "initializeSyncAdapter");
        getSyncAccount(context);
    }

    private static void onAccountCreated(Account newAccount, Context context) {
        Log.d(LOG_TAG, "onAccountCreated");
        configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);
        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.sync_authority), true);
        syncImmediately(context);
    }

    /**
     * Helper method to have the sync adapter sync immediately
     *
     * @param context The context used to access the account service
     */
    private static void syncImmediately(Context context) {
        Log.d(LOG_TAG, "syncImmediately");
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(mAccount,
                context.getString(R.string.sync_authority), bundle);
    }

    /**
     * Helper method to schedule the sync adapter periodic execution
     */
    private static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Log.d(LOG_TAG, "configurePeriodicSync");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(mAccount, context.getString(R.string.sync_authority)).
                    setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(mAccount,
                    context.getString(R.string.sync_authority), new Bundle(), syncInterval);
        }
    }

    private static Account getSyncAccount(Context context) {
        Log.d(LOG_TAG, "getSyncAccount");
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);
        mAccount = new Account(
                context.getString(R.string.app_name),
                context.getString(R.string.sync_account_type));
        accountManager.addAccountExplicitly(mAccount, "", null);
        onAccountCreated(mAccount, context);
        return mAccount;
    }
}
