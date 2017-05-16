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

import com.dmitrymalkovich.android.githubapi.core.data.TrendingRepository;

import java.io.IOException;
import java.util.List;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ExploreBuilder {
    private static final String THIRD_PARTY_GITHUB_API_BASE_URL = "http://anly.leanapp.cn/";
    private static OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
    private String mLanguage;
    private String mPeriod;

    public ExploreBuilder setLanguage(String language) {
        mLanguage = language;
        return this;
    }

    public ExploreBuilder setPeriod(String period) {
        mPeriod = period;
        return this;
    }

    @WorkerThread
    public List<TrendingRepository> getRepositories() throws IOException {

        GithubService githubService = createService(
                GithubService.class);
        Call<List<TrendingRepository>> call = githubService.getTrendingRepositories(mLanguage,
                mPeriod);

        List<TrendingRepository> repositories = call.execute().body();

        if (repositories != null) {
            return repositories;
        } else {
            throw new IOException();
        }
    }

    static <S> S createService(Class<S> serviceClass) {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        //interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = httpClient.addInterceptor(interceptor)
                .addInterceptor(new Interceptor() {
                    @Override
                    public okhttp3.Response intercept(Chain chain) throws IOException {
                        Request original = chain.request();
                        Request.Builder requestBuilder = original.newBuilder()
                                .header("Accept", "application/json")
                                .method(original.method(), original.body());
                        Request request = requestBuilder.build();
                        return chain.proceed(request);
                    }
                })
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(THIRD_PARTY_GITHUB_API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create()).client(client).build();
        return retrofit.create(serviceClass);
    }
}
