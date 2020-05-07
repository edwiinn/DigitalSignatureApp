package com.edwiinn.project.ui.documents;



import android.util.Log;

import com.edwiinn.project.data.DataManager;
import com.edwiinn.project.data.network.model.DocumentsResponse;
import com.edwiinn.project.ui.base.BasePresenter;
import com.edwiinn.project.utils.rx.SchedulerProvider;

import java.io.File;
import java.util.List;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.observers.DisposableObserver;

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

    @Override
    public void onDocumentClicked(DocumentsResponse.Document document) {
        getMvpView().openDocumentActvity(document);
    }

    @Override
    public void checkAllDocumentsIsSigned(List<DocumentsResponse.Document> documents) {
        for (DocumentsResponse.Document document: documents) {
            document.checkIfDocumentSigned(getDataManager().getSignedDocumentsStorageLocation());
        }
    }

    @Override
    public void uploadSignedDocument(DocumentsResponse.Document document) {
        try {
            getMvpView().showLoading();
            if (!document.getSigned()){
                getMvpView().hideLoading();
                getMvpView().showMessage("Dokumen belum ditandatangani");
                return;
            }
            File signedDocument = new File(getDataManager().getSignedDocumentsStorageLocation(), document.getName());
            if (!signedDocument.exists()) {
                getMvpView().hideLoading();
                getMvpView().showMessage("Dokumen tidak ditemukan");
                return;
            }
            getCompositeDisposable().add(getDataManager()
                .uploadSignedDocument(signedDocument)
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribeWith(new DisposableObserver<String>() {
                    @Override
                    public void onNext(String s) {
                        Log.d("response", s);
                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().hideLoading();
                        getMvpView().showMessage(e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        getMvpView().hideLoading();
                        getMvpView().showMessage("File Sukse di upload");
                    }
                })
            );
        } catch (Exception e){
            getMvpView().hideLoading();
            getMvpView().showMessage(e.getMessage());
        }
    }

    @Override
    public void onLogoutClick() {
        getDataManager().clearAuthState();
        getDataManager().setUserAsLoggedOut();
        getMvpView().openLoginActivity();
    }
}