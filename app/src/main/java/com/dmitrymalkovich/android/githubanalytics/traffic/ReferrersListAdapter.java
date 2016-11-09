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

    private final TrafficContract.View mView;

    ReferrersListAdapter(Cursor cursor, TrafficContract.View view) {
        super(cursor);
        mView = view;
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_refferer, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final Cursor cursor) {
        holder.titleView.setText(cursor.getString(ReferrerContract.ReferrerEntry.COL_PATHS_REFERRER));
    }

    @Override
    public int getItemCount() {
        return super.getItemCount();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleView;

        ViewHolder(View view) {
            super(view);
            titleView = (TextView) view.findViewById(R.id.name);
        }
    }
}