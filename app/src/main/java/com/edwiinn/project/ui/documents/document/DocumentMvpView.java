package com.edwiinn.project.ui.documents.document;


import android.content.Context;
import android.graphics.Bitmap;

import com.edwiinn.project.data.network.model.DocumentsResponse;
import com.edwiinn.project.ui.base.MvpView;

import java.io.File;

public interface DocumentMvpView extends MvpView {

    DocumentsResponse.Document getDocument();

    void registerSignatureImageBehaviour();

    void showDocument(File document);

    void showSignerDialog();

    void showToast(String text);

    void showSignerButton();

    void hideSignerButton();

    void signDocument();

    void signDocumentWithElectronicSignature();

    void updateSignatureImage(Bitmap bitmap);

    void closeActivity();
}