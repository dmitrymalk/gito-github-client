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
import com.dmitrymalkovich.android.githubanalytics.CursorRecyclerViewAdapter;
import com.dmitrymalkovich.android.githubanalytics.Utils;

import static com.dmitrymalkovich.android.githubanalytics.data.source.local.contract.RepositoryContract.RepositoryEntry.COL_CLONES_COUNT;
import static com.dmitrymalkovich.android.githubanalytics.data.source.local.contract.RepositoryContract.RepositoryEntry.COL_CLONES_UNIQUES_TWO_WEEKS;
import static com.dmitrymalkovich.android.githubanalytics.data.source.local.contract.RepositoryContract.RepositoryEntry.COL_CLONES_UNIQUES_YESTERDAY;
import static com.dmitrymalkovich.android.githubanalytics.data.source.local.contract.RepositoryContract.RepositoryEntry.COL_REFERRER_1_PATHS_COUNT;
import static com.dmitrymalkovich.android.githubanalytics.data.source.local.contract.RepositoryContract.RepositoryEntry.COL_REFERRER_1_PATHS_REFERRER;
import static com.dmitrymalkovich.android.githubanalytics.data.source.local.contract.RepositoryContract.RepositoryEntry.COL_REFERRER_1_PATHS_UNIQUES;
import static com.dmitrymalkovich.android.githubanalytics.data.source.local.contract.RepositoryContract.RepositoryEntry.COL_REFERRER_2_PATHS_COUNT;
import static com.dmitrymalkovich.android.githubanalytics.data.source.local.contract.RepositoryContract.RepositoryEntry.COL_REFERRER_2_PATHS_REFERRER;
import static com.dmitrymalkovich.android.githubanalytics.data.source.local.contract.RepositoryContract.RepositoryEntry.COL_REFERRER_2_PATHS_UNIQUES;
import static com.dmitrymalkovich.android.githubanalytics.data.source.local.contract.RepositoryContract.RepositoryEntry.COL_REPOSITORY_DESCRIPTION;
import static com.dmitrymalkovich.android.githubanalytics.data.source.local.contract.RepositoryContract.RepositoryEntry.COL_REPOSITORY_FORKS;
import static com.dmitrymalkovich.android.githubanalytics.data.source.local.contract.RepositoryContract.RepositoryEntry.COL_REPOSITORY_HTML_URL;
import static com.dmitrymalkovich.android.githubanalytics.data.source.local.contract.RepositoryContract.RepositoryEntry.COL_REPOSITORY_ID;
import static com.dmitrymalkovich.android.githubanalytics.data.source.local.contract.RepositoryContract.RepositoryEntry.COL_REPOSITORY_LANGUAGE;
import static com.dmitrymalkovich.android.githubanalytics.data.source.local.contract.RepositoryContract.RepositoryEntry.COL_REPOSITORY_NAME;
import static com.dmitrymalkovich.android.githubanalytics.data.source.local.contract.RepositoryContract.RepositoryEntry.COL_REPOSITORY_WATCHERS;
import static com.dmitrymalkovich.android.githubanalytics.data.source.local.contract.RepositoryContract.RepositoryEntry.COL_STARGAZERS_STARS;
import static com.dmitrymalkovich.android.githubanalytics.data.source.local.contract.RepositoryContract.RepositoryEntry.COL_STARGAZERS_STARS_TWO_WEEKS;
import static com.dmitrymalkovich.android.githubanalytics.data.source.local.contract.RepositoryContract.RepositoryEntry.COL_STARGAZERS_STARS_YESTERDAY;
import static com.dmitrymalkovich.android.githubanalytics.data.source.local.contract.RepositoryContract.RepositoryEntry.COL_VIEWS_UNIQUES;
import static com.dmitrymalkovich.android.githubanalytics.data.source.local.contract.RepositoryContract.RepositoryEntry.COL_VIEWS_UNIQUES_TWO_WEEKS;
import static com.dmitrymalkovich.android.githubanalytics.data.source.local.contract.RepositoryContract.RepositoryEntry.COL_VIEWS_UNIQUES_YESTERDAY;

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
                        // Nothing to do
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
                        // Nothing to do
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

        if (holder.getItemViewType() == VIEW_TYPE_THE_MOST_POPULAR) {
            setMostPopularRepository(holder, cursor);
        }
        // Common for popular repository
        holder.titleView.setText(cursor.getString(COL_REPOSITORY_NAME));
        holder.subtitleView.setText(cursor.getString(COL_REPOSITORY_DESCRIPTION));
        holder.starsTotalView.setText(cursor.getString(COL_REPOSITORY_WATCHERS));
        holder.totalForksView.setText(cursor.getString(COL_REPOSITORY_FORKS));
        String language = cursor.getString(COL_REPOSITORY_LANGUAGE);
        holder.languageView.setText(language);
        holder.languageIconView.setBackgroundDrawable(Utils.getColor(context, language));
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
        final long repositoryId = cursor.getLong(COL_REPOSITORY_ID);
        holder.trafficView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mView.showTraffic(repositoryId);
            }
        });
    }

    private void setMostPopularRepository(ViewHolder holder, Cursor cursor) {
        // Clones
        String clonesCount = cursor.getString(COL_CLONES_COUNT);
        holder.clonesCountTodayView.setText(clonesCount != null ? clonesCount : "0");
        // Clones (Yesterday)
        String clonesCountYesterday = cursor.getString(COL_CLONES_UNIQUES_YESTERDAY);
        holder.clonesCountYesterdayView.setText(clonesCountYesterday != null ? clonesCountYesterday : "0");
        // Clones (Two weeks)
        String clonesCountTwoWeeks = cursor.getString(COL_CLONES_UNIQUES_TWO_WEEKS);
        holder.clonesCountTwoWeeksView.setText(clonesCountTwoWeeks != null ? clonesCountTwoWeeks : "0");
        // Views
        String viewsUniques = cursor.getString(COL_VIEWS_UNIQUES);
        holder.viewsUniquesTodayView.setText(viewsUniques != null ? viewsUniques : "0");
        // Views (Yesterday)
        String viewsUniquesYesterday = cursor.getString(COL_VIEWS_UNIQUES_YESTERDAY);
        holder.viewsUniquesYesterdayView.setText(viewsUniquesYesterday != null ? viewsUniquesYesterday : "0");
        // Views (Two weeks)
        String viewsUniquesTwoWeeks = cursor.getString(COL_VIEWS_UNIQUES_TWO_WEEKS);
        holder.viewsUniquesTwoWeeksView.setText(viewsUniquesTwoWeeks != null ? viewsUniquesTwoWeeks : "0");
        // Stars
        String stargazersToday = cursor.getString(COL_STARGAZERS_STARS);
        holder.starsTodayView.setText(stargazersToday != null ? stargazersToday : "0");
        // Stars (Yesterday)
        String stargazersYesterday = cursor.getString(COL_STARGAZERS_STARS_YESTERDAY);
        holder.starsYesterdayView.setText(stargazersYesterday != null ? stargazersYesterday : "0");
        // Stars (Two weeks)
        String stargazersTwoWeeks = cursor.getString(COL_STARGAZERS_STARS_TWO_WEEKS);
        holder.starsTwoWeeksView.setText(stargazersTwoWeeks != null ? stargazersTwoWeeks : "0");

        setTopReferrer(holder, cursor);
    }

    private void setTopReferrer(ViewHolder holder, Cursor cursor) {
        String topReferrer1 = cursor.getString(COL_REFERRER_1_PATHS_REFERRER);
        String topReferrer2 = cursor.getString(COL_REFERRER_2_PATHS_REFERRER);

        if (topReferrer2 != null && topReferrer1 != null) {

            String topReferrer1Views = cursor.getString(COL_REFERRER_1_PATHS_COUNT);
            String topReferrer1Visitors = cursor.getString(COL_REFERRER_1_PATHS_UNIQUES);
            String topReferrer2Views = cursor.getString(COL_REFERRER_2_PATHS_COUNT);
            String topReferrer2Visitors = cursor.getString(COL_REFERRER_2_PATHS_UNIQUES);

            holder.topReferrer1Name.setText(topReferrer1);
            holder.topReferrer1Views.setText(topReferrer1Views);
            holder.topReferrer1Visitors.setText(topReferrer1Visitors);

            holder.topReferrer2Name.setText(topReferrer2);
            holder.topReferrer2Views.setText(topReferrer2Views);
            holder.topReferrer2Visitors.setText(topReferrer2Visitors);

            holder.mTopReferrerDivider.setVisibility(View.VISIBLE);
            holder.mTopReferrerSites.setVisibility(View.VISIBLE);

        } else {

            holder.mTopReferrerDivider.setVisibility(View.GONE);
            holder.mTopReferrerSites.setVisibility(View.GONE);

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
        private final Button githubView;
        private final Button trafficView;
        private final View mTopReferrer1;
        private final View mTopReferrerDivider;
        private final View mTopReferrerSites;
        private final View mTopReferrer2;
        private TextView topReferrer1Name;
        private TextView topReferrer1Views;
        private TextView topReferrer1Visitors;
        private TextView topReferrer2Name;
        private TextView topReferrer2Views;
        private TextView topReferrer2Visitors;
        private TextView clonesCountTwoWeeksView;
        private TextView viewsUniquesTwoWeeksView;
        private TextView starsTwoWeeksView;
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
            githubView = (Button) view.findViewById(R.id.open_with_github);
            trafficView = (Button) view.findViewById(R.id.traffic);

            View todayView = view.findViewById(R.id.today);
            if (todayView != null) {
                clonesCountTodayView = (TextView) todayView.findViewById(R.id.clones_uniques);
                viewsUniquesTodayView = (TextView) todayView.findViewById(R.id.views_uniques);
                starsTodayView = (TextView) todayView.findViewById(R.id.stars_today);
            }

            View yesterdayView = view.findViewById(R.id.yesterday);
            if (yesterdayView != null) {
                clonesCountYesterdayView = (TextView) yesterdayView.findViewById(R.id.clones_uniques);
                viewsUniquesYesterdayView = (TextView) yesterdayView.findViewById(R.id.views_uniques);
                starsYesterdayView = (TextView) yesterdayView.findViewById(R.id.stars_today);
            }

            View twoWeeksView = view.findViewById(R.id.two_weeks);
            if (yesterdayView != null) {
                clonesCountTwoWeeksView = (TextView) twoWeeksView.findViewById(R.id.clones_uniques);
                viewsUniquesTwoWeeksView = (TextView) twoWeeksView.findViewById(R.id.views_uniques);
                starsTwoWeeksView = (TextView) twoWeeksView.findViewById(R.id.stars_today);
            }

            mTopReferrer1 = view.findViewById(R.id.top_referrer_1);
            if (mTopReferrer1 != null) {
                topReferrer1Name = (TextView) mTopReferrer1.findViewById(R.id.name);
                topReferrer1Views = (TextView) mTopReferrer1.findViewById(R.id.views);
                topReferrer1Visitors = (TextView) mTopReferrer1.findViewById(R.id.visitors);
            }

            mTopReferrer2 = view.findViewById(R.id.top_referrer_2);
            if (mTopReferrer2 != null) {
                topReferrer2Name = (TextView) mTopReferrer2.findViewById(R.id.name);
                topReferrer2Views = (TextView) mTopReferrer2.findViewById(R.id.views);
                topReferrer2Visitors = (TextView) mTopReferrer2.findViewById(R.id.visitors);
            }

            mTopReferrerDivider = view.findViewById(R.id.top_referrer_divider);
            mTopReferrerSites = view.findViewById(R.id.top_referrer_sites);
        }
    }
}