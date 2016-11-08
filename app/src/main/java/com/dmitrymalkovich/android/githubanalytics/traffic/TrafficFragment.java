package com.dmitrymalkovich.android.githubanalytics.traffic;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dmitrymalkovich.android.githubanalytics.R;
import com.dmitrymalkovich.android.githubanalytics.util.DrawableUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.dmitrymalkovich.android.githubanalytics.data.source.local.contract.RepositoryContract.RepositoryEntry.COL_CLONES_COUNT;
import static com.dmitrymalkovich.android.githubanalytics.data.source.local.contract.RepositoryContract.RepositoryEntry.COL_CLONES_UNIQUES_TWO_WEEKS;
import static com.dmitrymalkovich.android.githubanalytics.data.source.local.contract.RepositoryContract.RepositoryEntry.COL_CLONES_UNIQUES_YESTERDAY;
import static com.dmitrymalkovich.android.githubanalytics.data.source.local.contract.RepositoryContract.RepositoryEntry.COL_REPOSITORY_DESCRIPTION;
import static com.dmitrymalkovich.android.githubanalytics.data.source.local.contract.RepositoryContract.RepositoryEntry.COL_REPOSITORY_FORKS;
import static com.dmitrymalkovich.android.githubanalytics.data.source.local.contract.RepositoryContract.RepositoryEntry.COL_REPOSITORY_LANGUAGE;
import static com.dmitrymalkovich.android.githubanalytics.data.source.local.contract.RepositoryContract.RepositoryEntry.COL_REPOSITORY_NAME;
import static com.dmitrymalkovich.android.githubanalytics.data.source.local.contract.RepositoryContract.RepositoryEntry.COL_REPOSITORY_WATCHERS;
import static com.dmitrymalkovich.android.githubanalytics.data.source.local.contract.RepositoryContract.RepositoryEntry.COL_STARGAZERS_STARS;
import static com.dmitrymalkovich.android.githubanalytics.data.source.local.contract.RepositoryContract.RepositoryEntry.COL_STARGAZERS_STARS_TWO_WEEKS;
import static com.dmitrymalkovich.android.githubanalytics.data.source.local.contract.RepositoryContract.RepositoryEntry.COL_STARGAZERS_STARS_YESTERDAY;
import static com.dmitrymalkovich.android.githubanalytics.data.source.local.contract.RepositoryContract.RepositoryEntry.COL_VIEWS_UNIQUES;
import static com.dmitrymalkovich.android.githubanalytics.data.source.local.contract.RepositoryContract.RepositoryEntry.COL_VIEWS_UNIQUES_TWO_WEEKS;
import static com.dmitrymalkovich.android.githubanalytics.data.source.local.contract.RepositoryContract.RepositoryEntry.COL_VIEWS_UNIQUES_YESTERDAY;
import static com.google.common.base.Preconditions.checkNotNull;

public class TrafficFragment extends Fragment implements TrafficContract.View {

    public static String ARG_REPOSITORY_ID = "ARG_REPOSITORY_ID";
    private TrafficContract.Presenter mPresenter;
    private Unbinder unbinder;
    @BindView(R.id.progress) ProgressBar mProgressBar;

    public static TrafficFragment newInstance(long repositoryId) {
        TrafficFragment trafficFragment = new TrafficFragment();
        Bundle bundle = new Bundle();
        bundle.putLong(ARG_REPOSITORY_ID, repositoryId);
        trafficFragment.setArguments(bundle);
        return trafficFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_traffic, container, false);
        unbinder = ButterKnife.bind(this, root);

        if (getActivity() instanceof AppCompatActivity) {
            AppCompatActivity activity = (AppCompatActivity) getActivity();
            if (activity.getSupportActionBar() != null) {
                activity.getSupportActionBar().setTitle("");
            }
        }

        if (getArguments() != null && getArguments().containsKey(ARG_REPOSITORY_ID)) {
            long repositoryId = getArguments().getLong(ARG_REPOSITORY_ID);
            mPresenter.start(savedInstanceState, repositoryId);
        }
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void setPresenter(TrafficContract.Presenter presenter) {
        mPresenter = checkNotNull(presenter);
    }

