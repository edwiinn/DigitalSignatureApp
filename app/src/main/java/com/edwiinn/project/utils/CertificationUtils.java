package com.edwiinn.project.utils;

import android.util.Base64;

import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfReader;
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
            PrivateKey pk,
            String reason,
            String location
    ) throws IOException, GeneralSecurityException {
        PdfReader reader = new PdfReader(src);
        PdfSigner signer = new PdfSigner(reader, new FileOutputStream(dest), false);
        PdfSignatureAppearance appearance = signer.getSignatureAppearance()
                .setReason(reason)
                .setLocation(location)
                .setReuseAppearance(false);
        signer.setFieldName("sig");
        // Creating the signature
        PrivateKeySignature pks = new PrivateKeySignature(pk, DigestAlgorithms.SHA256, "AndroidKeyStoreBCWorkaround");
        BouncyCastleDigest digest = new BouncyCastleDigest();
        signer.signDetached(digest, pks, chain, null, null, null, 4*8192, PdfSigner.CryptoStandard.CMS);
    }
}