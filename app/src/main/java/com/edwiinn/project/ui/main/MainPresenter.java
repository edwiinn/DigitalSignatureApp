package com.edwiinn.project.ui.main;

import com.edwiinn.project.data.DataManager;
import com.edwiinn.project.ui.base.BasePresenter;
import com.edwiinn.project.utils.rx.SchedulerProvider;

import io.reactivex.disposables.CompositeDisposable;

import javax.inject.Inject;

public class MainPresenter<V extends MainMvpView> extends BasePresenter<V> implements MainMvpPresenter<V> {

    private static final String TAG = "MainPresenter";

    @Inject
    public MainPresenter(DataManager dataManager,
                         SchedulerProvider schedulerProvider,
                         CompositeDisposable compositeDisposable) {
        super(dataManager, schedulerProvider, compositeDisposable);
    }
}