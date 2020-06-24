package com.edwiinn.project.ui.documents.document;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.edwiinn.project.data.DataManager;
import com.edwiinn.project.data.network.model.DocumentsResponse;
import com.edwiinn.project.ui.base.BasePresenter;
import com.edwiinn.project.utils.AppConstants;
import com.edwiinn.project.utils.CertificationUtils;
import com.edwiinn.project.utils.CommonUtils;
import com.edwiinn.project.utils.rx.SchedulerProvider;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.signatures.CertificateVerification;
import com.itextpdf.signatures.CertificateVerifier;
import com.itextpdf.signatures.PdfPKCS7;
import com.itextpdf.signatures.SignatureUtil;
import com.itextpdf.signatures.VerificationException;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.PublicKey;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.ECPublicKey;
import java.util.List;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;

import javax.inject.Inject;

import static com.edwiinn.project.utils.AppConstants.DOCUMENT_SIGNATURE_FIELD_NAME;

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
            .getDocument(Integer.toString(document.getId()), getDataManager().getDocumentsStorageLocation())
            .subscribeOn(getSchedulerProvider().io())
            .observeOn(getSchedulerProvider().ui())
            .subscribeWith(new DisposableObserver<String>() {
                @Override
                public void onNext(String s) { }

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

        File signatureImageFile = new File(getDataManager().getSignatureImageLocation());
        if (signatureImageFile.exists()){
            Bitmap signatureImageBitmap = BitmapFactory.decodeFile(signatureImageFile.getAbsolutePath());
            getMvpView().updateSignatureImage(signatureImageBitmap);
        } else {
            getMvpView().updateSignatureImage(null);
        }
    }

    @Override
    public void onDocumentLoad() {
        DocumentsResponse.Document document = getMvpView().getDocument();
        File documentFile = new File(getDataManager().getDocumentsStorageLocation(), document.getId() + ".pdf");
        File userSignedDocumentFile = new File(getDataManager().getSignedDocumentsStorageLocation(), document.getId() + ".pdf");
        if(document.getUserSigned() && !document.getSigned() && userSignedDocumentFile.exists()){
            documentFile = userSignedDocumentFile;
        }
        getMvpView().showDocument(documentFile);
        getMvpView().registerSignatureImageBehaviour();
    }

    @Override
    public void onDocumentSign() {
        DocumentsResponse.Document document = getMvpView().getDocument();
        try {
            KeyPair kp = getDataManager().getDocumentKeyPair();
            String documentSrc = getDataManager().getDocumentsStorageLocation() + "/" + document.getId() + ".pdf";
            String documentDst = getDataManager().getSignedDocumentsStorageLocation() + "/" + document.getId() + ".pdf";
            String certificatePem = CommonUtils.usingBufferedReader(getDataManager().getCertificateLocation());
            X509Certificate certificate = CertificationUtils.toX509Format(certificatePem);
            File file = new File(documentDst);
            if (!file.exists()) {
                file.getParentFile().mkdir();
                file.createNewFile();
            }
            Certificate[] chain = { certificate };
            CertificationUtils.signPdfDocument(documentSrc, documentDst, chain, kp.getPrivate());
            getMvpView().showMessage("Dokumen " + document.getName() + " berhasil di tanda tangani");
        } catch (Exception exception) {
            File file = new File(getDataManager().getSignedDocumentsStorageLocation(), document.getId() + ".pdf");
            file.delete();
            getMvpView().onError(exception.getMessage());
        }
        getMvpView().closeActivity();
    }

    @Override
    public void onDocumentSignWithSignature(float x, float y, float width, float height, int page) {
        getMvpView().showLoading();
        DocumentsResponse.Document document = getMvpView().getDocument();
        try {
            KeyPair kp = getDataManager().getDocumentKeyPair();
            String documentSrc = getDataManager().getDocumentsStorageLocation() + "/" + document.getId() + ".pdf";
            String documentDst = getDataManager().getSignedDocumentsStorageLocation() + "/" + document.getId() + ".pdf";
            String certificatePem = CommonUtils.usingBufferedReader(getDataManager().getCertificateLocation());
            X509Certificate certificate = CertificationUtils.toX509Format(certificatePem);
            File file = new File(documentDst);
            if (!file.exists()) {
                file.getParentFile().mkdir();
                file.createNewFile();
            }
            Certificate[] chain = { certificate };
            CertificationUtils.signPdfDocumentWithElectronicSignature(documentSrc, documentDst, chain, kp.getPrivate(), getDataManager().getSignatureImageLocation(), x, y, width, height, page);
            getMvpView().showMessage("Dokumen " + document.getName() + " berhasil di tanda tangani");
        } catch (Exception exception) {
            File file = new File(getDataManager().getSignedDocumentsStorageLocation(), document.getId() + ".pdf");
            file.delete();
            getMvpView().showMessage(exception.getMessage());
            getMvpView().onError(exception.getMessage());
            exception.printStackTrace();
        }
        getMvpView().hideLoading();
        getMvpView().closeActivity();

    }

    @Override
    public boolean isDocumentModified() {
        boolean wasModified = true;
        DocumentsResponse.Document document = getMvpView().getDocument();
        File file = new File(getDataManager().getDocumentsStorageLocation(), document.getId() + ".pdf");
        try {
            BouncyCastleProvider provider = new BouncyCastleProvider();
            Security.addProvider(provider);
            PdfDocument pdfDoc = new PdfDocument(new PdfReader(file.getAbsolutePath()));
            SignatureUtil signUtil = new SignatureUtil(pdfDoc);
            PdfPKCS7 signatureData = signUtil.readSignatureData(DOCUMENT_SIGNATURE_FIELD_NAME);
            if (signatureData != null){
                wasModified = !signatureData.verifySignatureIntegrityAndAuthenticity();
            }
            pdfDoc.close();
        } catch (Exception exception) {
            exception.printStackTrace();
            getMvpView().showMessage(exception.toString());
        }
        return wasModified;
    }

    @Override
    public PdfPKCS7 getSignatureData() {
        DocumentsResponse.Document document = getMvpView().getDocument();
        File file = new File(getDataManager().getDocumentsStorageLocation(), document.getId() + ".pdf");
        try {
            BouncyCastleProvider provider = new BouncyCastleProvider();
            Security.addProvider(provider);
            PdfDocument pdfDoc = new PdfDocument(new PdfReader(file.getAbsolutePath()));
            SignatureUtil signUtil = new SignatureUtil(pdfDoc);
            PdfPKCS7 signatureData = signUtil.readSignatureData(DOCUMENT_SIGNATURE_FIELD_NAME);
            pdfDoc.close();
            return signatureData;
        } catch (Exception exception) {
            exception.printStackTrace();
            getMvpView().showMessage(exception.toString());
        }
        return null;
    }


}