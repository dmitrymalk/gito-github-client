package com.dmitrymalkovich.android.githubanalytics.dashboard;

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

import static com.dmitrymalkovich.android.githubanalytics.data.source.local.contract.RepositoryContract.RepositoryEntry.COL_CLONES_COUNT;
import static com.dmitrymalkovich.android.githubanalytics.data.source.local.contract.RepositoryContract.RepositoryEntry.COL_REPOSITORY_DESCRIPTION;
import static com.dmitrymalkovich.android.githubanalytics.data.source.local.contract.RepositoryContract.RepositoryEntry.COL_REPOSITORY_FORKS;
import static com.dmitrymalkovich.android.githubanalytics.data.source.local.contract.RepositoryContract.RepositoryEntry.COL_REPOSITORY_LANGUAGE;
import static com.dmitrymalkovich.android.githubanalytics.data.source.local.contract.RepositoryContract.RepositoryEntry.COL_REPOSITORY_NAME;
import static com.dmitrymalkovich.android.githubanalytics.data.source.local.contract.RepositoryContract.RepositoryEntry.COL_REPOSITORY_WATCHERS;
import static com.dmitrymalkovich.android.githubanalytics.data.source.local.contract.RepositoryContract.RepositoryEntry.COL_STARGAZERS_STARS;
import static com.dmitrymalkovich.android.githubanalytics.data.source.local.contract.RepositoryContract.RepositoryEntry.COL_VIEWS_COUNT;

class RepositoryListAdapter extends CursorRecyclerViewAdapter<RepositoryListAdapter.ViewHolder> {

    private static final int VIEW_TYPE_THE_MOST_POPULAR = 0;
    private static final int VIEW_TYPE_POPULAR = 1;

    public static String LOG_TAG = RepositoryListAdapter.class.getSimpleName();

    RepositoryListAdapter(Cursor cursor) {
        super(cursor);
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        View view;
        ViewHolder vh;
        switch (viewType) {
            case VIEW_TYPE_THE_MOST_POPULAR:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_item_most_popular_repository, parent, false);
                vh = new ViewHolder(view);
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                    }
                });
                break;
            case VIEW_TYPE_POPULAR:
            default:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_item_popular_repository, parent, false);
                vh = new ViewHolder(view);
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                    }
                });
                break;
        }
        return vh;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return VIEW_TYPE_THE_MOST_POPULAR;
        } else {
            return VIEW_TYPE_POPULAR;
        }
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final Cursor cursor) {
        Context context = holder.itemView.getContext();
        holder.titleView.setText(cursor.getString(COL_REPOSITORY_NAME));
        holder.subtitleView.setText(cursor.getString(COL_REPOSITORY_DESCRIPTION));
        holder.starsTotalView.setText(cursor.getString(COL_REPOSITORY_WATCHERS));
        holder.totalForksView.setText(cursor.getString(COL_REPOSITORY_FORKS));
        holder.languageView.setText(cursor.getString(COL_REPOSITORY_LANGUAGE));
        holder.languageIconView.setVisibility(holder.languageView.getText() != null
                && holder.languageView.getText().length() != 0
                ? View.VISIBLE : View.GONE);
        if (holder.getItemViewType() == VIEW_TYPE_THE_MOST_POPULAR) {
            holder.clonesCountView.setText(cursor.getString(COL_CLONES_COUNT));
            holder.viewsCountView.setText(cursor.getString(COL_VIEWS_COUNT));
            String stargazersToday = cursor.getString(COL_STARGAZERS_STARS);
            holder.starsTodayView.setText(
                    context.getString(R.string.dashboard_stargazers, stargazersToday));
        }
    }

    @Override
    public int getItemCount() {
        return super.getItemCount();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView starsTotalView;
        private final TextView languageView;
        private final ImageView languageIconView;
        private final TextView totalForksView;
        private final TextView clonesCountView;
        private final TextView viewsCountView;
        private final TextView starsTodayView;
        private TextView titleView;
        private TextView subtitleView;

        ViewHolder(View view) {
            super(view);
            titleView = (TextView) view.findViewById(R.id.title);
            subtitleView = (TextView) view.findViewById(R.id.subtitle);
            starsTotalView = (TextView) view.findViewById(R.id.stars_total);
            languageView = (TextView) view.findViewById(R.id.language);
            languageIconView = (ImageView) view.findViewById(R.id.language_icon);
            totalForksView = (TextView) view.findViewById(R.id.forks_total);
            clonesCountView = (TextView) view.findViewById(R.id.clones_count);
            viewsCountView = (TextView) view.findViewById(R.id.views_count);
            starsTodayView = (TextView) view.findViewById(R.id.stars_today);
        }
    }
}