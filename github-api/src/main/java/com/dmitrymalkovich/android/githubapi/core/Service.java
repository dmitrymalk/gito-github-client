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

import android.support.annotation.NonNull;

import com.dmitrymalkovich.android.githubapi.core.data.AccessToken;
import com.dmitrymalkovich.android.githubapi.core.data.Star;
import com.google.gson.annotations.SerializedName;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.List;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Converter;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Credit to https://futurestud.io/tutorials/oauth-2-on-android-with-retrofit
 * <p>
 * OAuth GitHub API: https://developer.github.com/v3/oauth/
 */
public class Service {
    public static final String API_URL_AUTH = "https://github.com/login/oauth/authorize/";
    static final String API_HTTPS_BASE_URL = "https://api.github.com/";
    private static final String API_BASE_URL = "https://github.com/";
    private static OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
    private AccessToken mAccessToken = new AccessToken();

    Service() {
    }

    public Service setToken(String token) {
        mAccessToken.setAccessToken(token);
        return this;
    }

    public Service setTokenType(String tokenType) {
        mAccessToken.setTokenType(tokenType);
        return this;
    }

    AccessToken getAccessToken() {
        return mAccessToken;
    }

    <S> S createService(Class<S> serviceClass) {
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

    <S> S createService(Class<S> serviceClass, @NonNull final AccessToken token,
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

    /**
     * Client Errors: https://developer.github.com/v3/
     */
    @SuppressWarnings("all")
    public static class APIError {

        @SerializedName("message")
        private String mMessage = "No internet connection";

        public static APIError parseError(Response<?> response) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(API_HTTPS_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(new OkHttpClient.Builder().build()).build();

            Converter<ResponseBody, APIError> converter =
                    retrofit.responseBodyConverter(APIError.class, new Annotation[0]);

            APIError error;

            try {
                error = converter.convert(response.errorBody());
            } catch (IOException e) {
                return new APIError();
            }

            return error;
        }

        public String getMessage() {
            return mMessage;
        }
    }

    public class Pagination {

        public static final String LAST_PAGE = "last";
        private static final String HEADER_LINK = "Link";
        private int mLastPage;

        public void parse(Response<List<Star>> response) {
            String headerLink = response.headers().get(HEADER_LINK);
            if (headerLink != null) {
                headerLink = headerLink.replace(
                        headerLink.substring(headerLink.lastIndexOf(">")), "");
                mLastPage = Integer.valueOf(headerLink.replace(
                        headerLink.substring(0, headerLink.lastIndexOf("=") + 1), ""));
            }
        }

        int getLastPage() {
            return mLastPage;
        }
    }
}
