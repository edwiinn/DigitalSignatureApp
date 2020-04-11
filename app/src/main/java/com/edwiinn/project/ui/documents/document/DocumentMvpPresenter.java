package com.edwiinn.project.ui.documents.document;


import com.edwiinn.project.ui.base.MvpPresenter;

public interface DocumentMvpPresenter<V extends DocumentMvpView> extends MvpPresenter<V> {

    void onViewInitialized();

    void onLoadCertificate();

    void onDocumentLoad();

    void onDocumentSign();
}