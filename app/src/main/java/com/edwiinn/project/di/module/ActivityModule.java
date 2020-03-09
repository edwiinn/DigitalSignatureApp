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

import com.edwiinn.project.data.network.model.BlogResponse;
import com.edwiinn.project.data.network.model.DocumentsResponse;
import com.edwiinn.project.data.network.model.OpenSourceResponse;
import com.edwiinn.project.di.ActivityContext;
import com.edwiinn.project.di.PerActivity;
import com.edwiinn.project.ui.about.AboutMvpPresenter;
import com.edwiinn.project.ui.about.AboutMvpView;
import com.edwiinn.project.ui.about.AboutPresenter;
import com.edwiinn.project.ui.documents.DocumentsAdapter;
import com.edwiinn.project.ui.feed.FeedMvpPresenter;
import com.edwiinn.project.ui.feed.FeedMvpView;
import com.edwiinn.project.ui.feed.FeedPagerAdapter;
import com.edwiinn.project.ui.feed.FeedPresenter;
import com.edwiinn.project.ui.feed.blogs.BlogAdapter;
import com.edwiinn.project.ui.feed.blogs.BlogMvpPresenter;
import com.edwiinn.project.ui.feed.blogs.BlogMvpView;
import com.edwiinn.project.ui.feed.blogs.BlogPresenter;
import com.edwiinn.project.ui.feed.opensource.OpenSourceAdapter;
import com.edwiinn.project.ui.feed.opensource.OpenSourceMvpPresenter;
import com.edwiinn.project.ui.feed.opensource.OpenSourceMvpView;
import com.edwiinn.project.ui.feed.opensource.OpenSourcePresenter;
import com.edwiinn.project.ui.login.LoginMvpPresenter;
import com.edwiinn.project.ui.login.LoginMvpView;
import com.edwiinn.project.ui.login.LoginPresenter;
import com.edwiinn.project.ui.main.MainMvpPresenter;
import com.edwiinn.project.ui.main.MainMvpView;
import com.edwiinn.project.ui.main.MainPresenter;
import com.edwiinn.project.ui.main.rating.RatingDialogMvpPresenter;
import com.edwiinn.project.ui.main.rating.RatingDialogMvpView;
import com.edwiinn.project.ui.main.rating.RatingDialogPresenter;
import com.edwiinn.project.ui.splash.SplashMvpPresenter;
import com.edwiinn.project.ui.splash.SplashMvpView;
import com.edwiinn.project.ui.splash.SplashPresenter;
import com.edwiinn.project.utils.rx.AppSchedulerProvider;
import com.edwiinn.project.utils.rx.SchedulerProvider;

import java.util.ArrayList;

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
    AboutMvpPresenter<AboutMvpView> provideAboutPresenter(
            AboutPresenter<AboutMvpView> presenter) {
        return presenter;
    }

    @Provides
    @PerActivity
    LoginMvpPresenter<LoginMvpView> provideLoginPresenter(
            LoginPresenter<LoginMvpView> presenter) {
        return presenter;
    }

    @Provides
    @PerActivity
    MainMvpPresenter<MainMvpView> provideMainPresenter(
            MainPresenter<MainMvpView> presenter) {
        return presenter;
    }

    @Provides
    RatingDialogMvpPresenter<RatingDialogMvpView> provideRateUsPresenter(
            RatingDialogPresenter<RatingDialogMvpView> presenter) {
        return presenter;
    }

    @Provides
    FeedMvpPresenter<FeedMvpView> provideFeedPresenter(
            FeedPresenter<FeedMvpView> presenter) {
        return presenter;
    }

    @Provides
    OpenSourceMvpPresenter<OpenSourceMvpView> provideOpenSourcePresenter(
            OpenSourcePresenter<OpenSourceMvpView> presenter) {
        return presenter;
    }

    @Provides
    BlogMvpPresenter<BlogMvpView> provideBlogMvpPresenter(
            BlogPresenter<BlogMvpView> presenter) {
        return presenter;
    }

    @Provides
    FeedPagerAdapter provideFeedPagerAdapter(AppCompatActivity activity) {
        return new FeedPagerAdapter(activity.getSupportFragmentManager());
    }

    @Provides
    OpenSourceAdapter provideOpenSourceAdapter() {
        return new OpenSourceAdapter(new ArrayList<OpenSourceResponse.Repo>());
    }

    @Provides
    BlogAdapter provideBlogAdapter() {
        return new BlogAdapter(new ArrayList<BlogResponse.Blog>());
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
