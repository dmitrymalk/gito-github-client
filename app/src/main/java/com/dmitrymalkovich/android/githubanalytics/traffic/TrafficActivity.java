package com.dmitrymalkovich.android.githubanalytics.traffic;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.dmitrymalkovich.android.githubanalytics.R;
import com.dmitrymalkovich.android.githubanalytics.data.source.GithubRepository;
import com.dmitrymalkovich.android.githubanalytics.data.source.local.LoaderProvider;
import com.dmitrymalkovich.android.githubanalytics.util.ActivityUtils;

public class TrafficActivity extends AppCompatActivity {
    @SuppressWarnings("unused")
    private static final String LOG_TAG = TrafficActivity.class.getSimpleName();
    public static String EXTRA_REPOSITORY_ID = "EXTRA_REPOSITORY_ID";
    private long mRepositoryId;

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if (getIntent().hasExtra(EXTRA_REPOSITORY_ID)) {
            mRepositoryId = getIntent().getLongExtra(EXTRA_REPOSITORY_ID, 0);

        } else {
            throw new IllegalStateException("Repository id not specified");
        }

        showTraffic();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.navigation_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showTraffic() {
        Fragment fragment = getSupportFragmentManager()
                .findFragmentById(R.id.content_navigation_view);
        TrafficFragment trafficFragment;
        if (fragment instanceof TrafficFragment) {
            trafficFragment = (TrafficFragment) fragment;
        } else {
            trafficFragment = TrafficFragment.newInstance(mRepositoryId);
            ActivityUtils.replaceFragment(getSupportFragmentManager(),
                    trafficFragment, R.id.content_navigation_view);
        }
        new TrafficPresenter(
                GithubRepository.Injection.provideGithubRepository(this),
                trafficFragment,
                new LoaderProvider(this),
                getSupportLoaderManager());
    }
}
