package com.edwiinn.project.ui.documents;



import com.edwiinn.project.data.DataManager;
import com.edwiinn.project.data.network.model.DocumentsResponse;
import com.edwiinn.project.ui.base.BasePresenter;
import com.edwiinn.project.utils.rx.SchedulerProvider;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;

import javax.inject.Inject;

public class DocumentsPresenter<V extends DocumentsMvpView> extends BasePresenter<V> implements DocumentsMvpPresenter<V> {

    private static final String TAG = "DocumentsPresenter";

    @Inject
    public DocumentsPresenter(DataManager dataManager,
                              SchedulerProvider schedulerProvider,
                              CompositeDisposable compositeDisposable) {
        super(dataManager, schedulerProvider, compositeDisposable);
    }

    @Override
    public void onViewInitialized() {
        getMvpView().showLoading();
        getCompositeDisposable().add(getDataManager()
                .getAllDocuments()
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(new Consumer<DocumentsResponse>() {
                    @Override
                    public void accept(DocumentsResponse documentsResponse) throws Exception {
                        if (documentsResponse != null && documentsResponse.getDocuments() != null){
                            getMvpView().updateDocuments(documentsResponse.getDocuments());
                        }
                        getMvpView().hideLoading();
                    }
                }));
    }
}