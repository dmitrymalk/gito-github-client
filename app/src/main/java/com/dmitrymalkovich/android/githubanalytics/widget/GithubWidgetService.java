package com.dmitrymalkovich.android.githubanalytics.widget;

import android.content.Intent;
import android.widget.RemoteViewsService;

/**
 * The service to be connected to for a remote adapter to request RemoteViews for GithubWidget.
 */
public class GithubWidgetService extends RemoteViewsService {

    @Override
    public RemoteViewsService.RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new GithubWidgetFactory(getApplicationContext(), intent);
    }
}
