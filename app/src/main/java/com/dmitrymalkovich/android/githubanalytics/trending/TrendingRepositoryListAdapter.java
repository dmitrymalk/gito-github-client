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
package com.dmitrymalkovich.android.githubanalytics.trending;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dmitrymalkovich.android.githubanalytics.R;
import com.dmitrymalkovich.android.githubanalytics.data.source.local.GithubLocalDataSource;
import com.dmitrymalkovich.android.githubanalytics.data.source.local.contract.TrendingContract;
import com.dmitrymalkovich.android.githubanalytics.CursorRecyclerViewAdapter;

class TrendingRepositoryListAdapter extends CursorRecyclerViewAdapter<TrendingRepositoryListAdapter.ViewHolder> {

    private final TrendingRepositoryContract.View mView;

    TrendingRepositoryListAdapter(Cursor cursor, TrendingRepositoryContract.View view) {
        super(cursor);
        mView = view;
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_trending_repository, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final Cursor cursor) {
        holder.titleView.setText(cursor.getString(TrendingContract.TrendingEntry.COL_NAME));
        holder.subtitleView.setText(cursor.getString(TrendingContract.TrendingEntry.COL_DESCRIPTION));
        final String htmlUrl = cursor.getString(TrendingContract.TrendingEntry.COL_HTML_URL);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (htmlUrl != null) {
                    mView.openUrl(htmlUrl);
                }
            }
        });
        Context context = holder.titleView.getContext();
        String rank = context.getString(R.string.trending_rank, String.valueOf(cursor.getPosition() + 1));
        holder.rankView.setText(rank);

        String language = cursor.getString(TrendingContract.TrendingEntry.COL_LANGUAGE);
        // Capitalize the first letter
        language = language.substring(0, 1).toUpperCase() + language.substring(1);

        String period = cursor.getString(TrendingContract.TrendingEntry.COL_PERIOD);
        switch (period) {
            case GithubLocalDataSource.TrendingPeriod.MONTHLY:
                period = context.getString(R.string.trending_monthly);
                break;
            case GithubLocalDataSource.TrendingPeriod.WEEKLY:
                period = context.getString(R.string.trending_weekly);
                break;
            default:
            case GithubLocalDataSource.TrendingPeriod.DAILY:
                period = context.getString(R.string.trending_daily);
                break;
        }
        final String stars = cursor.getString(TrendingContract.TrendingEntry.COL_WATCHER_COUNT);
        String bottomText = context.getString(R.string.trending_information, language, stars, period);
        holder.infoView.setText(bottomText);

        Glide.clear(holder.avatarView);
        final String avatar = cursor.getString(TrendingContract.TrendingEntry.COL_AVATAR);
        Glide.with(context).load(avatar).into(holder.avatarView);
    }

    @Override
    public int getItemCount() {
        return super.getItemCount();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView titleView;
        private TextView subtitleView;
        private TextView rankView;
        private TextView infoView;
        private ImageView avatarView;

        ViewHolder(View view) {
            super(view);
            titleView = (TextView) view.findViewById(R.id.title);
            subtitleView = (TextView) view.findViewById(R.id.subtitle);
            rankView = (TextView) view.findViewById(R.id.rank);
            infoView = (TextView) view.findViewById(R.id.info);
            avatarView = (ImageView) view.findViewById(R.id.avatar);
        }
    }
}