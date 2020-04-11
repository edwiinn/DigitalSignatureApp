package com.edwiinn.project.utils;

import android.util.Base64;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.ExtensionsGenerator;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.bouncycastle.pkcs.PKCS10CertificationRequestBuilder;
import org.bouncycastle.pkcs.jcajce.JcaPKCS10CertificationRequestBuilder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.Signature;
import java.util.HashMap;
import java.util.Map;

public final class CsrUtils {

    private CsrUtils() {
        // This utility class is not publicly instantiable
    }

    private static class JCESigner implements ContentSigner {

        private String mAlgo;
        private Signature signature;
        private ByteArrayOutputStream outputStream;

        private static Map<String, AlgorithmIdentifier> ALGOS = new HashMap<>();
        static {
            ALGOS.put("SHA256withRSA".toLowerCase(), new AlgorithmIdentifier(
                    new ASN1ObjectIdentifier("1.2.840.113549.1.1.11")
            ));
            ALGOS.put("SHA1withRSA".toLowerCase(), new AlgorithmIdentifier(
                    new ASN1ObjectIdentifier("1.2.840.113549.1.1.5")
            ));
        }

        private JCESigner(PrivateKey privateKey , String sigAlgo){
            this.mAlgo = sigAlgo.toLowerCase();
            try {
                this.outputStream = new ByteArrayOutputStream();
                this.signature = Signature.getInstance(sigAlgo);
                this.signature.initSign(privateKey);
            } catch (GeneralSecurityException gse) {
                throw new IllegalArgumentException(gse.getMessage());
            }
        }

        @Override
        public AlgorithmIdentifier getAlgorithmIdentifier() {
            AlgorithmIdentifier algo = ALGOS.get(this.mAlgo);
            if (algo == null){
                throw new IllegalArgumentException("Does not support algo: ".concat(this.mAlgo));
            }
            return algo;
        }

        @Override
        public OutputStream getOutputStream() {
            return this.outputStream;
        }

        @Override
        public byte[] getSignature() {
            byte[] signatureByte = null;
            try {
                signature.update(outputStream.toByteArray());
                signatureByte = signature.sign();
            } catch (GeneralSecurityException gse){
                gse.printStackTrace();
            }
            return signatureByte;
        }
    }

    public static PKCS10CertificationRequest generateCSR(KeyPair keyPair, String cn, String o, String ou) throws IOException {
        String principal = String.format(AppConstants.CSR_CN_PATTERN, cn, o, ou);
        JCESigner signer = new JCESigner(keyPair.getPrivate(), AppConstants.CSR_SIGNATURE_ALGORITHM);
        PKCS10CertificationRequestBuilder csrBuilder = new JcaPKCS10CertificationRequestBuilder(
                new X500Name(principal), keyPair.getPublic()
        );
        ExtensionsGenerator extensionsGenerator = new ExtensionsGenerator();
        extensionsGenerator.addExtension(
                Extension.basicConstraints, true, new BasicConstraints(true)
        );
        csrBuilder.addAttribute(
                PKCSObjectIdentifiers.pkcs_9_at_extensionRequest,
                extensionsGenerator.generate()
        );

        return csrBuilder.build(signer);
    }

    public static String toBase64Format(PKCS10CertificationRequest csr) throws IOException {
        String encodedCsr = Base64.encodeToString(csr.getEncoded(), Base64.DEFAULT);
        return String.format(AppConstants.CSR_BASE64_PATTERN, encodedCsr);
    }
}