package com.dmitrymalkovich.android.githubanalytics.trendingrepository;

import android.content.Context;
import android.content.res.Resources;
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

class BadgesAdapter extends RecyclerView.Adapter<BadgesAdapter.ViewHolder> {

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

        int textColor = R.color.trending_badge_inactive;
        if (mPresenter.isLanguageSelected(language)) {
            textColor = R.color.trending_badge_active;
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
        TextView badgeView;
        View bottomLine;

        ViewHolder(View view) {
            super(view);
            badgeView = (TextView) view.findViewById(R.id.badge_text);
            bottomLine = view.findViewById(R.id.badge_line);
        }
    }
}
