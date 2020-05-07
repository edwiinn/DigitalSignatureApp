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


import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.edwiinn.project.data.network.model.CsrRequest;
import com.edwiinn.project.data.network.model.DocumentsResponse;
import com.edwiinn.project.data.network.model.GoogleResponse;
import com.edwiinn.project.data.network.model.LogoutResponse;
import com.rx2androidnetworking.Rx2AndroidNetworking;


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

    @Override
    public ApiHeader getApiHeader() {
        return mApiHeader;
    }

    @Override
    public Single<LogoutResponse> doLogoutApiCall() {
        return Rx2AndroidNetworking.post(ApiEndPoint.ENDPOINT_LOGOUT)
                .addHeaders(mApiHeader.getProtectedApiHeader())
                .build()
                .getObjectSingle(LogoutResponse.class);
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

    @Override
    public Single<GoogleResponse.UserInfo> getGoogleUserInformation() {
        return Rx2AndroidNetworking.get(ApiEndPoint.ENDPOINT_GOOGLE_USER_INFO)
                .addHeaders(mApiHeader.getProtectedApiHeader())
                .build()
                .getObjectSingle(GoogleResponse.UserInfo.class);
    }
}

