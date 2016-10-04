package com.dmitrymalkovich.android.githubanalytics.basicauthorization;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.dmitrymalkovich.android.githubanalytics.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.google.common.base.Preconditions.checkNotNull;

public class BasicAuthorizationFragment extends Fragment implements BasicAuthorizationContract.View {

    private BasicAuthorizationContract.Presenter mPresenter;
    private Unbinder unbinder;
    @BindView(R.id.email) AutoCompleteTextView mEmailView;
    @BindView(R.id.password) EditText mPasswordView;
    @BindView(R.id.register_link) TextView mRegisterLinkView;
    @BindView(R.id.email_sign_in_button) Button signInView;

    public static BasicAuthorizationFragment newInstance() {
        return new BasicAuthorizationFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_login, container, false);
        unbinder = ButterKnife.bind(this, root);

        // Links will be clickable
        mRegisterLinkView.setMovementMethod(LinkMovementMethod.getInstance());

        signInView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.login(mEmailView.getText().toString(),
                        mPasswordView.getText().toString());
            }
        });

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.start();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void setPresenter(BasicAuthorizationContract.Presenter presenter) {
        mPresenter = checkNotNull(presenter);
    }
}
