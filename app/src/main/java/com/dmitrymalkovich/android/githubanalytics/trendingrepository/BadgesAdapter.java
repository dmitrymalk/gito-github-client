package com.dmitrymalkovich.android.githubanalytics.trendingrepository;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dmitrymalkovich.android.githubanalytics.R;

import static com.dmitrymalkovich.android.githubanalytics.data.source.local.GithubLocalDataSource.TRENDING_LANGUAGE_C;
import static com.dmitrymalkovich.android.githubanalytics.data.source.local.GithubLocalDataSource.TRENDING_LANGUAGE_C_PLUS_PLUS;
import static com.dmitrymalkovich.android.githubanalytics.data.source.local.GithubLocalDataSource.TRENDING_LANGUAGE_C_SHARP;
import static com.dmitrymalkovich.android.githubanalytics.data.source.local.GithubLocalDataSource.TRENDING_LANGUAGE_HTML;
import static com.dmitrymalkovich.android.githubanalytics.data.source.local.GithubLocalDataSource.TRENDING_LANGUAGE_JAVA;
import static com.dmitrymalkovich.android.githubanalytics.data.source.local.GithubLocalDataSource.TRENDING_LANGUAGE_JAVASCRIPT;
import static com.dmitrymalkovich.android.githubanalytics.data.source.local.GithubLocalDataSource.TRENDING_LANGUAGE_OBJECTIVE_C;
import static com.dmitrymalkovich.android.githubanalytics.data.source.local.GithubLocalDataSource.TRENDING_LANGUAGE_PYTHON;
import static com.dmitrymalkovich.android.githubanalytics.data.source.local.GithubLocalDataSource.TRENDING_LANGUAGE_RUBY;
import static com.dmitrymalkovich.android.githubanalytics.data.source.local.GithubLocalDataSource.TRENDING_LANGUAGE_SWIFT;

class BadgesAdapter extends RecyclerView.Adapter<BadgesAdapter.ViewHolder>{

    private final TrendingRepositoryContract.Presenter mPresenter;
    private String[] mLanguages = {TRENDING_LANGUAGE_JAVA,
            TRENDING_LANGUAGE_C,
            TRENDING_LANGUAGE_RUBY,
            TRENDING_LANGUAGE_JAVASCRIPT,
            TRENDING_LANGUAGE_SWIFT,
            TRENDING_LANGUAGE_OBJECTIVE_C,
            TRENDING_LANGUAGE_C_PLUS_PLUS,
            TRENDING_LANGUAGE_PYTHON,
            TRENDING_LANGUAGE_C_SHARP,
            TRENDING_LANGUAGE_HTML};

    BadgesAdapter(TrendingRepositoryContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public BadgesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_badge, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final BadgesAdapter.ViewHolder holder, final int position) {
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPresenter.changeLanguage(mLanguages[holder.getAdapterPosition()]);
            }
        });
        holder.badgeView.setText(mLanguages[position]);
    }

    @Override
    public int getItemCount() {
        return mLanguages.length;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView badgeView;

        ViewHolder(View view) {
            super(view);
            badgeView = (TextView) view.findViewById(R.id.badge_text);
        }
    }
}
