package com.dmitrymalkovich.android.githubanalytics.trending;

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
    @BindView(R.id.progress) ProgressBar mProgressBar;
    @BindView(R.id.recycler_view_for_repositories)
    RecyclerView mRecyclerView;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.bottom_navigation) AHBottomNavigation mBottomNavigation;
    @BindView(R.id.empty_state) View mEmptyState;
    View mCoordinatorLayout;

    private TrendingRepositoryListAdapter mAdapter;
    @BindView(R.id.recycler_view_for_badges) RecyclerView mRecyclerViewForBadges;

    public static TrendingRepositoryFragment newInstance() {
        return new TrendingRepositoryFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
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
            mCoordinatorLayout = activity.findViewById(R.id.coordinator_layout);
            setUpBadges();
        }

        setUpBottomNavigationBar();

        mPresenter.start(savedInstanceState, repositoryId);
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
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
            if (active) {
                setRefreshIndicator(false);
                setEmptyState(false);
            }
        }
    }

    @Override
    public void setRefreshIndicator(boolean active) {
        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.setRefreshing(active);
            if (active) {
                setLoadingIndicator(false);
                setEmptyState(false);
            }
        }
    }

    @Override
    public void setEmptyState(boolean active) {
        if (mEmptyState != null && !mSwipeRefreshLayout.isRefreshing()
                && mProgressBar.getVisibility() != View.VISIBLE && active) {
            mEmptyState.setVisibility(View.VISIBLE);
        } else if (mEmptyState != null) {
            mEmptyState.setVisibility(View.GONE);
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

    @SuppressWarnings("deprecation")
    private void setUpBottomNavigationBar() {
        int icon = R.drawable.ic_trending_up_black_24dp;
        int backgroundColor = R.color.trending_bottom_bar_background;
        int colorActive = R.color.trending_bottom_bar_active;
        int colorInActive = R.color.trending_bottom_bar_inactive;

        AHBottomNavigationItem daily = new AHBottomNavigationItem(R.string.trending_daily,
                icon, backgroundColor);
        AHBottomNavigationItem weekly = new AHBottomNavigationItem(R.string.trending_weekly,
                icon, backgroundColor);
        AHBottomNavigationItem monthly = new AHBottomNavigationItem(R.string.trending_monthly,
                icon, backgroundColor);

        mBottomNavigation.setVisibility(View.VISIBLE);

        mBottomNavigation.removeAllItems();

        mBottomNavigation.addItem(daily);
        mBottomNavigation.addItem(weekly);
        mBottomNavigation.addItem(monthly);

        mBottomNavigation.setBehaviorTranslationEnabled(true);

        mBottomNavigation.setColoredModeColors(getResources().getColor(colorActive),
                getResources().getColor(colorInActive));
        mBottomNavigation.setColored(true);

        mBottomNavigation.setOnTabSelectedListener(new AHBottomNavigation.OnTabSelectedListener() {
            @Override
            public boolean onTabSelected(int position, boolean wasSelected) {
                mPresenter.onTabSelected(position);
                return true;
            }
        });
    }

    private void setUpBadges() {
        mRecyclerViewForBadges.setVisibility(View.VISIBLE);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRecyclerViewForBadges.setLayoutManager(linearLayoutManager);

        mRecyclerViewForBadges.setAdapter(new BadgesAdapter(mPresenter));
    }
}
