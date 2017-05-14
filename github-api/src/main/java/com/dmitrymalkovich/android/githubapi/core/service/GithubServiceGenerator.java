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
package com.dmitrymalkovich.android.githubapi.core.service;

import android.support.annotation.NonNull;

import com.dmitrymalkovich.android.githubapi.core.gson.AccessToken;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Credit to https://futurestud.io/tutorials/oauth-2-on-android-with-retrofit
 * <p>
 * OAuth GitHub API: https://developer.github.com/v3/oauth/
 */
public class GithubServiceGenerator {

    public static final String API_URL_AUTH = "https://github.com/login/oauth/authorize/";
    private static final String API_BASE_URL = "https://github.com/";
    public static final String API_HTTPS_BASE_URL = "https://api.github.com/";

    private static OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

    public static <S> S createService(Class<S> serviceClass) {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        //interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
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
                .baseUrl(API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create()).client(client).build();
        return retrofit.create(serviceClass);
    }

    public static <S> S createService(Class<S> serviceClass, @NonNull final AccessToken token,
                               String baseUrl, final String headerAccept) {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        //interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = httpClient
                .addInterceptor(interceptor)
                .addInterceptor(new Interceptor() {
                    @Override
                    public okhttp3.Response intercept(Chain chain) throws IOException {
                        Request original = chain.request();
                        Request.Builder requestBuilder = original.newBuilder()
                                .header("Accept", headerAccept)
                                .header("Authorization",
                                        token.getTokenType() + " " + token.getToken())
                                .method(original.method(), original.body());
                        Request request = requestBuilder.build();
                        return chain.proceed(request);
                    }
                })
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create()).client(client).build();
        return retrofit.create(serviceClass);
    }
}
