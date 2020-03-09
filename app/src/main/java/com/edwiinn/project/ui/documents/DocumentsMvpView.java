package com.edwiinn.project.ui.documents;


import com.edwiinn.project.data.network.model.DocumentsResponse;
import com.edwiinn.project.ui.base.MvpView;

import java.util.List;

public interface DocumentsMvpView extends MvpView {

    void updateDocuments(List<DocumentsResponse.Document> documents);
}