package com.dmitrymalkovich.android.githubanalytics.publicrepository;

import android.content.Context;
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
import static com.dmitrymalkovich.android.githubanalytics.data.source.local.contract.RepositoryContract.RepositoryEntry.COL_REPOSITORY_FORK;
import static com.dmitrymalkovich.android.githubanalytics.data.source.local.contract.RepositoryContract.RepositoryEntry.COL_REPOSITORY_FORKS;
import static com.dmitrymalkovich.android.githubanalytics.data.source.local.contract.RepositoryContract.RepositoryEntry.COL_REPOSITORY_HTML_URL;
import static com.dmitrymalkovich.android.githubanalytics.data.source.local.contract.RepositoryContract.RepositoryEntry.COL_REPOSITORY_LANGUAGE;
import static com.dmitrymalkovich.android.githubanalytics.data.source.local.contract.RepositoryContract.RepositoryEntry.COL_REPOSITORY_NAME;
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
        Context context = holder.itemView.getContext();

        holder.titleView.setText(cursor.getString(COL_REPOSITORY_NAME));
        holder.subtitleView.setText(cursor.getString(COL_REPOSITORY_DESCRIPTION));
        holder.watchersView.setText(cursor.getString(COL_REPOSITORY_WATCHERS));
        holder.languageView.setText(cursor.getString(COL_REPOSITORY_LANGUAGE));
        holder.languageIconView.setVisibility(holder.languageView.getText() != null
                && holder.languageView.getText().length() != 0
                ? View.VISIBLE : View.GONE);
        final String htmlUrl = cursor.getString(COL_REPOSITORY_HTML_URL);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (htmlUrl != null) {
                    mView.openUrl(htmlUrl);
                }
            }
        });

        holder.forksView.setText(cursor.getString(COL_REPOSITORY_FORKS));
        String fork = cursor.getString(COL_REPOSITORY_FORK);
        boolean forked = fork != null && fork.equals("1");
        if (forked) {
            holder.badgeView.setBackgroundColor(context.getResources()
                    .getColor(R.color.blue));
            holder.badgeView.setText(R.string.repositories_forked);
        }
        else
        {
            holder.badgeView.setBackgroundColor(context.getResources()
                    .getColor(R.color.green));
            holder.badgeView.setText(R.string.trending_public);
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
        TextView titleView;
        TextView subtitleView;
        private final ImageView languageIconView;

        ViewHolder(View view) {
            super(view);
            titleView = (TextView) view.findViewById(R.id.title);
            subtitleView = (TextView) view.findViewById(R.id.subtitle);
            watchersView = (TextView) view.findViewById(R.id.stars_total);
            languageView = (TextView) view.findViewById(R.id.language);
            languageIconView = (ImageView) view.findViewById(R.id.language_icon);
            forksView = (TextView) view.findViewById(R.id.forks_total);
            badgeView = (TextView) view.findViewById(R.id.badge);
        }
    }
}