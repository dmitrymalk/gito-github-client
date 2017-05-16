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
package com.dmitrymalkovich.android.githubapi.core;

import com.dmitrymalkovich.android.githubapi.core.data.User;

import java.io.IOException;

public class UserService extends Service {

    public UserService setToken(String token) {
        return (UserService) super.setToken(token);
    }

    public User getUser() throws IOException {
        org.eclipse.egit.github.core.service.UserService service = new org.eclipse.egit.github.core.service.UserService();
        service.getClient().setOAuth2Token(getAccessToken().getToken());
        org.eclipse.egit.github.core.User eGitUser = service.getUser();
        User user = new User();
        user.setName(eGitUser.getName());
        user.setLogin(eGitUser.getLogin());
        user.setAvatarUrl(eGitUser.getAvatarUrl());
        user.setFollowers(String.valueOf(eGitUser.getFollowers()));
        return user;
    }
}