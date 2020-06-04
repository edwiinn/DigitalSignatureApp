package com.edwiinn.project.utils;

import android.util.Base64;
import android.util.Log;

import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.ExtensionsGenerator;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.bouncycastle.pkcs.PKCS10CertificationRequestBuilder;
import org.bouncycastle.pkcs.jcajce.JcaPKCS10CertificationRequestBuilder;
import org.bouncycastle.util.io.pem.PemObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.Signature;
import java.util.HashMap;
import java.util.Map;

import static com.edwiinn.project.utils.AppConstants.CSR_ATTRIBUTE_C;
import static com.edwiinn.project.utils.AppConstants.CSR_ATTRIBUTE_L;
import static com.edwiinn.project.utils.AppConstants.CSR_ATTRIBUTE_ST;

public final class CsrUtils {

    private CsrUtils() {
        // This utility class is not publicly instantiable
    }

    public static PKCS10CertificationRequest generateCSR(KeyPair keyPair, String cn, String o, String ou) throws IOException, OperatorCreationException {
        X500NameBuilder builder = new X500NameBuilder(BCStyle.INSTANCE);
        builder.addRDN(BCStyle.O, o);
        builder.addRDN(BCStyle.OU, ou);
        builder.addRDN(BCStyle.ST, CSR_ATTRIBUTE_ST);
        builder.addRDN(BCStyle.C, CSR_ATTRIBUTE_C);
        builder.addRDN(BCStyle.L, CSR_ATTRIBUTE_L);
        builder.addRDN(BCStyle.CN, cn);
        PKCS10CertificationRequestBuilder p10Builder = new JcaPKCS10CertificationRequestBuilder(
                builder.build(), keyPair.getPublic());
        JcaContentSignerBuilder csrBuilder = new JcaContentSignerBuilder(AppConstants.CSR_SIGNATURE_ALGORITHM);
        ContentSigner signer = csrBuilder.build(keyPair.getPrivate());
        return p10Builder.build(signer);
    }

    public static String toPemFormat(PKCS10CertificationRequest csr) throws IOException {
        final StringWriter writer = new StringWriter();
        final JcaPEMWriter pemWriter = new JcaPEMWriter(writer);
        pemWriter.writeObject(csr);
        pemWriter.flush();
        pemWriter.close();
        return writer.toString();
    }
}