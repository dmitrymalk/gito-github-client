package com.dmitrymalkovich.android.githubanalytics.data.source.remote.gson;

import com.dmitrymalkovich.android.githubanalytics.data.source.remote.GithubServiceGenerator;
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

    @SerializedName("message")
    private String message = "No internet connection";

    public String getMessage() {
        return message;
    }

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
}
