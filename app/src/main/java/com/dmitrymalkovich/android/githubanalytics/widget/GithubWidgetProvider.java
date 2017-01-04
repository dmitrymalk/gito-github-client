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
package com.dmitrymalkovich.android.githubanalytics.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.dmitrymalkovich.android.githubanalytics.R;
import com.dmitrymalkovich.android.githubanalytics.traffic.TrafficActivity;

/**
 * Implement functionality for Github Widget.
 */
public class GithubWidgetProvider extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
                         int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        for (int i : appWidgetIds) {
            updateWidget(context, appWidgetManager, i);
        }
    }

    void updateWidget(Context context, AppWidgetManager appWidgetManager,
                      int appWidgetId) {
        RemoteViews rv = new RemoteViews(context.getPackageName(),
                R.layout.widget_github);
        setList(rv, context, appWidgetId);

        Intent startActivityIntent = new Intent(context, TrafficActivity.class);
        PendingIntent startActivityPendingIntent = PendingIntent.getActivity(context, 0,
                startActivityIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        rv.setPendingIntentTemplate(R.id.repository_list, startActivityPendingIntent);

        appWidgetManager.updateAppWidget(appWidgetId, rv);
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId,
                R.id.repository_list);
    }

    void setList(RemoteViews rv, Context context, int appWidgetId) {
        Intent adapter = new Intent(context, GithubWidgetService.class);
        adapter.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        rv.setRemoteAdapter(R.id.repository_list, adapter);
    }
}
