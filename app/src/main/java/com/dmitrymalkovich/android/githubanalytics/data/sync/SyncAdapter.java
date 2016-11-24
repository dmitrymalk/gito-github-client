package com.dmitrymalkovich.android.githubanalytics.data.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;

import com.dmitrymalkovich.android.githubanalytics.R;
import com.dmitrymalkovich.android.githubanalytics.data.source.GithubRepository;
import com.dmitrymalkovich.android.githubanalytics.data.source.local.contract.RepositoryContract;
import com.dmitrymalkovich.android.githubanalytics.data.source.remote.GithubRemoteDataSource;

import org.eclipse.egit.github.core.Repository;

import java.util.List;

/**
 * Handle the transfer of data between a server and an
 * app, using the Android sync adapter framework.
 * <p>
 * https://developer.android.com/training/sync-adapters/creating-sync-adapter.html
 */
public class SyncAdapter extends AbstractThreadedSyncAdapter {

    @SuppressWarnings("unused")
    private static String LOG_TAG = SyncAdapter.class.getSimpleName();
    private static final int SECONDS_PER_MINUTE = 60;
    private static final int SYNC_INTERVAL_IN_MINUTES = 60;
    private static final int SYNC_INTERVAL = SYNC_INTERVAL_IN_MINUTES * SECONDS_PER_MINUTE;
    private static final int SYNC_FLEXTIME = SYNC_INTERVAL / 3;
    private static Account mAccount;
    private SyncSettings mSyncSettings;

    /**
     * Set up the sync adapter
     */
    SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        SharedPreferences sharedPreferences =
                context.getSharedPreferences(GithubRepository.PREFERENCES,
                        Context.MODE_PRIVATE);
        mSyncSettings = new SyncSettings(sharedPreferences);
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
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority,
                              ContentProviderClient provider, SyncResult syncResult) {
        String key = "onPerformSync";
        final GithubRemoteDataSource githubRepository =
                GithubRepository.Injection.provideRemoteDataSource(getContext());
        githubRepository.getUserSync();
        Cursor cursor = getContext().getContentResolver().query(RepositoryContract
                .RepositoryEntry.CONTENT_URI_REPOSITORY_STARGAZERS,
                RepositoryContract.RepositoryEntry.REPOSITORY_COLUMNS_WITH_ADDITIONAL_INFO,
                null, null, null);
        boolean forceSync = cursor == null || !cursor.moveToFirst();
        if (cursor != null) {
            cursor.close();
        }

        if (mSyncSettings.isSynced(key) && !forceSync) {
            return;
        } else {
            mSyncSettings.synced(key);
        }
        List<Repository> repositories = githubRepository.getRepositoriesSync();
        githubRepository.getRepositoriesWithAdditionalInfoSync(repositories);
        githubRepository.getTrendingRepositoriesSync(githubRepository.getDefaultPeriodForTrending(),
                githubRepository.getDefaultLanguageForTrending(), false);
    }

    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
    }

    private static void onAccountCreated(Account newAccount, Context context) {
        configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);
        ContentResolver.setSyncAutomatically(newAccount,
                context.getString(R.string.sync_authority), true);
        syncImmediately(context);
    }

    /**
     * Helper method to have the sync adapter sync immediately
     *
     * @param context The context used to access the account service
     */
    private static void syncImmediately(Context context) {
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
