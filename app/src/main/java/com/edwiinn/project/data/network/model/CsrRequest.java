package com.edwiinn.project.data.network.model;

import com.edwiinn.project.utils.CsrUtils;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.bouncycastle.pkcs.PKCS10CertificationRequest;

import java.io.IOException;

public class CsrRequest {

    @Expose
    @SerializedName("csr")
    private String mCertificationRequest;

    public CsrRequest(String certificationRequest){
        mCertificationRequest = certificationRequest;
    }

    public CsrRequest(PKCS10CertificationRequest certificationRequest){
        try {
            mCertificationRequest = CsrUtils.toBase64Format(certificationRequest);
        } catch (IOException e) {
            mCertificationRequest = null;
        }
    }

    public String getCertificationRequest() {
        return mCertificationRequest;
    }

    public void setCertificationRequest(String mCertificationRequest) {
        this.mCertificationRequest = mCertificationRequest;
    }
}
