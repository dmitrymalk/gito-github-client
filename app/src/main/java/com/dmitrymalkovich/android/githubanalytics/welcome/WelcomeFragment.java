package com.dmitrymalkovich.android.githubanalytics.welcome;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.dmitrymalkovich.android.githubanalytics.basicauthorization.BasicAuthorizationActivity;
import com.dmitrymalkovich.android.githubanalytics.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.google.common.base.Preconditions.checkNotNull;

public class WelcomeFragment extends Fragment implements WelcomeContract.View {

    private WelcomeContract.Presenter mPresenter;
    private Unbinder unbinder;
    @BindView(R.id.sign_in) Button mBasicSignIn;
    @BindView(R.id.sign_in_oauth) Button mOauthSignIn;

    public static WelcomeFragment newInstance() {
        return new WelcomeFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_welcome, container, false);
        unbinder = ButterKnife.bind(this, root);

        mBasicSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.signIn();
            }
        });

        mOauthSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.oauthSignIn();
            }
        });

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.start();
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
    public void startBasicAuthorizationActivity() {
        Intent intent = new Intent(getContext(), BasicAuthorizationActivity.class);
        startActivity(intent);
    }

    @Override
    public void startOAuthIntent(Uri uri) {
        Intent intent = new Intent(
                Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }
}
