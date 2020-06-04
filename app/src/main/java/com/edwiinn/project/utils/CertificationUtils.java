package com.edwiinn.project.utils;

import android.graphics.Rect;
import android.util.Base64;

import com.itextpdf.io.font.FontConstants;
import com.itextpdf.io.font.FontMetrics;
import com.itextpdf.io.font.FontNames;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.font.PdfType0Font;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.StampingProperties;
import com.itextpdf.layout.element.Image;
import com.itextpdf.signatures.BouncyCastleDigest;
import com.itextpdf.signatures.DigestAlgorithms;
import com.itextpdf.signatures.PdfSignatureAppearance;
import com.itextpdf.signatures.PdfSigner;
import com.itextpdf.signatures.PrivateKeySignature;

import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import static com.edwiinn.project.utils.AppConstants.DOCUMENT_SIGNATURE_FIELD_NAME;
import static com.edwiinn.project.utils.AppConstants.DOCUMENT_SIGNATURE_LOCATION;
import static com.edwiinn.project.utils.AppConstants.DOCUMENT_SIGNATURE_REASON;

public final class CertificationUtils {

    private CertificationUtils(){

    }

    public static X509Certificate toX509Format(String certificatePem) throws CertificateException, IOException {
        String certificateBase64 = certificatePem
                .replace("-----BEGIN CERTIFICATE-----","")
                .replace("-----END CERTIFICATE-----","");
        byte[] certificateString = Base64.decode(certificateBase64, Base64.DEFAULT);
        ByteArrayInputStream certificateInputStream = new ByteArrayInputStream(certificateString);
        X509Certificate cert = (X509Certificate) CertificateFactory.getInstance("X.509").generateCertificate(certificateInputStream);
        certificateInputStream.close();
        return cert;
    }

    public static void signPdfDocument(
            String src , String dest ,
            Certificate[] chain,
            PrivateKey pk
    ) throws IOException, GeneralSecurityException {
        PdfReader reader = new PdfReader(src);
        PdfSigner signer = new PdfSigner(reader, new FileOutputStream(dest), new StampingProperties());
        signer.getSignatureAppearance()
                .setReason(DOCUMENT_SIGNATURE_REASON)
                .setLocation(DOCUMENT_SIGNATURE_LOCATION);
        signer.setFieldName(DOCUMENT_SIGNATURE_FIELD_NAME);
        PrivateKeySignature pks = new PrivateKeySignature(pk, DigestAlgorithms.SHA256, "AndroidKeyStoreBCWorkaround");
        BouncyCastleDigest digest = new BouncyCastleDigest();
        signer.signDetached(digest, pks, chain, null, null, null, 4*8192, PdfSigner.CryptoStandard.CMS);
    }

    public static void signPdfDocumentWithElectronicSignature(
            String src, String dest,
            Certificate[] chain,
            PrivateKey pk,
            String imageSrc,
            float imageX,
            float imageY,
            float imageWidth,
            float imageHeight,
            int page
    ) throws IOException, GeneralSecurityException {
        PdfReader reader = new PdfReader(src);
        PdfSigner signer = new PdfSigner(reader, new FileOutputStream(dest), new StampingProperties());
        ImageData image = ImageDataFactory.create(imageSrc);
        Rectangle rect = new Rectangle(imageX, imageY, imageWidth, imageHeight);
        signer.getSignatureAppearance()
                .setReason(DOCUMENT_SIGNATURE_REASON)
                .setLocation(DOCUMENT_SIGNATURE_LOCATION)
                .setPageRect(rect)
                .setSignatureGraphic(image)
                .setPageNumber(page)
                .setRenderingMode(PdfSignatureAppearance.RenderingMode.GRAPHIC);
        signer.setFieldName(DOCUMENT_SIGNATURE_FIELD_NAME);

        // Creating the signature
        PrivateKeySignature pks = new PrivateKeySignature(pk, DigestAlgorithms.SHA256, "AndroidKeyStoreBCWorkaround");
        BouncyCastleDigest digest = new BouncyCastleDigest();
        signer.signDetached(digest, pks, chain, null, null, null, 4*8192, PdfSigner.CryptoStandard.CMS);
    }
}
