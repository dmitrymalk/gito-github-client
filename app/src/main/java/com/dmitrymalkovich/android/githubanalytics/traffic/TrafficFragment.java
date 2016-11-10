package com.dmitrymalkovich.android.githubanalytics.traffic;

import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dmitrymalkovich.android.githubanalytics.R;
import com.dmitrymalkovich.android.githubanalytics.data.source.local.contract.ClonesContract;
import com.dmitrymalkovich.android.githubanalytics.data.source.local.contract.RepositoryContract;
import com.dmitrymalkovich.android.githubanalytics.data.source.local.contract.ViewsContract;
import com.dmitrymalkovich.android.githubanalytics.util.DrawableUtils;
import com.dmitrymalkovich.android.githubanalytics.util.TimeUtils;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.AxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.google.common.base.Preconditions.checkNotNull;

public class TrafficFragment extends Fragment implements TrafficContract.View {
    @SuppressWarnings("unused")
    private static final String LOG_TAG = TrafficFragment.class.getSimpleName();
    public static String ARG_REPOSITORY_ID = "ARG_REPOSITORY_ID";
    private TrafficContract.Presenter mPresenter;
    private Unbinder unbinder;
    @BindView(R.id.progress)
    ProgressBar mProgressBar;
    @BindView(R.id.empty_state)
    View mEmptyState;
    @BindView(R.id.recycler_view_for_referrers)
    RecyclerView mRecyclerView;
    private ReferrersListAdapter mAdapter;
    @BindView(R.id.chart_clones)
    LineChart mChartClones;
    @BindView(R.id.chart_views)
    LineChart mChartViews;

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

        int columnCount = getResources().getInteger(R.integer.grid_column_count);
        StaggeredGridLayoutManager staggeredGridLayoutManager =
                new StaggeredGridLayoutManager(columnCount, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(staggeredGridLayoutManager);

        mAdapter = new ReferrersListAdapter(null, this);
        mAdapter.setHasStableIds(true);
        mRecyclerView.setAdapter(mAdapter);

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
    public void setEmptyState(boolean active) {
        if (mEmptyState != null) {
            mEmptyState.setVisibility(active ? View.VISIBLE : View.GONE);
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public void showRepository(Cursor cursor) {

        if (getView() == null) {
            return;
        }

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

        // Clones
        String clonesCount = cursor.getString(RepositoryContract.RepositoryEntry.COL_CLONES_COUNT);
        clonesCountTodayView.setText(clonesCount != null ? clonesCount : "0");
        // Clones (Yesterday)
        String clonesCountYesterday = cursor.getString(RepositoryContract.RepositoryEntry.COL_CLONES_UNIQUES_YESTERDAY);
        clonesCountYesterdayView.setText(clonesCountYesterday != null ? clonesCountYesterday : "0");
        // Clones (Two weeks)
        String clonesCountTwoWeeks = cursor.getString(RepositoryContract.RepositoryEntry.COL_CLONES_UNIQUES_TWO_WEEKS);
        clonesCountTwoWeeksView.setText(clonesCountTwoWeeks != null ? clonesCountTwoWeeks : "0");
        // Views
        String viewsUniques = cursor.getString(RepositoryContract.RepositoryEntry.COL_VIEWS_UNIQUES);
        viewsUniquesTodayView.setText(viewsUniques != null ? viewsUniques : "0");
        // Views (Yesterday)
        String viewsUniquesYesterday = cursor.getString(RepositoryContract.RepositoryEntry.COL_VIEWS_UNIQUES_YESTERDAY);
        viewsUniquesYesterdayView.setText(viewsUniquesYesterday != null ? viewsUniquesYesterday : "0");
        // Views (Two weeks)
        String viewsUniquesTwoWeeks = cursor.getString(RepositoryContract.RepositoryEntry.COL_VIEWS_UNIQUES_TWO_WEEKS);
        viewsUniquesTwoWeeksView.setText(viewsUniquesTwoWeeks != null ? viewsUniquesTwoWeeks : "0");
        // Stars
        String stargazersToday = cursor.getString(RepositoryContract.RepositoryEntry.COL_STARGAZERS_STARS);
        starsTodayView.setText(stargazersToday != null ? stargazersToday : "0");
        // Stars (Yesterday)
        String stargazersYesterday = cursor.getString(RepositoryContract.RepositoryEntry.COL_STARGAZERS_STARS_YESTERDAY);
        starsYesterdayView.setText(stargazersYesterday != null ? stargazersYesterday : "0");
        // Stars (Two weeks)
        String stargazersTwoWeeks = cursor.getString(RepositoryContract.RepositoryEntry.COL_STARGAZERS_STARS_TWO_WEEKS);
        starsTwoWeeksView.setText(stargazersTwoWeeks != null ? stargazersTwoWeeks : "0");
        // Common for popular repository
        titleView.setText(cursor.getString(RepositoryContract.RepositoryEntry.COL_REPOSITORY_NAME));
        subtitleView.setText(cursor.getString(RepositoryContract.RepositoryEntry.COL_REPOSITORY_DESCRIPTION));
        starsTotalView.setText(cursor.getString(RepositoryContract.RepositoryEntry.COL_REPOSITORY_WATCHERS));
        totalForksView.setText(cursor.getString(RepositoryContract.RepositoryEntry.COL_REPOSITORY_FORKS));
        String language = cursor.getString(RepositoryContract.RepositoryEntry.COL_REPOSITORY_LANGUAGE);
        languageView.setText(language);
        languageIconView.setBackgroundDrawable(DrawableUtils.getColor(getContext(), language));
        languageIconView.setVisibility(languageView.getText() != null
                && languageView.getText().length() != 0
                ? View.VISIBLE : View.GONE);
        languageView.setVisibility(languageIconView.getVisibility() == View.VISIBLE
                ? View.VISIBLE : View.GONE);
    }

    @Override
    public void showReferrers(Cursor data) {
        mAdapter.swapCursor(data);
    }

    @Override
    public void showClones(Cursor data) {
        List<Entry> values = new ArrayList<>();
        if (data.moveToFirst()) {
            do {
                int clones = data.getInt(ClonesContract.ClonesEntry.COL_CLONES_UNIQUES);
                long timestamp = data.getLong(ClonesContract.ClonesEntry.COL_CLONES_TIMESTAMP);
                values.add(new Entry(timestamp, clones));
            } while (data.moveToNext());
        }

        LineDataSet set1 = new LineDataSet(values, "Uniques clones");
        set1.setAxisDependency(YAxis.AxisDependency.LEFT);
        set1.setColor(Color.BLACK);
        set1.setValueTextColor(Color.BLACK);
        set1.setLineWidth(1.5f);
        set1.setDrawCircles(false);
        set1.setDrawValues(false);
        set1.setFillAlpha(65);
        set1.setFillColor(Color.BLACK);
        set1.setHighLightColor(Color.rgb(244, 117, 117));
        set1.setDrawCircleHole(false);
        LineData lineData = new LineData(set1);


        // enable touch gestures
        mChartClones.setTouchEnabled(false);

        // get the legend (only possible after setting data)
        Legend l = mChartClones.getLegend();
        l.setEnabled(false);

        XAxis xAxis = mChartClones.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.TOP_INSIDE);
        xAxis.setTextSize(10f);
        xAxis.setTextColor(Color.BLACK);
        xAxis.setDrawAxisLine(false);
        xAxis.setDrawGridLines(true);
        xAxis.setTextColor(Color.BLACK);
        xAxis.setCenterAxisLabels(true);
        xAxis.setGranularity(1f); // one hour
        xAxis.setValueFormatter(new AxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return TimeUtils.humanReadable((long) value);
            }

            @Override
            public int getDecimalDigits() {
                return 0;
            }
        });

        YAxis leftAxis = mChartClones.getAxisLeft();
        leftAxis.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
        leftAxis.setTextColor(ColorTemplate.getHoloBlue());
        leftAxis.setDrawGridLines(true);
        leftAxis.setGranularityEnabled(true);
        leftAxis.setYOffset(-9f);
        leftAxis.setTextColor(Color.BLACK);

        YAxis rightAxis = mChartClones.getAxisRight();
        rightAxis.setEnabled(false);

        mChartClones.setData(lineData);
    }

