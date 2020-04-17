/*
 * Copyright (C) 2017 MINDORKS NEXTGEN PRIVATE LIMITED
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://mindorks.com/license/apache-v2
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.edwiinn.project.data.network;


import android.content.Context;
import android.util.Log;

import com.edwiinn.project.data.network.model.BlogResponse;
import com.edwiinn.project.data.network.model.CsrRequest;
import com.edwiinn.project.data.network.model.DocumentsResponse;
import com.edwiinn.project.data.network.model.LoginRequest;
import com.edwiinn.project.data.network.model.LoginResponse;
import com.edwiinn.project.data.network.model.LogoutResponse;
import com.edwiinn.project.data.network.model.OpenSourceResponse;
import com.edwiinn.project.di.ApplicationContext;
import com.rx2androidnetworking.Rx2AndroidNetworking;

import org.bouncycastle.pkcs.PKCS10CertificationRequest;

import java.io.File;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Observable;
import io.reactivex.Single;

/**
 * Created by janisharali on 28/01/17.
 */

@Singleton
public class AppApiHelper implements ApiHelper {

    private ApiHeader mApiHeader;

    @Inject
    public AppApiHelper(ApiHeader apiHeader) {
        mApiHeader = apiHeader;
    }

    @Inject
    @ApplicationContext
    Context applicationContext;

    @Override
    public ApiHeader getApiHeader() {
        return mApiHeader;
    }

    @Override
    public Single<LoginResponse> doGoogleLoginApiCall(LoginRequest.GoogleLoginRequest
                                                              request) {
        return Rx2AndroidNetworking.post(ApiEndPoint.ENDPOINT_GOOGLE_LOGIN)
                .addHeaders(mApiHeader.getPublicApiHeader())
                .addBodyParameter(request)
                .build()
                .getObjectSingle(LoginResponse.class);
    }

    @Override
    public Single<LoginResponse> doFacebookLoginApiCall(LoginRequest.FacebookLoginRequest
                                                                request) {
        return Rx2AndroidNetworking.post(ApiEndPoint.ENDPOINT_FACEBOOK_LOGIN)
                .addHeaders(mApiHeader.getPublicApiHeader())
                .addBodyParameter(request)
                .build()
                .getObjectSingle(LoginResponse.class);
    }

    @Override
    public Single<LoginResponse> doServerLoginApiCall(LoginRequest.ServerLoginRequest
                                                              request) {
        return Rx2AndroidNetworking.post(ApiEndPoint.ENDPOINT_SERVER_LOGIN)
                .addHeaders(mApiHeader.getPublicApiHeader())
                .addBodyParameter(request)
                .build()
                .getObjectSingle(LoginResponse.class);
    }

    @Override
    public Single<LogoutResponse> doLogoutApiCall() {
        return Rx2AndroidNetworking.post(ApiEndPoint.ENDPOINT_LOGOUT)
                .addHeaders(mApiHeader.getProtectedApiHeader())
                .build()
                .getObjectSingle(LogoutResponse.class);
    }

    @Override
    public Single<BlogResponse> getBlogApiCall() {
        return Rx2AndroidNetworking.get(ApiEndPoint.ENDPOINT_BLOG)
                .addHeaders(mApiHeader.getProtectedApiHeader())
                .build()
                .getObjectSingle(BlogResponse.class);
    }

    @Override
    public Single<OpenSourceResponse> getOpenSourceApiCall() {
        return Rx2AndroidNetworking.get(ApiEndPoint.ENDPOINT_OPEN_SOURCE)
                .addHeaders(mApiHeader.getProtectedApiHeader())
                .build()
                .getObjectSingle(OpenSourceResponse.class);
    }

    @Override
    public Single<DocumentsResponse> getAllDocuments() {
        return Rx2AndroidNetworking.get(ApiEndPoint.ENDPOINT_DOCUMENTS)
                .addHeaders(mApiHeader.getPublicApiHeader())
                .build()
                .getObjectSingle(DocumentsResponse.class);
    }

    @Override
    public Observable<String> getDocument(String documentName, String downloadLocation) {
        return Rx2AndroidNetworking.download(ApiEndPoint.ENDPOINT_DOCUMENTS + "/" + documentName, downloadLocation, documentName)
                .addHeaders(mApiHeader.getPublicApiHeader())
                .build()
                .getDownloadObservable();
    }

    @Override
    public Observable<String> requestSignCsr(CsrRequest request) {
        return Rx2AndroidNetworking.post(ApiEndPoint.ENDPOINT_CERTIFICATE + "/csr/sign")
                .addHeaders(mApiHeader.getPublicApiHeader())
                .addBodyParameter(request)
                .build()
                .getStringObservable();
    }

    @Override
    public Observable<String> uploadSignedDocument(File signedDocument) {
        return Rx2AndroidNetworking.upload(ApiEndPoint.ENDPOINT_SIGNED_DOCUMENTS)
                .addHeaders(mApiHeader.getPublicApiHeader())
                .addMultipartFile("document", signedDocument)
                .build()
                .getStringObservable();
    }
}

