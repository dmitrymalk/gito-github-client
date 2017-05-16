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

import android.support.annotation.WorkerThread;

import org.eclipse.egit.github.core.Repository;

import java.io.IOException;
import java.util.List;

public class RepositoryService extends Service {

    public RepositoryService setToken(String token) {
        return (RepositoryService) super.setToken(token);
    }

    @WorkerThread
    public List<Repository> getRepositories() throws IOException {
        org.eclipse.egit.github.core.service.RepositoryService service = new org.eclipse.egit.github.core.service.RepositoryService();
        service.getClient().setOAuth2Token(getAccessToken().getToken());
        return service.getRepositories();
    }
}
