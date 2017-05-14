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
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dmitrymalkovich.android.githubanalytics.R;
import com.dmitrymalkovich.android.githubanalytics.data.source.local.GithubLocalDataSource;

class BadgesAdapter extends RecyclerView.Adapter<BadgesAdapter.ViewHolder> {

    private final TrendingRepositoryContract.Presenter mPresenter;
    private String[] mLanguages = {GithubLocalDataSource.TrendingLanguage.JAVA,
            GithubLocalDataSource.TrendingLanguage.C,
            GithubLocalDataSource.TrendingLanguage.RUBY,
            GithubLocalDataSource.TrendingLanguage.JAVASCRIPT,
            GithubLocalDataSource.TrendingLanguage.SWIFT,
            GithubLocalDataSource.TrendingLanguage.OBJECTIVE_C,
            GithubLocalDataSource.TrendingLanguage.C_PLUS_PLUS,
            GithubLocalDataSource.TrendingLanguage.PYTHON,
            GithubLocalDataSource.TrendingLanguage.C_SHARP,
            GithubLocalDataSource.TrendingLanguage.HTML};

    BadgesAdapter(TrendingRepositoryContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public BadgesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_badge, parent, false);
        return new ViewHolder(view);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onBindViewHolder(final BadgesAdapter.ViewHolder holder, final int position) {
        Context context = holder.itemView.getContext();
        Resources resources = context.getResources();

        final String language = mLanguages[holder.getAdapterPosition()];
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPresenter.changeLanguage(language);
                notifyDataSetChanged();
            }
        });
        holder.badgeView.setText(language);

        int textColor = R.color.trending_badge_inactive_light;
        if (mPresenter.isLanguageSelected(language)) {
            textColor = R.color.trending_badge_active_light;
        }
        holder.badgeView.setTextColor(resources.getColor(textColor));

        holder.bottomLine.setVisibility(mPresenter.isLanguageSelected(language) ?
                View.VISIBLE : View.GONE);
    }

    @Override
    public int getItemCount() {
        return mLanguages.length;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView badgeView;
        private View bottomLine;

        ViewHolder(View view) {
            super(view);
            badgeView = (TextView) view.findViewById(R.id.badge_text);
            bottomLine = view.findViewById(R.id.badge_line);
        }
    }
}
