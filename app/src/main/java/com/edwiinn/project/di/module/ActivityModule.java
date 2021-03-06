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

package com.edwiinn.project.di.module;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;

import com.edwiinn.project.data.network.model.DocumentsResponse;
import com.edwiinn.project.di.ActivityContext;
import com.edwiinn.project.di.PerActivity;
import com.edwiinn.project.ui.documents.DocumentsAdapter;
import com.edwiinn.project.ui.documents.document.signer.SignDialogMvpPresenter;
import com.edwiinn.project.ui.documents.document.signer.SignDialogMvpView;
import com.edwiinn.project.ui.documents.document.signer.SignDialogPresenter;
import com.edwiinn.project.ui.login.LoginMvpPresenter;
import com.edwiinn.project.ui.login.LoginMvpView;
import com.edwiinn.project.ui.login.LoginPresenter;
import com.edwiinn.project.ui.splash.SplashMvpPresenter;
import com.edwiinn.project.ui.splash.SplashMvpView;
import com.edwiinn.project.ui.splash.SplashPresenter;
import com.edwiinn.project.utils.rx.AppSchedulerProvider;
import com.edwiinn.project.utils.rx.SchedulerProvider;

import net.openid.appauth.AuthState;
import net.openid.appauth.AuthorizationException;
import net.openid.appauth.AuthorizationResponse;
import net.openid.appauth.AuthorizationService;

import java.util.ArrayList;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by janisharali on 27/01/17.
 */

@Module
public class ActivityModule {

    private AppCompatActivity mActivity;

    public ActivityModule(AppCompatActivity activity) {
        this.mActivity = activity;
    }

    @Provides
    @ActivityContext
    Context provideContext() {
        return mActivity;
    }

    @Provides
    @ActivityContext
    AuthorizationService provideAuthorizationService() { return new AuthorizationService(mActivity); }

    @Provides
    AppCompatActivity provideActivity() {
        return mActivity;
    }

    @Provides
    CompositeDisposable provideCompositeDisposable() {
        return new CompositeDisposable();
    }

    @Provides
    SchedulerProvider provideSchedulerProvider() {
        return new AppSchedulerProvider();
    }

    @Provides
    @PerActivity
    SplashMvpPresenter<SplashMvpView> provideSplashPresenter(
            SplashPresenter<SplashMvpView> presenter) {
        return presenter;
    }

    @Provides
    @PerActivity
    LoginMvpPresenter<LoginMvpView> provideLoginPresenter(
            LoginPresenter<LoginMvpView> presenter) {
        return presenter;
    }

    @Provides
    SignDialogMvpPresenter<SignDialogMvpView> provideSignDialogMvpPresenter(
            SignDialogPresenter<SignDialogMvpView> presenter) {
        return presenter;
    }

    @Provides
    DocumentsAdapter provideDocumentsAdapter() {
        return new DocumentsAdapter(new ArrayList<DocumentsResponse.Document>());
    }

    @Provides
    LinearLayoutManager provideLinearLayoutManager(AppCompatActivity activity) {
        return new LinearLayoutManager(activity);
    }
}
