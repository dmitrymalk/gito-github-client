package com.dmitrymalkovich.android.githubanalytics.traffic;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.dmitrymalkovich.android.githubanalytics.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

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
}
