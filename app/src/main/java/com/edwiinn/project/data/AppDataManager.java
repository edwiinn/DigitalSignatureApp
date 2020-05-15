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
import com.edwiinn.project.data.prefs.AuthStateManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.$Gson$Types;
import com.google.gson.reflect.TypeToken;
import com.edwiinn.project.data.db.DbHelper;
import com.edwiinn.project.data.db.model.Option;
import com.edwiinn.project.data.db.model.Question;
import com.edwiinn.project.data.db.model.User;
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
    private final DbHelper mDbHelper;
    private final PreferencesHelper mPreferencesHelper;
    private final ApiHelper mApiHelper;
    private final AuthStateManager mAuthStateManager;

    @Inject
    public AppDataManager(@ApplicationContext Context context,
                          DbHelper dbHelper,
                          PreferencesHelper preferencesHelper,
                          ApiHelper apiHelper,
                          AuthStateManager authStateManager) {
        mContext = context;
        mDbHelper = dbHelper;
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
    public KeyPair getDocumentKeyPair() throws CertificateException, NoSuchAlgorithmException, KeyStoreException, UnrecoverableEntryException, InvalidAlgorithmParameterException, IOException {
        return mPreferencesHelper.getDocumentKeyPair();
    }

    @Override
    public Observable<Long> insertUser(User user) {
        return mDbHelper.insertUser(user);
    }

    @Override
    public Observable<List<User>> getAllUsers() {
        return mDbHelper.getAllUsers();
    }

    @Override
    public Single<LogoutResponse> doLogoutApiCall() {
        return mApiHelper.doLogoutApiCall();
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
        return mContext.getExternalFilesDir(null).toString() + "/certificate/certificate.cert";
    }

    @Override
    public String getSignatureImageLocation() {
        return mContext.getExternalFilesDir(null).toString() + "/signature/signature.png";
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
    public Observable<Boolean> isQuestionEmpty() {
        return mDbHelper.isQuestionEmpty();
    }

    @Override
    public Observable<Boolean> isOptionEmpty() {
        return mDbHelper.isOptionEmpty();
    }

    @Override
    public Observable<Boolean> saveQuestion(Question question) {
        return mDbHelper.saveQuestion(question);
    }

    @Override
    public Observable<Boolean> saveOption(Option option) {
        return mDbHelper.saveOption(option);
    }

    @Override
    public Observable<Boolean> saveQuestionList(List<Question> questionList) {
        return mDbHelper.saveQuestionList(questionList);
    }

    @Override
    public Observable<Boolean> saveOptionList(List<Option> optionList) {
        return mDbHelper.saveOptionList(optionList);
    }

    @Override
    public Observable<List<Question>> getAllQuestions() {
        return mDbHelper.getAllQuestions();
    }

    @Override
    public Observable<Boolean> seedDatabaseQuestions() {

        GsonBuilder builder = new GsonBuilder().excludeFieldsWithoutExposeAnnotation();
        final Gson gson = builder.create();

        return mDbHelper.isQuestionEmpty()
                .concatMap(new Function<Boolean, ObservableSource<? extends Boolean>>() {
                    @Override
                    public ObservableSource<? extends Boolean> apply(Boolean isEmpty)
                            throws Exception {
                        if (isEmpty) {
                            Type type = $Gson$Types
                                    .newParameterizedTypeWithOwner(null, List.class,
                                            Question.class);
                            List<Question> questionList = gson.fromJson(
                                    CommonUtils.loadJSONFromAsset(mContext,
                                            AppConstants.SEED_DATABASE_QUESTIONS),
                                    type);

                            return saveQuestionList(questionList);
                        }
                        return Observable.just(false);
                    }
                });
    }

    @Override
    public Observable<Boolean> seedDatabaseOptions() {

        GsonBuilder builder = new GsonBuilder().excludeFieldsWithoutExposeAnnotation();
        final Gson gson = builder.create();

        return mDbHelper.isOptionEmpty()
                .concatMap(new Function<Boolean, ObservableSource<? extends Boolean>>() {
                    @Override
                    public ObservableSource<? extends Boolean> apply(Boolean isEmpty)
                            throws Exception {
                        if (isEmpty) {
                            Type type = new TypeToken<List<Option>>() {
                            }
                                    .getType();
                            List<Option> optionList = gson.fromJson(
                                    CommonUtils.loadJSONFromAsset(mContext,
                                            AppConstants.SEED_DATABASE_OPTIONS),
                                    type);

                            return saveOptionList(optionList);
                        }
                        return Observable.just(false);
                    }
                });
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
    public Observable<String> uploadSignedDocument(File signedDocument) {
        return mApiHelper.uploadSignedDocument(signedDocument);
    }

    @Override
    public Single<GoogleResponse.UserInfo> getGoogleUserInformation() {
        return mApiHelper.getGoogleUserInformation();
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
