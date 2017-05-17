/*
 * Copyright 2017.  Dmitry Malkovich
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.dmitrymalkovich.android.githubanalytics.welcome;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.dmitrymalkovich.android.githubanalytics.R;
import com.dmitrymalkovich.android.githubanalytics.data.source.GithubRepository;
import com.dmitrymalkovich.android.githubanalytics.settings.SettingsActivity;
import com.dmitrymalkovich.android.githubanalytics.Utils;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(SettingsActivity.ThemePreferenceFragment.getTheme(this,
                SettingsActivity
                        .ThemePreferenceFragment.THEME_TYPE.NO_ACTION_BAR));
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
            Utils.addFragmentToActivity(getSupportFragmentManager(),
                    welcomeFragment, R.id.contentFrame);
        }

        new WelcomePresenter(
                GithubRepository.Injection.provideGithubRepository(this),
                welcomeFragment);
    }
}

