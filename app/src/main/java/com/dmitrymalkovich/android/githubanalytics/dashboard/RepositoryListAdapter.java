package com.dmitrymalkovich.android.githubanalytics.dashboard;

import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.dmitrymalkovich.android.githubanalytics.R;
import com.dmitrymalkovich.android.githubanalytics.util.CursorRecyclerViewAdapter;

import static com.dmitrymalkovich.android.githubanalytics.data.source.local.contract.RepositoryContract.RepositoryEntry.COL_REPOSITORY_DESCRIPTION;
import static com.dmitrymalkovich.android.githubanalytics.data.source.local.contract.RepositoryContract.RepositoryEntry.COL_REPOSITORY_LANGUAGE;
import static com.dmitrymalkovich.android.githubanalytics.data.source.local.contract.RepositoryContract.RepositoryEntry.COL_REPOSITORY_NAME;
import static com.dmitrymalkovich.android.githubanalytics.data.source.local.contract.RepositoryContract.RepositoryEntry.COL_REPOSITORY_WATCHERS;

class RepositoryListAdapter extends CursorRecyclerViewAdapter<RepositoryListAdapter.ViewHolder> {

    RepositoryListAdapter(Cursor cursor) {
        super(cursor);
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_popular_repository, parent, false);
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
        holder.titleView.setText(cursor.getString(COL_REPOSITORY_NAME));
        holder.subtitleView.setText(cursor.getString(COL_REPOSITORY_DESCRIPTION));
        holder.watchersView.setText(cursor.getString(COL_REPOSITORY_WATCHERS));
        holder.languageView.setText(cursor.getString(COL_REPOSITORY_LANGUAGE));
        holder.languageIconView.setVisibility(holder.languageView.getText() != null
                    && holder.languageView.getText().length() != 0
                    ? View.VISIBLE : View.GONE);
    }

    @Override
    public int getItemCount() {
        return super.getItemCount();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView watchersView;
        private final TextView languageView;
        private final ImageView languageIconView;
        TextView titleView;
        TextView subtitleView;

        ViewHolder(View view) {
            super(view);
            titleView = (TextView) view.findViewById(R.id.title);
            subtitleView = (TextView) view.findViewById(R.id.subtitle);
            watchersView = (TextView) view.findViewById(R.id.repository_watchers);
            languageView = (TextView) view.findViewById(R.id.repository_language);
            languageIconView = (ImageView) view.findViewById(R.id.repository_language_icon);
        }
    }
}