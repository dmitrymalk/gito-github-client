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
package com.dmitrymalkovich.android.githubapi;

import com.dmitrymalkovich.android.githubapi.core.AuthenticationBuilder;
import com.dmitrymalkovich.android.githubapi.core.ExploreBuilder;
import com.dmitrymalkovich.android.githubapi.core.RepositoryService;
import com.dmitrymalkovich.android.githubapi.core.StargazersService;
import com.dmitrymalkovich.android.githubapi.core.TrafficService;
import com.dmitrymalkovich.android.githubapi.core.UserService;

public class GitHubAPI {

    private GitHubAPI() {
    }

    public static TrafficService traffic() {
        return new TrafficService();
    }

    public static RepositoryService repository() {
        return new RepositoryService();
    }

    public static UserService user() {
        return new UserService();
    }

    public static StargazersService stargazers() {
        return new StargazersService();
    }

    public static AuthenticationBuilder auth() {
        return new AuthenticationBuilder();
    }

    public static ExploreBuilder trending() {
        return new ExploreBuilder();
    }
}
