package com.dmitrymalkovich.android.githubanalytics.basicauthorization;

import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.dmitrymalkovich.android.githubanalytics.R;
import com.dmitrymalkovich.android.githubanalytics.data.source.Injection;
import com.dmitrymalkovich.android.githubanalytics.data.source.LoaderProvider;
import com.dmitrymalkovich.android.githubanalytics.util.ActivityUtils;

/**
 * A login screen that offers login via email/password.
 */
public class BasicAuthorizationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Set up the toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        BasicAuthorizationFragment basicAuthorizationFragment = (BasicAuthorizationFragment) getSupportFragmentManager()
                .findFragmentById(R.id.contentFrame);

        if (basicAuthorizationFragment == null) {
            basicAuthorizationFragment = BasicAuthorizationFragment.newInstance();
            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(),
                    basicAuthorizationFragment, R.id.contentFrame);
        }

        new BasicAuthorizationPresenter(
                Injection.provideTasksRepository(this),
                basicAuthorizationFragment,
                new LoaderProvider(this),
                getSupportLoaderManager());
    }
}

