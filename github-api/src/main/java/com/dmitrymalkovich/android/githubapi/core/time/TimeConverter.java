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
package com.dmitrymalkovich.android.githubapi.core.time;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class TimeConverter {
    private static final String LOG_TAG = TimeConverter.class.getSimpleName();

    public static long iso8601ToMilliseconds(String date) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
        long timeInMilliseconds = 0;
        try {
            Date parsedDate = df.parse(date);
            timeInMilliseconds = parsedDate.getTime();
        } catch (ParseException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
        }
        return timeInMilliseconds;
    }

    public static TimeZone getGitHubDefaultTimeZone() {
        return TimeZone.getTimeZone("GMT");
    }
}