    @Override
    public void setLoadingIndicator(boolean active) {
        if (mProgressBar != null) {
            mProgressBar.setVisibility(active ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    public void showRepository(Cursor cursor) {
        TextView titleView = (TextView) getView().findViewById(R.id.title);
        TextView subtitleView = (TextView) getView().findViewById(R.id.subtitle);
        TextView starsTotalView = (TextView) getView().findViewById(R.id.stars_total);
        TextView languageView = (TextView) getView().findViewById(R.id.language);
        ImageView languageIconView = (ImageView) getView().findViewById(R.id.language_icon);
        TextView totalForksView = (TextView) getView().findViewById(R.id.forks_total);

        View todayView = getView().findViewById(R.id.today);
        TextView clonesCountTodayView = (TextView) todayView.findViewById(R.id.clones_count);
        TextView viewsUniquesTodayView = (TextView) todayView.findViewById(R.id.views_count);
        TextView starsTodayView = (TextView) todayView.findViewById(R.id.stars_today);

        View yesterdayView = getView().findViewById(R.id.yesterday);
        TextView clonesCountYesterdayView = (TextView) yesterdayView.findViewById(R.id.clones_count);
        TextView viewsUniquesYesterdayView = (TextView) yesterdayView.findViewById(R.id.views_count);
        TextView starsYesterdayView = (TextView) yesterdayView.findViewById(R.id.stars_today);

        View twoWeeksView = getView().findViewById(R.id.two_weeks);
        TextView clonesCountTwoWeeksView = (TextView) twoWeeksView.findViewById(R.id.clones_count);
        TextView viewsUniquesTwoWeeksView = (TextView) twoWeeksView.findViewById(R.id.views_count);
        TextView starsTwoWeeksView = (TextView) twoWeeksView.findViewById(R.id.stars_today);

        // Most popular repository
        // Clones
        String clonesCount = cursor.getString(COL_CLONES_COUNT);
        clonesCountTodayView.setText(clonesCount != null ? clonesCount : "0");
        // Clones (Yesterday)
        String clonesCountYesterday = cursor.getString(COL_CLONES_UNIQUES_YESTERDAY);
        clonesCountYesterdayView.setText(clonesCountYesterday != null ? clonesCountYesterday : "0");
        // Clones (Two weeks)
        String clonesCountTwoWeeks = cursor.getString(COL_CLONES_UNIQUES_TWO_WEEKS);
        clonesCountTwoWeeksView.setText(clonesCountTwoWeeks != null ? clonesCountTwoWeeks : "0");
        // Views
        String viewsUniques = cursor.getString(COL_VIEWS_UNIQUES);
        viewsUniquesTodayView.setText(viewsUniques != null ? viewsUniques : "0");
        // Views (Yesterday)
        String viewsUniquesYesterday = cursor.getString(COL_VIEWS_UNIQUES_YESTERDAY);
        viewsUniquesYesterdayView.setText(viewsUniquesYesterday != null ? viewsUniquesYesterday : "0");
        // Views (Two weeks)
        String viewsUniquesTwoWeeks = cursor.getString(COL_VIEWS_UNIQUES_TWO_WEEKS);
        viewsUniquesTwoWeeksView.setText(viewsUniquesTwoWeeks != null ? viewsUniquesTwoWeeks : "0");
        // Stars
        String stargazersToday = cursor.getString(COL_STARGAZERS_STARS);
        starsTodayView.setText(stargazersToday != null ? stargazersToday : "0");
        // Stars (Yesterday)
        String stargazersYesterday = cursor.getString(COL_STARGAZERS_STARS_YESTERDAY);
        starsYesterdayView.setText(stargazersYesterday != null ? stargazersYesterday : "0");
        // Stars (Two weeks)
        String stargazersTwoWeeks = cursor.getString(COL_STARGAZERS_STARS_TWO_WEEKS);
        starsTwoWeeksView.setText(stargazersTwoWeeks != null ? stargazersTwoWeeks : "0");
        // Common for popular repository
        titleView.setText(cursor.getString(COL_REPOSITORY_NAME));
        subtitleView.setText(cursor.getString(COL_REPOSITORY_DESCRIPTION));
        starsTotalView.setText(cursor.getString(COL_REPOSITORY_WATCHERS));
        totalForksView.setText(cursor.getString(COL_REPOSITORY_FORKS));
        String language = cursor.getString(COL_REPOSITORY_LANGUAGE);
        languageView.setText(language);
        languageIconView.setBackgroundDrawable(DrawableUtils.getColor(getContext(), language));
        languageIconView.setVisibility(languageView.getText() != null
                && languageView.getText().length() != 0
                ? View.VISIBLE : View.GONE);
        languageView.setVisibility(languageIconView.getVisibility() == View.VISIBLE
                ? View.VISIBLE : View.GONE);
    }
}
