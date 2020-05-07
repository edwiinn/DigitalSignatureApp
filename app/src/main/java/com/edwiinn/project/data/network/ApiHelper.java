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


import com.edwiinn.project.data.network.model.CsrRequest;
import com.edwiinn.project.data.network.model.DocumentsResponse;
import com.edwiinn.project.data.network.model.GoogleResponse;
import com.edwiinn.project.data.network.model.LogoutResponse;

import java.io.File;

import io.reactivex.Observable;
import io.reactivex.Single;

/**
 * Created by janisharali on 27/01/17.
 */

public interface ApiHelper {

    ApiHeader getApiHeader();

    Single<LogoutResponse> doLogoutApiCall();

    Single<DocumentsResponse> getAllDocuments();

    Observable<String> getDocument(String documentName, String downloadLocation);

    Observable<String> requestSignCsr(CsrRequest request);

    Observable<String> uploadSignedDocument(File signedDocument);

    Single<GoogleResponse.UserInfo> getGoogleUserInformation();
}
