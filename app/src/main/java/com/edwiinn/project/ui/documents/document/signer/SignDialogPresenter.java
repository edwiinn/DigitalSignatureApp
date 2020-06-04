package com.edwiinn.project.ui.documents.document.signer;

import com.edwiinn.project.data.DataManager;
import com.edwiinn.project.ui.base.BasePresenter;
import com.edwiinn.project.utils.rx.SchedulerProvider;

import java.io.File;

import io.reactivex.disposables.CompositeDisposable;

import javax.inject.Inject;

public class SignDialogPresenter<V extends SignDialogMvpView> extends BasePresenter<V> implements SignDialogMvpPresenter<V> {

    private static final String TAG = "SignDialogPresenter";

    @Inject
    public SignDialogPresenter(DataManager dataManager,
                               SchedulerProvider schedulerProvider,
                               CompositeDisposable compositeDisposable) {
        super(dataManager, schedulerProvider, compositeDisposable);
    }

    @Override
    public boolean isSignatureImageAvailable() {
        return new File(getDataManager().getSignatureImageLocation()).exists();
    }
}