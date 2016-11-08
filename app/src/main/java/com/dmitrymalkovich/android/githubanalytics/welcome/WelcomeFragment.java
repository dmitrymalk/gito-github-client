package com.dmitrymalkovich.android.githubanalytics.welcome;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;

import com.dmitrymalkovich.android.githubanalytics.R;
import com.dmitrymalkovich.android.githubanalytics.navigation.NavigationViewActivity;
import com.google.firebase.crash.FirebaseCrash;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.google.common.base.Preconditions.checkNotNull;

public class WelcomeFragment extends Fragment implements WelcomeContract.View {

    public static String LOG_TAG = WelcomeFragment.class.getSimpleName();
    private WelcomeContract.Presenter mPresenter;
    private Unbinder unbinder;
    @BindView(R.id.sign_in_oauth)
    Button mOauthSignInButton;
    @BindView(R.id.progress)
    ProgressBar mProgressBar;
    @BindView(R.id.content)
    View mContentView;

    public static WelcomeFragment newInstance() {
        return new WelcomeFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_welcome, container, false);
        unbinder = ButterKnife.bind(this, root);

        mOauthSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.oauthSignIn();
            }
        });

        return root;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPresenter.start(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.handleIntent(getActivity().getIntent());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void setPresenter(WelcomeContract.Presenter presenter) {
        mPresenter = checkNotNull(presenter);
    }

    @Override
    public void startOAuthIntent(Uri uri) {
        Intent intent = new Intent(
                Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    @Override
    public void startDashboard() {
        if (getActivity() != null) {
            // Hide progress bar
            mProgressBar.setVisibility(View.GONE);
            // Start dashboard
            Intent intent = new Intent(getContext(), NavigationViewActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            // Finish current activity
            getActivity().finish();
        } else {
            FirebaseCrash.log("Dashboard cannot be started, activity is null");
        }
    }

    @Override
    public void setLoadingIndicator(boolean active) {
        if (mProgressBar != null && mContentView != null) {
            mContentView.setVisibility(!active ? View.VISIBLE : View.GONE);
            mProgressBar.setVisibility(active ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    public void authorizationFailed() {
        Log.i(LOG_TAG, "Authorization failed");
        // TODO : Show error dialog to the user
    }
}
