package com.dmitrymalkovich.android.githubanalytics.trendingrepository;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.dmitrymalkovich.android.githubanalytics.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.google.common.base.Preconditions.checkNotNull;

public class TrendingRepositoryFragment extends Fragment implements TrendingRepositoryContract.View {

    private TrendingRepositoryContract.Presenter mPresenter;
    private Unbinder unbinder;
    ProgressBar mProgressBar;
    @BindView(R.id.recycler_view_for_repositories) RecyclerView mRecyclerView;
    @BindView(R.id.swipe_refresh_layout) SwipeRefreshLayout mSwipeRefreshLayout;
    AHBottomNavigation mBottomNavigation;

    private TrendingRepositoryListAdapter mAdapter;

    public static TrendingRepositoryFragment newInstance() {
        return new TrendingRepositoryFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_trending, container, false);
        unbinder = ButterKnife.bind(this, root);

        int columnCount = getResources().getInteger(R.integer.grid_column_count);
        StaggeredGridLayoutManager staggeredGridLayoutManager =
                new StaggeredGridLayoutManager(columnCount, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(staggeredGridLayoutManager);

        mAdapter = new TrendingRepositoryListAdapter(null, this);
        mAdapter.setHasStableIds(true);
        mRecyclerView.setAdapter(mAdapter);

        if (getActivity() instanceof AppCompatActivity) {
            AppCompatActivity activity = (AppCompatActivity) getActivity();
            if (activity.getSupportActionBar() != null) {
                activity.getSupportActionBar().setTitle(mPresenter.getTitle(getContext()));
            }

            mProgressBar = (ProgressBar) getActivity().findViewById(R.id.progress);
            mBottomNavigation = (AHBottomNavigation) getActivity().findViewById(R.id.bottom_navigation);
            getActivity().findViewById(R.id.toolbar_logo).setVisibility(View.GONE);
            getActivity().findViewById(R.id.bottom_navigation)
                    .setVisibility(View.VISIBLE);

            RecyclerView mRecyclerViewForBadges = (RecyclerView) getActivity()
                    .findViewById(R.id.recycler_view_for_badges);
            mRecyclerViewForBadges.setVisibility(View.VISIBLE);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
            linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            mRecyclerViewForBadges.setLayoutManager(linearLayoutManager);
            mRecyclerViewForBadges.setAdapter(new BadgesAdapter(mPresenter));
        }
        // Set up bottom navigation
        AHBottomNavigationItem daily = new AHBottomNavigationItem(R.string.trending_daily,
                R.drawable.ic_trending_up_black_24dp, R.color.blue);
        AHBottomNavigationItem weekly = new AHBottomNavigationItem(R.string.trending_weekly,
                R.drawable.ic_trending_up_black_24dp, R.color.blue);
        AHBottomNavigationItem monthly = new AHBottomNavigationItem(R.string.trending_monthly,
                R.drawable.ic_trending_up_black_24dp, R.color.blue);
        mBottomNavigation.removeAllItems();
        mBottomNavigation.addItem(daily);
        mBottomNavigation.addItem(weekly);
        mBottomNavigation.addItem(monthly);
        mBottomNavigation.setAccentColor(R.color.blue);
        mBottomNavigation.setColored(true);
        mBottomNavigation.setOnTabSelectedListener(new AHBottomNavigation.OnTabSelectedListener() {
            @Override
            public boolean onTabSelected(int position, boolean wasSelected) {
                mPresenter.onTabSelected(position);
                return true;
            }
        });
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.start();
        mSwipeRefreshLayout.setOnRefreshListener(mPresenter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void setPresenter(TrendingRepositoryContract.Presenter presenter) {
        mPresenter = checkNotNull(presenter);
    }

    @Override
    public void selectTab(int position) {
        mBottomNavigation.setCurrentItem(position);
    }

    @Override
    public void setLoadingIndicator(boolean active) {
        if (mProgressBar != null) {
            mProgressBar.setVisibility(active ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    public void setRefreshIndicator(boolean active) {
        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.setRefreshing(active);
        }
    }

    @Override
    public void showRepositories(Cursor data) {
        this.mAdapter.swapCursor(data);
    }

    @Override
    public void openUrl(@NonNull String htmlUrl) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(htmlUrl));
        getActivity().startActivity(browserIntent);
    }
}
