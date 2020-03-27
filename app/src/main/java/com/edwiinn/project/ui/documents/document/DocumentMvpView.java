package com.edwiinn.project.ui.documents.document;


import com.edwiinn.project.data.network.model.DocumentsResponse;
import com.edwiinn.project.ui.base.MvpView;

public interface DocumentMvpView extends MvpView {

    DocumentsResponse.Document getDocument();

    void showDocument();
}