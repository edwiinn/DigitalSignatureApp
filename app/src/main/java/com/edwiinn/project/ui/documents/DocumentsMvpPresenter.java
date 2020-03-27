package com.edwiinn.project.ui.documents;


import com.edwiinn.project.data.network.model.DocumentsResponse;
import com.edwiinn.project.ui.base.MvpPresenter;

public interface DocumentsMvpPresenter<V extends DocumentsMvpView> extends MvpPresenter<V> {

    void onViewInitialized();

    void onDocumentClicked(DocumentsResponse.Document document);
}