package com.edwiinn.project.ui.documents.document;


import com.edwiinn.project.ui.base.MvpPresenter;
import com.itextpdf.signatures.PdfPKCS7;

public interface DocumentMvpPresenter<V extends DocumentMvpView> extends MvpPresenter<V> {

    void onViewInitialized();

    void onDocumentLoad();

    void onDocumentSign();

    void onDocumentSignWithSignature(float x, float y, float width, float height, int page);

    boolean isDocumentModified();

    PdfPKCS7 getSignatureData();
}