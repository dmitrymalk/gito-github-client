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
package com.dmitrymalkovich.android.githubanalytics.repositories;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.dmitrymalkovich.android.githubanalytics.R;
import com.dmitrymalkovich.android.githubanalytics.util.CursorRecyclerViewAdapter;
import com.dmitrymalkovich.android.githubanalytics.util.DrawableUtils;

import static com.dmitrymalkovich.android.githubanalytics.data.source.local.contract.RepositoryContract.RepositoryEntry.COL_REPOSITORY_DESCRIPTION;
import static com.dmitrymalkovich.android.githubanalytics.data.source.local.contract.RepositoryContract.RepositoryEntry.COL_REPOSITORY_FORK;
import static com.dmitrymalkovich.android.githubanalytics.data.source.local.contract.RepositoryContract.RepositoryEntry.COL_REPOSITORY_FORKS;
import static com.dmitrymalkovich.android.githubanalytics.data.source.local.contract.RepositoryContract.RepositoryEntry.COL_REPOSITORY_HTML_URL;
import static com.dmitrymalkovich.android.githubanalytics.data.source.local.contract.RepositoryContract.RepositoryEntry.COL_REPOSITORY_ID;
import static com.dmitrymalkovich.android.githubanalytics.data.source.local.contract.RepositoryContract.RepositoryEntry.COL_REPOSITORY_LANGUAGE;
import static com.dmitrymalkovich.android.githubanalytics.data.source.local.contract.RepositoryContract.RepositoryEntry.COL_REPOSITORY_NAME;
import static com.dmitrymalkovich.android.githubanalytics.data.source.local.contract.RepositoryContract.RepositoryEntry.COL_REPOSITORY_PINNED;
import static com.dmitrymalkovich.android.githubanalytics.data.source.local.contract.RepositoryContract.RepositoryEntry.COL_REPOSITORY_WATCHERS;

class PublicRepositoryListAdapter extends CursorRecyclerViewAdapter<PublicRepositoryListAdapter.ViewHolder> {

    public static String LOG_TAG = PublicRepositoryListAdapter.class.getSimpleName();
    private final PublicRepositoryContract.View mView;

    PublicRepositoryListAdapter(Cursor cursor, PublicRepositoryContract.View view) {
        super(cursor);
        mView = view;
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_repository, parent, false);
        return new ViewHolder(view);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onBindViewHolder(final ViewHolder holder, final Cursor cursor) {
        Context context = holder.itemView.getContext();
        Resources resources = context.getResources();

        holder.titleView.setText(cursor.getString(COL_REPOSITORY_NAME));
        holder.subtitleView.setText(cursor.getString(COL_REPOSITORY_DESCRIPTION));
        holder.watchersView.setText(cursor.getString(COL_REPOSITORY_WATCHERS));

        setLanguage(context, cursor, holder);

        // Link
        final String htmlUrl = cursor.getString(COL_REPOSITORY_HTML_URL);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (htmlUrl != null) {
                    mView.openUrl(htmlUrl);
                }
            }
        });

        setFork(context, cursor, holder);
        setPinned(resources, cursor, holder);

    }

    @SuppressWarnings("deprecation")
    private void setLanguage(Context context, Cursor cursor, ViewHolder holder) {
        String language = cursor.getString(COL_REPOSITORY_LANGUAGE);
        holder.languageView.setText(cursor.getString(COL_REPOSITORY_LANGUAGE));
        holder.languageIconView.setVisibility(holder.languageView.getText() != null
                && holder.languageView.getText().length() != 0
                ? View.VISIBLE : View.GONE);
        holder.languageView.setVisibility(holder.languageIconView.getVisibility() == View.VISIBLE
                ? View.VISIBLE : View.GONE);
        holder.languageIconView.setBackgroundDrawable(DrawableUtils.getColor(context, language));
    }

    @SuppressWarnings("deprecation")
    private void setFork(Context context, Cursor cursor, ViewHolder holder) {
        holder.forksView.setText(cursor.getString(COL_REPOSITORY_FORKS));
        String fork = cursor.getString(COL_REPOSITORY_FORK);
        boolean forked = fork != null && fork.equals("1");
        if (forked) {
            holder.badgeView.setBackgroundColor(context.getResources()
                    .getColor(R.color.blue));
            holder.badgeView.setText(R.string.repositories_forked);
        } else {
            holder.badgeView.setBackgroundColor(context.getResources()
                    .getColor(R.color.green));
            holder.badgeView.setText(R.string.trending_public);
        }

        final String pinned = cursor.getString(COL_REPOSITORY_PINNED);
        final long id = cursor.getLong(COL_REPOSITORY_ID);
        holder.favoriteView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mView.setPinned(!"1".equals(pinned), id);
            }
        });
        holder.favoriteView.setVisibility(!forked ? View.VISIBLE : View.INVISIBLE);
    }

    @SuppressWarnings("deprecation")
    private void setPinned(Resources resources, Cursor cursor, ViewHolder holder) {
        String pinned = cursor.getString(COL_REPOSITORY_PINNED);
        final boolean favorite = "1".equals(pinned);
        if (favorite) {
            holder.favoriteView.setImageDrawable(
                    resources.getDrawable(R.drawable.ic_turned_in_blue_24dp));
        } else {
            holder.favoriteView.setImageDrawable(
                    resources.getDrawable(R.drawable.ic_turned_in_not_blue_24dp));
        }
    }

    @Override
    public int getItemCount() {
        return super.getItemCount();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView watchersView;
        private final TextView languageView;
        private final TextView forksView;
        private final TextView badgeView;
        private final ImageView favoriteView;
        private final ImageView languageIconView;
        private TextView titleView;
        private TextView subtitleView;

        ViewHolder(View view) {
            super(view);
            titleView = (TextView) view.findViewById(R.id.title);
            subtitleView = (TextView) view.findViewById(R.id.subtitle);
            watchersView = (TextView) view.findViewById(R.id.stars_total);
            languageView = (TextView) view.findViewById(R.id.language);
            languageIconView = (ImageView) view.findViewById(R.id.language_icon);
            forksView = (TextView) view.findViewById(R.id.forks_total);
            badgeView = (TextView) view.findViewById(R.id.badge);
            favoriteView = (ImageView) view.findViewById(R.id.favorite);
        }
    }
}