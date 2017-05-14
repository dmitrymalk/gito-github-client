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
package com.dmitrymalkovich.android.githubanalytics.traffic;

import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dmitrymalkovich.android.githubanalytics.R;
import com.dmitrymalkovich.android.githubanalytics.data.source.local.contract.ReferrerContract;
import com.dmitrymalkovich.android.githubanalytics.util.CursorRecyclerViewAdapter;

class ReferrersListAdapter extends CursorRecyclerViewAdapter<ReferrersListAdapter.ViewHolder> {

    @SuppressWarnings("unused")
    private final TrafficContract.View mView;

    ReferrersListAdapter(Cursor cursor, TrafficContract.View view) {
        super(cursor);
        mView = view;
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_referrer, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final Cursor cursor) {
        final String name = cursor.getString(ReferrerContract.ReferrerEntry.COL_PATHS_REFERRER);
        String views = cursor.getString(ReferrerContract.ReferrerEntry.COL_PATHS_COUNT);
        String visitors = cursor.getString(ReferrerContract.ReferrerEntry.COL_PATHS_UNIQUES);

        holder.nameView.setText(name);
        holder.viewsView.setText(views);
        holder.visitorsView.setText(visitors);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mView.openUrl("http://www.google.com/#q=" + name);
            }
        });
    }

    @Override
    public int getItemCount() {
        return super.getItemCount();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView visitorsView;
        private TextView viewsView;
        private TextView nameView;

        ViewHolder(View view) {
            super(view);
            nameView = (TextView) view.findViewById(R.id.name);
            viewsView = (TextView) view.findViewById(R.id.views);
            visitorsView = (TextView) view.findViewById(R.id.visitors);
        }
    }
}