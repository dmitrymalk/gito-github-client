package com.dmitrymalkovich.android.githubanalytics.navigation;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dmitrymalkovich.android.githubanalytics.R;
import com.dmitrymalkovich.android.githubanalytics.dashboard.DashboardFragment;
import com.dmitrymalkovich.android.githubanalytics.dashboard.DashboardPresenter;
import com.dmitrymalkovich.android.githubanalytics.data.source.GithubDataSource;
import com.dmitrymalkovich.android.githubanalytics.data.source.GithubRepository;
import com.dmitrymalkovich.android.githubanalytics.data.source.Injection;
import com.dmitrymalkovich.android.githubanalytics.data.source.LoaderProvider;
import com.dmitrymalkovich.android.githubanalytics.data.source.remote.gson.ResponseUser;
import com.dmitrymalkovich.android.githubanalytics.data.sync.SyncAdapter;
import com.dmitrymalkovich.android.githubanalytics.publicrepository.PublicRepositoryFragment;
import com.dmitrymalkovich.android.githubanalytics.publicrepository.PublicRepositoryPresenter;
import com.dmitrymalkovich.android.githubanalytics.trendingrepository.TrendingRepositoryFragment;
import com.dmitrymalkovich.android.githubanalytics.trendingrepository.TrendingRepositoryPresenter;
import com.dmitrymalkovich.android.githubanalytics.util.ActivityUtils;
import com.dmitrymalkovich.android.githubanalytics.welcome.WelcomeActivity;

public class NavigationViewActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @SuppressWarnings("unused")
    private static final String LOG_TAG = NavigationViewActivity.class.getSimpleName();
    private NavigationView mNavigationView;
    public static final String EXTRA_CURRENT_FRAGMENT = "EXTRA_CURRENT_FRAGMENT";
    private String mCurrentFragment = DashboardFragment.class.getSimpleName();

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(this);

        if (savedInstanceState == null) {
            showDashboard();
        }
        else
        {
            mCurrentFragment = savedInstanceState.getString(EXTRA_CURRENT_FRAGMENT);
            if (mCurrentFragment.equals(DashboardFragment.class.getSimpleName())) {
                showDashboard();
            } else if (mCurrentFragment.equals(PublicRepositoryFragment.class.getSimpleName())) {
                showRepositories();
            } else {
                showTrendingRepositories();
            }
        }

        SyncAdapter.initializeSyncAdapter(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        View headerLayout = mNavigationView.getHeaderView(0);
        final ImageView avatarView = (ImageView) headerLayout.findViewById(R.id.avatar);
        final TextView nameView = (TextView) headerLayout.findViewById(R.id.name);
        final TextView usernameView = (TextView) headerLayout.findViewById(R.id.username);
        final TextView followersView = (TextView) headerLayout.findViewById(R.id.followers);

        GithubRepository repository = Injection.provideGithubRepository(this);
        repository.getUser(new GithubDataSource.GerUserCallback() {
            @Override
            public void onUserLoaded(ResponseUser user) {
                Glide.with(NavigationViewActivity.this)
                        .load(user.getAvatarUrl()).into(avatarView);
                nameView.setText(user.getName());
                usernameView.setText(getString(R.string.username, user.getLogin()));
                followersView.setText(getString(R.string.followers, user.getFollowers()));
            }

            @Override
            public void onDataNotAvailable() {

            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(EXTRA_CURRENT_FRAGMENT, mCurrentFragment);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.navigation_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_dashboard) {
            showDashboard();
        } else if (id == R.id.nav_repositories) {
            showRepositories();
        } else if (id == R.id.nav_trending) {
            showTrendingRepositories();
        } else if (id == R.id.nav_feedback) {
            ActivityUtils.openFeedback(this);
        } else if (id == R.id.nav_sign_out) {
            signOut();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void showDashboard() {
        Fragment fragment = getSupportFragmentManager()
                .findFragmentById(R.id.content_navigation_view);
        DashboardFragment dashboardFragment;
        if (fragment instanceof DashboardFragment) {
            dashboardFragment = (DashboardFragment) fragment;
        } else {
            dashboardFragment = DashboardFragment.newInstance();
            ActivityUtils.replaceFragment(getSupportFragmentManager(),
                    dashboardFragment, R.id.content_navigation_view);
        }
        new DashboardPresenter(
                Injection.provideGithubRepository(this),
                dashboardFragment,
                new LoaderProvider(this),
                getSupportLoaderManager());

        mCurrentFragment = DashboardFragment.class.getSimpleName();
    }

    private void showRepositories() {
        Fragment fragment = getSupportFragmentManager()
                .findFragmentById(R.id.content_navigation_view);
        PublicRepositoryFragment publicRepositoriesFragment;
        if (fragment instanceof PublicRepositoryFragment) {
            publicRepositoriesFragment = (PublicRepositoryFragment) fragment;
        } else {
            publicRepositoriesFragment = PublicRepositoryFragment.newInstance();
            ActivityUtils.replaceFragment(getSupportFragmentManager(),
                    publicRepositoriesFragment, R.id.content_navigation_view);
        }
        new PublicRepositoryPresenter(
                Injection.provideGithubRepository(this),
                publicRepositoriesFragment,
                new LoaderProvider(this),
                getSupportLoaderManager());

        mCurrentFragment = PublicRepositoryFragment.class.getSimpleName();
    }

    private void showTrendingRepositories() {
        Fragment fragment = getSupportFragmentManager()
                .findFragmentById(R.id.content_navigation_view);
        TrendingRepositoryFragment trendingRepositoryFragment;
        if (fragment instanceof TrendingRepositoryFragment) {
            trendingRepositoryFragment = (TrendingRepositoryFragment) fragment;
        } else {
            trendingRepositoryFragment = TrendingRepositoryFragment.newInstance();
            ActivityUtils.replaceFragment(getSupportFragmentManager(),
                    trendingRepositoryFragment, R.id.content_navigation_view);
        }
        new TrendingRepositoryPresenter(
                Injection.provideGithubRepository(this),
                trendingRepositoryFragment,
                new LoaderProvider(this),
                getSupportLoaderManager());

        mCurrentFragment = TrendingRepositoryPresenter.class.getSimpleName();
    }

    private void signOut() {
        Injection.provideGithubRepository(this).saveToken(null, null);

        Intent intent = new Intent(this, WelcomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intent);
        finish();
    }
}
