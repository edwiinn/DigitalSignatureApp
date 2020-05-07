package com.edwiinn.project.ui.documents.document;

import android.util.Log;

import com.edwiinn.project.data.DataManager;
import com.edwiinn.project.data.network.model.CsrRequest;
import com.edwiinn.project.data.network.model.DocumentsResponse;
import com.edwiinn.project.ui.base.BasePresenter;
import com.edwiinn.project.utils.CertificationUtils;
import com.edwiinn.project.utils.CommonUtils;
import com.edwiinn.project.utils.CsrUtils;
import com.edwiinn.project.utils.rx.SchedulerProvider;

import org.bouncycastle.pkcs.PKCS10CertificationRequest;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.security.KeyPair;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;

import javax.inject.Inject;

public class DocumentPresenter<V extends DocumentMvpView> extends BasePresenter<V> implements DocumentMvpPresenter<V> {

    private static final String TAG = "DocumentPresenter";

    @Inject
    public DocumentPresenter(DataManager dataManager,
                             SchedulerProvider schedulerProvider,
                             CompositeDisposable compositeDisposable) {
        super(dataManager, schedulerProvider, compositeDisposable);
    }

    @Override
    public void onViewInitialized() {

        DocumentsResponse.Document document = getMvpView().getDocument();
        getMvpView().showLoading();

        getCompositeDisposable().add(getDataManager()
            .getDocument(document.getName(), getDataManager().getDocumentsStorageLocation())
            .subscribeOn(getSchedulerProvider().io())
            .observeOn(getSchedulerProvider().ui())
            .subscribeWith(new DisposableObserver<String>() {
                @Override
                public void onNext(String s) {
                    Log.d("next", s);
                }

                @Override
                public void onError(Throwable e) {
                    e.printStackTrace();
                    getMvpView().showToast(e.getMessage());
                    getMvpView().closeActivity();
                }

                @Override
                public void onComplete() {
                    onDocumentLoad();
                    getMvpView().hideLoading();
                }
            })
        );
    }

    @Override
    public void onDocumentLoad() {
        DocumentsResponse.Document document = getMvpView().getDocument();
        File documentFile = new File(getDataManager().getDocumentsStorageLocation(), document.getName());
        getMvpView().showDocument(documentFile);
    }

    @Override
    public void onDocumentSign() {
        DocumentsResponse.Document document = getMvpView().getDocument();
        try {
            KeyPair kp = getDataManager().getDocumentKeyPair();
            String documentSrc = getDataManager().getDocumentsStorageLocation() + "/" + document.getName();
            String documentDst = getDataManager().getSignedDocumentsStorageLocation() + "/" + document.getName();
            String certificatePem = CommonUtils.usingBufferedReader(getDataManager().getCertificateLocation());
            X509Certificate certificate = CertificationUtils.toX509Format(certificatePem);
            File file = new File(documentDst);
            if (!file.exists()) {
                file.getParentFile().mkdir();
                file.createNewFile();
            }
            Certificate[] chain = { certificate };
            CertificationUtils.signPdfDocument(documentSrc, documentDst, chain, kp.getPrivate(), "I Verify This Document","Surabaya");
            getMvpView().showMessage("Dokumen " + document.getName() + " berhasil di tanda tangani");
        } catch (Exception exception) {
            getMvpView().showMessage(exception.getMessage());
        }
        getMvpView().closeActivity();
    }
}