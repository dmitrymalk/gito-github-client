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

import com.dmitrymalkovich.android.githubanalytics.R;
import com.dmitrymalkovich.android.githubanalytics.dashboard.DashboardFragment;
import com.dmitrymalkovich.android.githubanalytics.dashboard.DashboardPresenter;
import com.dmitrymalkovich.android.githubanalytics.data.source.Injection;
import com.dmitrymalkovich.android.githubanalytics.data.source.LoaderProvider;
import com.dmitrymalkovich.android.githubanalytics.data.sync.SyncAdapter;
import com.dmitrymalkovich.android.githubanalytics.publicrepositories.PublicRepositoriesFragment;
import com.dmitrymalkovich.android.githubanalytics.publicrepositories.PublicRepositoryPresenter;
import com.dmitrymalkovich.android.githubanalytics.util.ActivityUtils;
import com.dmitrymalkovich.android.githubanalytics.welcome.WelcomeActivity;

public class NavigationViewActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

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

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        showDashboard();

        SyncAdapter.initializeSyncAdapter(this);
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
        } else if (id == R.id.nav_feedback) {
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
    }

    private void showRepositories() {
        Fragment fragment = getSupportFragmentManager()
                .findFragmentById(R.id.content_navigation_view);
        PublicRepositoriesFragment publicRepositoriesFragment;
        if (fragment instanceof PublicRepositoriesFragment) {
            publicRepositoriesFragment = (PublicRepositoriesFragment) fragment;
        } else {
            publicRepositoriesFragment = PublicRepositoriesFragment.newInstance();
            ActivityUtils.replaceFragment(getSupportFragmentManager(),
                    publicRepositoriesFragment, R.id.content_navigation_view);
        }
        new PublicRepositoryPresenter(
                Injection.provideGithubRepository(this),
                publicRepositoriesFragment,
                new LoaderProvider(this),
                getSupportLoaderManager());
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
