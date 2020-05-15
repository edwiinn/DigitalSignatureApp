package com.edwiinn.project.data.network.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CertificateResponse {
    @Expose
    @SerializedName("success")
    Boolean isSuccess;

    @Expose
    @SerializedName("result")
    Certificate mCertificate;

    @Expose
    @SerializedName("errors")
    List<String> mErrors;

    @Expose
    @SerializedName("messages")
    List<String> mMessages;

    public Boolean getSuccess() {
        return isSuccess;
    }

    public void setSuccess(Boolean success) {
        isSuccess = success;
    }

    public Certificate getCertificate() {
        return mCertificate;
    }

    public void setCertificate(Certificate certificate) {
        this.mCertificate = certificate;
    }

    public List<String> getErrors() {
        return mErrors;
    }

    public void setErrors(List<String> errors) {
        this.mErrors = errors;
    }

    public List<String> getMessages() {
        return mMessages;
    }

    public void setMessages(List<String> messages) {
        this.mMessages = mMessages;
    }

    public static class Certificate {
        @Expose
        @SerializedName("certificate")
        String mCertificatePem;


        public String getCertificatePem() {
            return mCertificatePem;
        }

        public void setCertificatePem(String certificatePem) {
            this.mCertificatePem = certificatePem;
        }
    }
}
