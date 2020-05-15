/*
 * Copyright (C) 2017 MINDORKS NEXTGEN PRIVATE LIMITED
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://mindorks.com/license/apache-v2
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.edwiinn.project.ui.login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.edwiinn.project.R;
import com.edwiinn.project.ui.base.BaseActivity;
import com.edwiinn.project.ui.documents.DocumentsActivity;
import com.edwiinn.project.ui.main.MainActivity;
import com.edwiinn.project.ui.splash.SplashActivity;

import net.openid.appauth.AuthorizationException;
import net.openid.appauth.AuthorizationResponse;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * Created by janisharali on 27/01/17.
 */

public class LoginActivity extends BaseActivity implements LoginMvpView {

    private static final String USED_INTENT = "used_intent";

    @Inject
    LoginMvpPresenter<LoginMvpView> mPresenter;

    public static Intent getStartIntent(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getActivityComponent().inject(this);

        setUnBinder(ButterKnife.bind(this));

        mPresenter.onAttach(LoginActivity.this);
    }

    @OnClick(R.id.google_login_btn)
    void onGoogleLoginClick(View v) {
        mPresenter.onGoogleLoginClick();
    }

    @Override
    protected void onDestroy() {
        mPresenter.onDetach();
        super.onDestroy();
    }

    @Override
    protected void setUp() {

    }

    @Override
    protected void onNewIntent(Intent intent) {
        checkIntent(intent);
    }

    private void checkIntent(@Nullable Intent intent) {
        if (intent != null) {
            String action = intent.getAction();
            if (action == null) return;
            switch (action) {
                case "com.edwiinn.project.HANDLE_AUTHORIZATION_RESPONSE":
                    if (!intent.hasExtra(USED_INTENT)) {
                        handleAuthorizationResponse(intent);
                        intent.putExtra(USED_INTENT, true);
                    }
                    break;
                default:
                    // do nothing
            }
        }
    }

    private void handleAuthorizationResponse(Intent intent) {
        AuthorizationResponse response = AuthorizationResponse.fromIntent(intent);
        AuthorizationException exception = AuthorizationException.fromIntent(intent);
        mPresenter.doGoogleRequestToken(response, exception);
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkIntent(getIntent());
    }

    @Override
    public void openDocumentsActivity() {
        Intent intent = DocumentsActivity.getStartIntent(LoginActivity.this);
        startActivity(intent);
        finish();
    }

    @Override
    public void openMainActivity() {
        Intent intent = MainActivity.getStartIntent(LoginActivity.this);
        startActivity(intent);
        finish();
    }
}
