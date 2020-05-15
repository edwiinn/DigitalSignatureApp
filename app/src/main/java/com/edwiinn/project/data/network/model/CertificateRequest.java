package com.edwiinn.project.data.network.model;

import com.edwiinn.project.utils.CsrUtils;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.bouncycastle.pkcs.PKCS10CertificationRequest;

import java.io.IOException;

public class CertificateRequest {

    @Expose
    @SerializedName("certificate_request")
    private String mCertificationRequest;

    public CertificateRequest(String certificationRequest){
        mCertificationRequest = certificationRequest;
    }

    public String getCertificationRequest() {
        return mCertificationRequest;
    }

    public void setCertificationRequest(String certificationRequest) {
        this.mCertificationRequest = certificationRequest;
    }
}
