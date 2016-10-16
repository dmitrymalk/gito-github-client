package com.dmitrymalkovich.android.githubanalytics.trendingrepository;

import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dmitrymalkovich.android.githubanalytics.R;
import com.dmitrymalkovich.android.githubanalytics.data.source.local.contract.TrendingContract;
import com.dmitrymalkovich.android.githubanalytics.util.CursorRecyclerViewAdapter;

class TrendingRepositoryListAdapter extends CursorRecyclerViewAdapter<TrendingRepositoryListAdapter.ViewHolder> {

    TrendingRepositoryListAdapter(Cursor cursor) {
        super(cursor);
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_trending_repository, parent, false);
        final ViewHolder vh = new ViewHolder(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final Cursor cursor) {
        holder.titleView.setText(cursor.getString(TrendingContract.TrendingEntry.COL_NAME));
        holder.subtitleView.setText(cursor.getString(TrendingContract.TrendingEntry.COL_DESCRIPTION));
    }

    @Override
    public int getItemCount() {
        return super.getItemCount();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleView;
        TextView subtitleView;

        ViewHolder(View view) {
            super(view);
            titleView = (TextView) view.findViewById(R.id.repository_title);
            subtitleView = (TextView) view.findViewById(R.id.repository_subtitle);
        }
    }
}