package com.dmitrymalkovich.android.githubanalytics.dashboard;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.dmitrymalkovich.android.githubanalytics.R;
import com.dmitrymalkovich.android.githubanalytics.util.DrawableUtils;
import com.dmitrymalkovich.android.githubanalytics.util.CursorRecyclerViewAdapter;

import static com.dmitrymalkovich.android.githubanalytics.data.source.local.contract.RepositoryContract.RepositoryEntry.*;

class DashboardListAdapter extends CursorRecyclerViewAdapter<DashboardListAdapter.ViewHolder> {

    private static final int VIEW_TYPE_THE_MOST_POPULAR = 0;
    private static final int VIEW_TYPE_POPULAR = 1;

    public static String LOG_TAG = DashboardListAdapter.class.getSimpleName();
    private final DashboardContract.View mView;

    DashboardListAdapter(Cursor cursor, DashboardContract.View view) {
        super(cursor);
        mView = view;
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
        if (position >= 0 && position <= 1) {
            return VIEW_TYPE_THE_MOST_POPULAR;
        } else {
            return VIEW_TYPE_POPULAR;
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onBindViewHolder(final ViewHolder holder, final Cursor cursor) {
        Context context = holder.itemView.getContext();
        // Most popular repository
        if (holder.getItemViewType() == VIEW_TYPE_THE_MOST_POPULAR) {
            // Clones
            String clonesCount = cursor.getString(COL_CLONES_COUNT);
            holder.clonesCountTodayView.setText(clonesCount != null ? clonesCount : "0");
            // Clones (Yesterday)
            String clonesCountYesterday = cursor.getString(COL_CLONES_UNIQUES_YESTERDAY);
            holder.clonesCountYesterdayView.setText(clonesCountYesterday != null ? clonesCountYesterday : "0");
            // Views
            String viewsUniques = cursor.getString(COL_VIEWS_UNIQUES);
            holder.viewsUniquesTodayView.setText(viewsUniques != null ? viewsUniques : "0");
            // Views (Yesterday)
            String viewsUniquesYesterday = cursor.getString(COL_VIEWS_UNIQUES_YESTERDAY);
            holder.viewsUniquesYesterdayView.setText(viewsUniquesYesterday != null ? viewsUniquesYesterday : "0");
            // Stars
            String stargazersToday = cursor.getString(COL_STARGAZERS_STARS);
            holder.starsTodayView.setText(stargazersToday != null ? stargazersToday : "0");
            // Stars (Yesterday)
            String stargazersYesterday = cursor.getString(COL_STARGAZERS_STARS_YESTERDAY);
            holder.starsYesterdayView.setText(stargazersYesterday != null ? stargazersYesterday : "0");
        }
        // Common
        holder.titleView.setText(cursor.getString(COL_REPOSITORY_NAME));
        holder.subtitleView.setText(cursor.getString(COL_REPOSITORY_DESCRIPTION));
        holder.starsTotalView.setText(cursor.getString(COL_REPOSITORY_WATCHERS));
        holder.totalForksView.setText(cursor.getString(COL_REPOSITORY_FORKS));
        String language = cursor.getString(COL_REPOSITORY_LANGUAGE);
        holder.languageView.setText(language);
        holder.languageIconView.setBackgroundDrawable(DrawableUtils.getColor(context, language));
        holder.languageIconView.setVisibility(holder.languageView.getText() != null
                && holder.languageView.getText().length() != 0
                ? View.VISIBLE : View.GONE);
        holder.languageView.setVisibility(holder.languageIconView.getVisibility() == View.VISIBLE
                ? View.VISIBLE : View.GONE);
        // Github button
        final String htmlUrl = cursor.getString(COL_REPOSITORY_HTML_URL);
        holder.githubView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mView.openUrl(htmlUrl);
            }
        });
        // Traffic button
        holder.trafficView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
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
        private final Button githubView;
        private final Button trafficView;
        private TextView clonesCountTodayView;
        private TextView viewsUniquesTodayView;
        private TextView starsTodayView;
        private TextView clonesCountYesterdayView;
        private TextView viewsUniquesYesterdayView;
        private TextView starsYesterdayView;
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
            githubView = (Button) view.findViewById(R.id.github);
            trafficView = (Button) view.findViewById(R.id.traffic);

            View todayView = view.findViewById(R.id.today);
            if (todayView != null) {
                clonesCountTodayView = (TextView) todayView.findViewById(R.id.clones_count);
                viewsUniquesTodayView = (TextView) todayView.findViewById(R.id.views_count);
                starsTodayView = (TextView) todayView.findViewById(R.id.stars_today);
            }

            View yesterdayView = view.findViewById(R.id.yesterday);
            if (yesterdayView != null) {
                clonesCountYesterdayView = (TextView) yesterdayView.findViewById(R.id.clones_count);
                viewsUniquesYesterdayView = (TextView) yesterdayView.findViewById(R.id.views_count);
                starsYesterdayView = (TextView) yesterdayView.findViewById(R.id.stars_today);
            }
        }
    }
}