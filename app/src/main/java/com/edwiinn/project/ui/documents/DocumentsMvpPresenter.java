package com.edwiinn.project.ui.documents;


import com.edwiinn.project.data.network.model.DocumentsResponse;
import com.edwiinn.project.ui.base.MvpPresenter;

import java.util.List;

public interface DocumentsMvpPresenter<V extends DocumentsMvpView> extends MvpPresenter<V> {

    void onViewInitialized();

    void onDocumentClicked(DocumentsResponse.Document document);

    void checkAllDocumentsIsSigned(List<DocumentsResponse.Document> documents);

    void uploadSignedDocument(DocumentsResponse.Document document);

    void deleteUserSignedDocument(DocumentsResponse.Document document);

    void onLogoutClick();
}