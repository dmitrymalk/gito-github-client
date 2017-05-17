/*
 * Copyright 2016, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.dmitrymalkovich.android.githubanalytics;

import android.app.Activity;
import android.app.ApplicationErrorReport;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.dmitrymalkovich.android.githubanalytics.GitoApplication;
import com.dmitrymalkovich.android.githubanalytics.R;
import com.dmitrymalkovich.android.githubanalytics.data.source.local.GithubLocalDataSource;
import com.dmitrymalkovich.android.githubanalytics.settings.SettingsActivity;
import com.dmitrymalkovich.android.githubapi.core.time.TimeConverter;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * This provides methods to help Activities load their UI.
 */
public class Utils {

    /**
     * The {@code fragment} is added to the container view with id {@code frameId}. The operation is
     * performed by the {@code fragmentManager}.
     */
    public static void addFragmentToActivity(@NonNull FragmentManager fragmentManager,
                                             @NonNull Fragment fragment, int frameId) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(frameId, fragment);
        transaction.commitAllowingStateLoss();
    }

    public static void replaceFragment(@NonNull FragmentManager fragmentManager,
                                       @NonNull Fragment fragment, int frameId) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(frameId, fragment);
        transaction.commitAllowingStateLoss();
    }

    public static void openFeedback(Activity activity) {
        try {
            throw new IOException();
        } catch (IOException e) {
            ApplicationErrorReport report = new ApplicationErrorReport();
            report.packageName = report.processName = activity.getApplication()
                    .getPackageName();
            report.time = System.currentTimeMillis();
            report.type = ApplicationErrorReport.TYPE_CRASH;
            report.systemApp = false;
            ApplicationErrorReport.CrashInfo crash = new ApplicationErrorReport.CrashInfo();
            crash.exceptionClassName = e.getClass().getSimpleName();
            crash.exceptionMessage = e.getMessage();
            StringWriter writer = new StringWriter();
            PrintWriter printer = new PrintWriter(writer);
            e.printStackTrace(printer);
            crash.stackTrace = writer.toString();
            StackTraceElement stack = e.getStackTrace()[0];
            crash.throwClassName = stack.getClassName();
            crash.throwFileName = stack.getFileName();
            crash.throwLineNumber = stack.getLineNumber();
            crash.throwMethodName = stack.getMethodName();
            report.crashInfo = crash;
            Intent intent = new Intent(Intent.ACTION_APP_ERROR);
            intent.putExtra(Intent.EXTRA_BUG_REPORT, report);
            activity.startActivity(intent);
        }
    }

    public static boolean isNetworkAvailable() {
        Context context = GitoApplication.context();
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static void openSettings(Activity activity) {
        Intent intent = new Intent(activity, SettingsActivity.class);
        activity.startActivity(intent);
    }

    @SuppressWarnings("deprecation")
    public static Drawable getColor(Context context, String language) {
        Resources resources = context.getResources();

        if (language == null) {
            return resources.getDrawable(R.drawable.shape_oval_java);
        }

        switch (language) {
            case GithubLocalDataSource.TrendingLanguage.C:
                return resources.getDrawable(R.drawable.shape_oval_c);
            case GithubLocalDataSource.TrendingLanguage.RUBY:
                return resources.getDrawable(R.drawable.shape_oval_ruby);
            case GithubLocalDataSource.TrendingLanguage.JAVASCRIPT:
                return resources.getDrawable(R.drawable.shape_oval_javascript);
            case GithubLocalDataSource.TrendingLanguage.SWIFT:
                return resources.getDrawable(R.drawable.shape_oval_swift);
            case GithubLocalDataSource.TrendingLanguage.OBJECTIVE_C:
                return resources.getDrawable(R.drawable.shape_oval_objective_c);
            case GithubLocalDataSource.TrendingLanguage.C_PLUS_PLUS:
                return resources.getDrawable(R.drawable.shape_oval_c_plus);
            case GithubLocalDataSource.TrendingLanguage.PYTHON:
                return resources.getDrawable(R.drawable.shape_oval_python);
            case GithubLocalDataSource.TrendingLanguage.C_SHARP:
                return resources.getDrawable(R.drawable.shape_oval_c_sharp);
            case GithubLocalDataSource.TrendingLanguage.HTML:
                return resources.getDrawable(R.drawable.shape_oval_html);
            default:
            case GithubLocalDataSource.TrendingLanguage.JAVA:
                return resources.getDrawable(R.drawable.shape_oval_java);
        }
    }

    public static long today() {
        Calendar c = new GregorianCalendar(TimeConverter.getGitHubDefaultTimeZone());
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day, 0, 0, 0);
        return calendar.getTimeInMillis() / 1000 * 1000;
    }

    public static long yesterday() {
        Calendar c = new GregorianCalendar(TimeConverter.getGitHubDefaultTimeZone());
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day - 1, 0, 0, 0);
        return calendar.getTimeInMillis() / 1000 * 1000;
    }

    public static long twoWeeksAgo() {
        Calendar c = new GregorianCalendar(TimeConverter.getGitHubDefaultTimeZone());
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day - 14, 0, 0, 0);
        return calendar.getTimeInMillis() / 1000 * 1000;
    }

    public static long weekAgo() {
        Calendar c = new GregorianCalendar(TimeConverter.getGitHubDefaultTimeZone());
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day - 7, 0, 0, 0);
        return calendar.getTimeInMillis() / 1000 * 1000;
    }

    public static String humanReadable(long timestamp) {
        DateFormat dateFormat = new SimpleDateFormat("dd.MM", Locale.US);
        Date date = new Date(timestamp);
        return dateFormat.format(date);
    }
}
