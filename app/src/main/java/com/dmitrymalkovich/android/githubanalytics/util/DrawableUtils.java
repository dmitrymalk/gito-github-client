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

import static com.dmitrymalkovich.android.githubanalytics.data.source.local.GithubLocalDataSource.*;

public class DrawableUtils {

    @SuppressWarnings("deprecation")
    public static Drawable getColor(Context context, String language) {
        Resources resources = context.getResources();

        if (language == null) {
            return resources.getDrawable(R.drawable.shape_oval_java);
        }

        switch (language) {
            case TRENDING_LANGUAGE_C:
                return resources.getDrawable(R.drawable.shape_oval_c);
            case TRENDING_LANGUAGE_RUBY:
                return resources.getDrawable(R.drawable.shape_oval_ruby);
            case TRENDING_LANGUAGE_JAVASCRIPT:
                return resources.getDrawable(R.drawable.shape_oval_javascript);
            case TRENDING_LANGUAGE_SWIFT:
                return resources.getDrawable(R.drawable.shape_oval_swift);
            case TRENDING_LANGUAGE_OBJECTIVE_C:
                return resources.getDrawable(R.drawable.shape_oval_objective_c);
            case TRENDING_LANGUAGE_C_PLUS_PLUS:
                return resources.getDrawable(R.drawable.shape_oval_c_plus);
            case TRENDING_LANGUAGE_PYTHON:
                return resources.getDrawable(R.drawable.shape_oval_python);
            case TRENDING_LANGUAGE_C_SHARP:
                return resources.getDrawable(R.drawable.shape_oval_c_sharp);
            case TRENDING_LANGUAGE_HTML:
                return resources.getDrawable(R.drawable.shape_oval_html);
            default:
            case TRENDING_LANGUAGE_JAVA:
                return resources.getDrawable(R.drawable.shape_oval_java);
        }
    }
}
