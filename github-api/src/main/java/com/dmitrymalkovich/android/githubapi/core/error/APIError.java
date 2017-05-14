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
package com.dmitrymalkovich.android.githubapi.core.error;

import com.dmitrymalkovich.android.githubapi.core.service.GithubServiceGenerator;
import com.google.gson.annotations.SerializedName;

import java.io.IOException;
import java.lang.annotation.Annotation;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Client Errors: https://developer.github.com/v3/
 */
public class APIError {

    @SuppressWarnings("all")
    @SerializedName("message")
    private String mMessage = "No internet connection";

    public static APIError parseError(Response<?> response) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(GithubServiceGenerator.API_HTTPS_BASE_URL)
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
