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
package com.dmitrymalkovich.android.githubanalytics.util;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;

import com.dmitrymalkovich.android.githubanalytics.R;
import com.dmitrymalkovich.android.githubanalytics.data.source.local.GithubLocalDataSource;

public class DrawableUtils {

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
}