    @Override
    public void showViews(Cursor data) {
        List<Entry> values = new ArrayList<>();
        if (data.moveToFirst()) {
            do {
                int views = data.getInt(ViewsContract.ViewsEntry.COL_VIEWS_UNIQUES);
                long timestamp = data.getLong(ViewsContract.ViewsEntry.COL_VIEWS_TIMESTAMP);
                values.add(new Entry(timestamp, views));
            } while (data.moveToNext());
        }

        LineDataSet set1 = new LineDataSet(values, "Visitors");
        set1.setAxisDependency(YAxis.AxisDependency.LEFT);
        set1.setColor(Color.BLACK);
        set1.setValueTextColor(Color.BLACK);
        set1.setLineWidth(1.5f);
        set1.setDrawCircles(false);
        set1.setDrawValues(false);
        set1.setFillAlpha(65);
        set1.setFillColor(Color.BLACK);
        set1.setHighLightColor(Color.rgb(244, 117, 117));
        set1.setDrawCircleHole(false);
        LineData lineData = new LineData(set1);


        // enable touch gestures
        mChartViews.setTouchEnabled(false);

        // get the legend (only possible after setting data)
        Legend l = mChartViews.getLegend();
        l.setEnabled(false);

        XAxis xAxis = mChartViews.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.TOP_INSIDE);
        xAxis.setTextSize(10f);
        xAxis.setTextColor(Color.BLACK);
        xAxis.setDrawAxisLine(false);
        xAxis.setDrawGridLines(false);
        xAxis.setTextColor(Color.BLACK);
        xAxis.setCenterAxisLabels(false);
        xAxis.setValueFormatter(new AxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return TimeUtils.humanReadable((long) value);
            }

            @Override
            public int getDecimalDigits() {
                return 0;
            }
        });

        YAxis leftAxis = mChartViews.getAxisLeft();
        leftAxis.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
        leftAxis.setTextColor(ColorTemplate.getHoloBlue());
        leftAxis.setDrawGridLines(true);
        leftAxis.setGranularityEnabled(true);
        leftAxis.setYOffset(-9f);
        leftAxis.setTextColor(Color.BLACK);

        YAxis rightAxis = mChartViews.getAxisRight();
        rightAxis.setEnabled(false);

        mChartViews.setData(lineData);

    }
}
