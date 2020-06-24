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

package com.edwiinn.project.data;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.edwiinn.project.data.network.model.CertificateRequest;
import com.edwiinn.project.data.network.model.CertificateResponse;
import com.edwiinn.project.data.network.model.DocumentsResponse;
import com.edwiinn.project.data.network.model.GoogleResponse;
import com.edwiinn.project.data.network.model.ItsResponse;
import com.edwiinn.project.data.prefs.AuthStateManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.$Gson$Types;
import com.edwiinn.project.data.network.ApiHeader;
import com.edwiinn.project.data.network.ApiHelper;
import com.edwiinn.project.data.network.model.LogoutResponse;
import com.edwiinn.project.data.prefs.PreferencesHelper;
import com.edwiinn.project.di.ApplicationContext;
import com.edwiinn.project.utils.AppConstants;
import com.edwiinn.project.utils.CommonUtils;

import net.openid.appauth.AuthState;
import net.openid.appauth.AuthorizationException;
import net.openid.appauth.TokenResponse;

import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Single;
import io.reactivex.functions.Function;

/**
 * Created by janisharali on 27/01/17.
 */

@Singleton
public class AppDataManager implements DataManager {

    private static final String TAG = "AppDataManager";

    private final Context mContext;
    private final PreferencesHelper mPreferencesHelper;
    private final ApiHelper mApiHelper;
    private final AuthStateManager mAuthStateManager;

    @Inject
    public AppDataManager(@ApplicationContext Context context,
                          PreferencesHelper preferencesHelper,
                          ApiHelper apiHelper,
                          AuthStateManager authStateManager) {
        mContext = context;
        mPreferencesHelper = preferencesHelper;
        mApiHelper = apiHelper;
        mAuthStateManager = authStateManager;
    }

    @Override
    public ApiHeader getApiHeader() {
        return mApiHelper.getApiHeader();
    }

    @Override
    public String getAccessToken() {
        return mPreferencesHelper.getAccessToken();
    }

    @Override
    public void setAccessToken(String accessToken) {
        mPreferencesHelper.setAccessToken(accessToken);
        mApiHelper.getApiHeader().getProtectedApiHeader().setAccessToken(accessToken);
    }

    @Override
    public KeyPair getDocumentKeyPair(String keyAlias) throws CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException, InvalidAlgorithmParameterException, UnrecoverableEntryException {
        return mPreferencesHelper.getDocumentKeyPair(keyAlias);
    }

    @Override
    public KeyPair getDocumentKeyPair() throws CertificateException, NoSuchAlgorithmException, KeyStoreException, UnrecoverableEntryException, InvalidAlgorithmParameterException, IOException {
        return getDocumentKeyPair(getCurrentUserId());
    }

    @Override
    public int getCurrentUserLoggedInMode() {
        return mPreferencesHelper.getCurrentUserLoggedInMode();
    }

    @Override
    public void setCurrentUserLoggedInMode(LoggedInMode mode) {
        mPreferencesHelper.setCurrentUserLoggedInMode(mode);
    }

    @Override
    public String getCurrentUserId() {
        return mPreferencesHelper.getCurrentUserId();
    }

    @Override
    public void setCurrentUserId(String userId) {
        mPreferencesHelper.setCurrentUserId(userId);
    }

    @Override
    public String getCurrentUserName() {
        return mPreferencesHelper.getCurrentUserName();
    }

    @Override
    public void setCurrentUserName(String userName) {
        mPreferencesHelper.setCurrentUserName(userName);
    }

    @Override
    public String getCurrentUserEmail() {
        return mPreferencesHelper.getCurrentUserEmail();
    }

    @Override
    public void setCurrentUserEmail(String email) {
        mPreferencesHelper.setCurrentUserEmail(email);
    }

    @Override
    public String getCurrentUserProfilePicUrl() {
        return mPreferencesHelper.getCurrentUserProfilePicUrl();
    }

    @Override
    public void setCurrentUserProfilePicUrl(String profilePicUrl) {
        mPreferencesHelper.setCurrentUserProfilePicUrl(profilePicUrl);
    }

    @Override
    public void updateApiHeader(String userId, String accessToken) {
        mApiHelper.getApiHeader().getProtectedApiHeader().setUserId(userId);
        mApiHelper.getApiHeader().getProtectedApiHeader().setAccessToken(accessToken);
    }

    @Override
    public void updateUserInfo(
            String accessToken,
            String userId,
            LoggedInMode loggedInMode,
            String userName,
            String email,
            String profilePicPath) {

        setAccessToken(accessToken);
        setCurrentUserId(userId);
        setCurrentUserLoggedInMode(loggedInMode);
        setCurrentUserName(userName);
        setCurrentUserEmail(email);
        setCurrentUserProfilePicUrl(profilePicPath);

        updateApiHeader(userId, accessToken);
    }

    @Override
    public String getDocumentsStorageLocation() {
        return mContext.getExternalFilesDir(null).toString() + "/document";
    }

    @Override
    public String getSignedDocumentsStorageLocation() {
        return mContext.getExternalFilesDir(null).toString() + "/signed-document";
    }

    @Override
    public String getCertificateLocation() {
        return mContext.getExternalFilesDir(null).toString() + "/certificate/" + getCurrentUserId() + ".cert";
    }

    @Override
    public String getRootCertificateLocation() {
        return mContext.getExternalFilesDir(null).toString() + "/certificate/" + "root.cer";
    }

    @Override
    public String getSignatureImageLocation() {
        return mContext.getExternalFilesDir(null).toString() + "/signature/" + getCurrentUserId() + ".png";
    }

    @Override
    public void updateUserAccessToken(String accessToken) {
        setAccessToken(accessToken);
    }

    @Override
    public void setUserAsLoggedOut() {
        updateUserInfo(
                null,
                null,
                DataManager.LoggedInMode.LOGGED_IN_MODE_LOGGED_OUT,
                null,
                null,
                null);
    }

    @Override
    public Single<DocumentsResponse> getAllDocuments() {
        return mApiHelper.getAllDocuments();
    }

    @Override
    public Observable<String> getDocument(String documentName, String downloadLocation) {
        return mApiHelper.getDocument(documentName, downloadLocation);
    }

    @Override
    public Single<CertificateResponse> requestSignCsr(CertificateRequest request) throws JSONException {
        return mApiHelper.requestSignCsr(request);
    }

    @Override
    public Observable<String> uploadSignedDocument(File signedDocument, String documentId) {
        return mApiHelper.uploadSignedDocument(signedDocument, documentId);
    }

    @Override
    public Single<GoogleResponse.UserInfo> getGoogleUserInformation() {
        return mApiHelper.getGoogleUserInformation();
    }

    @Override
    public Single<ItsResponse.UserInfo> getSsoUserInformation() {
        return mApiHelper.getSsoUserInformation();
    }

    @Override
    public AuthState getCurrentAuthState() {
        return mAuthStateManager.getCurrentAuthState();
    }

    @Override
    public void clearAuthState() {
        mAuthStateManager.clearAuthState();
    }

    @Override
    public AuthState replaceAuthState(@NonNull AuthState state) {
        return mAuthStateManager.replaceAuthState(state);
    }

    @Override
    public AuthState updateAuthState(@Nullable TokenResponse response, @Nullable AuthorizationException ex) {
        return mAuthStateManager.updateAuthState(response, ex);
    }
}
