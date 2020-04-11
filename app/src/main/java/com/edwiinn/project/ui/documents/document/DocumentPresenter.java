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
    public void onLoadCertificate() {
        getMvpView().showLoading();
        File file = new File(getDataManager().getCertificateLocation());
        if(file.exists()) file.delete();

        try {
            KeyPair keyPair = getDataManager().getDocumentKeyPair();
            PKCS10CertificationRequest csr = CsrUtils.generateCSR(keyPair, "Edwin", "ITS", "Informatika");
            CsrRequest request = new CsrRequest(CsrUtils.toBase64Format(csr));
            Log.d("csr", CsrUtils.toBase64Format(csr));
            getCompositeDisposable().add(getDataManager()
                    .requestSignCsr(request)
                    .subscribeOn(getSchedulerProvider().io())
                    .observeOn(getSchedulerProvider().ui())
                    .subscribeWith(new DisposableObserver<String>() {
                        @Override
                        public void onNext(String s) {
                            File file = new File(getDataManager().getCertificateLocation());
                            try {
                                if (!file.exists()){
                                    file.getParentFile().mkdir();
                                    file.createNewFile();
                                }
                                FileWriter fileWriter = new FileWriter(file, true);
                                fileWriter.append(s);
                                fileWriter.flush();
                                fileWriter.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                                getMvpView().showMessage(e.getMessage());
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            e.printStackTrace();
                            getMvpView().showMessage(e.getMessage());
                            getMvpView().hideLoading();
                            Log.e("Certificate", e.getMessage());
                            getMvpView().closeActivity();
                        }

                        @Override
                        public void onComplete() {
                            getMvpView().hideLoading();
                        }
                    })
            );
        } catch (Exception exception) {
            getMvpView().showMessage(exception.getMessage());
            getMvpView().hideLoading();
        }
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