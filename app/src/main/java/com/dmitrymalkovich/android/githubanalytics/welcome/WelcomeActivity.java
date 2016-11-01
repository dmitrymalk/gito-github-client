package com.dmitrymalkovich.android.githubanalytics.welcome;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.dmitrymalkovich.android.githubanalytics.R;
import com.dmitrymalkovich.android.githubanalytics.data.source.Injection;
import com.dmitrymalkovich.android.githubanalytics.util.ActivityUtils;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        WelcomeFragment welcomeFragment = (WelcomeFragment) getSupportFragmentManager()
                .findFragmentById(R.id.contentFrame);

        if (welcomeFragment == null) {
            welcomeFragment = WelcomeFragment.newInstance();
            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(),
                    welcomeFragment, R.id.contentFrame);
        }

        new WelcomePresenter(
                Injection.provideGithubRepository(this),
                welcomeFragment);
    }
}

