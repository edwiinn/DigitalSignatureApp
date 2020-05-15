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

package com.edwiinn.project.ui.login;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

import com.androidnetworking.error.ANError;
import com.edwiinn.project.BuildConfig;
import com.edwiinn.project.data.DataManager;
import com.edwiinn.project.data.network.ApiEndPoint;
import com.edwiinn.project.data.network.model.CertificateRequest;
import com.edwiinn.project.data.network.model.CertificateResponse;
import com.edwiinn.project.data.network.model.GoogleResponse;
import com.edwiinn.project.di.ActivityContext;
import com.edwiinn.project.ui.base.BasePresenter;
import com.edwiinn.project.utils.CsrUtils;
import com.edwiinn.project.utils.rx.SchedulerProvider;

import net.openid.appauth.AuthState;
import net.openid.appauth.AuthorizationException;
import net.openid.appauth.AuthorizationRequest;
import net.openid.appauth.AuthorizationResponse;
import net.openid.appauth.AuthorizationService;
import net.openid.appauth.AuthorizationServiceConfiguration;
import net.openid.appauth.TokenResponse;

import org.bouncycastle.pkcs.PKCS10CertificationRequest;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.security.KeyPair;

import javax.inject.Inject;

import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.observers.DisposableObserver;

/**
 * Created by janisharali on 27/01/17.
 */

public class LoginPresenter<V extends LoginMvpView> extends BasePresenter<V>
        implements LoginMvpPresenter<V> {

    private static final String TAG = "LoginPresenter";

    @Inject
    @ActivityContext
    Context mActivityContext;

    @Inject
    public LoginPresenter(DataManager dataManager,
                          SchedulerProvider schedulerProvider,
                          CompositeDisposable compositeDisposable) {
        super(dataManager, schedulerProvider, compositeDisposable);
    }

    @Inject
    @ActivityContext
    AuthorizationService mAuthorizationService;

    @Override
    public void onAttach(V mvpView) {
        super.onAttach(mvpView);

        decideNextActivity();
    }

    @Override
    public void onGoogleLoginClick() {
        getMvpView().showLoading();
        try {
            doGoogleRequestAuth();
            getMvpView().hideLoading();
        } catch (Exception ex){
            getMvpView().hideLoading();
            getMvpView().showMessage(ex.getMessage());
        }
    }

    @Override
    public void doGoogleRequestAuth() {
        try {
            String clientId = BuildConfig.GOOGLE_CLIENT_ID;
            Uri redirectUri = Uri.parse("com.edwiinn.project:/oauth2callback");
            AuthorizationServiceConfiguration serviceConfiguration = new AuthorizationServiceConfiguration(
                    Uri.parse(ApiEndPoint.ENDPOINT_GOOGLE_LOGIN_AUTH),
                    Uri.parse(ApiEndPoint.ENDPOINT_GOOGLE_LOGIN_TOKEN)
            );
            AuthorizationRequest.Builder builder = new AuthorizationRequest.Builder(
                    serviceConfiguration,
                    clientId,
                    AuthorizationRequest.RESPONSE_TYPE_CODE,
                    redirectUri
            );
            builder.setScopes("profile");
            AuthorizationRequest request = builder.build();
            String action = "com.edwiinn.project.HANDLE_AUTHORIZATION_RESPONSE";
            Intent postAuthorizationIntent = new Intent(action);
            postAuthorizationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(mActivityContext, request.hashCode(), postAuthorizationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            mAuthorizationService.performAuthorizationRequest(request, pendingIntent);
            getMvpView().hideLoading();
        } catch (Exception exception) {
            getMvpView().hideLoading();
            getMvpView().showMessage(exception.getMessage());
        }
    }

    @Override
    public void doGoogleRequestToken(AuthorizationResponse response, AuthorizationException error) {
        getMvpView().showLoading();
        try {
            final AuthState authState = getDataManager().getCurrentAuthState();
            mAuthorizationService.performTokenRequest(response.createTokenExchangeRequest(), new AuthorizationService.TokenResponseCallback() {
                @Override
                public void onTokenRequestCompleted(@Nullable TokenResponse tokenResponse, @Nullable AuthorizationException exception) {
                    if (exception != null) {
                        Log.w("Token", "Token Exchange failed", exception);
                        getMvpView().onError(exception.errorDescription);
                        getMvpView().hideLoading();
                    } else {
                        if (tokenResponse != null) {
                            Log.d("Acess Token", tokenResponse.accessToken);
                            getDataManager().updateUserInfo(
                                    "Bearer " + tokenResponse.accessToken,
                                    tokenResponse.idToken,
                                    DataManager.LoggedInMode.LOGGED_IN_MODE_GOOGLE,
                                    null,
                                    null,
                                    null);
                            getMvpView().hideLoading();
                            getDataManager().updateAuthState(tokenResponse, exception);
                            loadGoogleUserInfo();
                        }
                    }
                }
            });
        } catch (Exception exception) {
            getMvpView().hideLoading();
            getMvpView().onError(exception.getMessage());
        }
    }

    @Override
    public void loadGoogleUserInfo() {
        getMvpView().showLoading();
        try {
            getCompositeDisposable().add(getDataManager()
                    .getGoogleUserInformation()
                    .subscribeOn(getSchedulerProvider().io())
                    .observeOn(getSchedulerProvider().ui())
                    .subscribe(new Consumer<GoogleResponse.UserInfo>() {
                        @Override
                        public void accept(GoogleResponse.UserInfo userInfo) throws Exception {
                            getDataManager().updateUserInfo(
                                    getDataManager().getAccessToken(),
                                    getDataManager().getCurrentUserId(),
                                    DataManager.LoggedInMode.LOGGED_IN_MODE_GOOGLE,
                                    userInfo.getName(),
                                    null,
                                    null
                            );
                            onLoadCertificate();
                            getMvpView().hideLoading();
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(@NonNull Throwable throwable)
                                throws Exception {
                            if (!isViewAttached()) {
                                return;
                            }

                            getMvpView().hideLoading();

                            // handle the error here
                            if (throwable instanceof ANError) {
                                ANError anError = (ANError) throwable;
                                handleApiError(anError);
                            }
                        }
                    }));
        } catch (Exception exception){
            getMvpView().onError(exception.getMessage());
        }
    }

    @Override
    public void doGoogleRequestFreshToken() {
        AuthState authState = getDataManager().getCurrentAuthState();
        authState.performActionWithFreshTokens(mAuthorizationService, new AuthState.AuthStateAction() {
            @Override
            public void execute(@Nullable String accessToken, @Nullable String idToken, @Nullable AuthorizationException exception) {
                if (exception != null){
                    getMvpView().onError(exception.getMessage());
                    return;
                }

                getDataManager().updateUserInfo(
                        accessToken,
                        idToken,
                        DataManager.LoggedInMode.LOGGED_IN_MODE_GOOGLE,
                        getDataManager().getCurrentUserName(),
                        null,
                        null
                );
            }
        });
    }

    @Override
    public void onLoadCertificate() {
        getMvpView().showLoading();
        File file = new File(getDataManager().getCertificateLocation());
        if(file.exists()) file.delete();

        try {
            KeyPair keyPair = getDataManager().getDocumentKeyPair();
            PKCS10CertificationRequest csr = CsrUtils.generateCSR(keyPair, "project.edwiinn.com", "ITS", "Informatika");
            CertificateRequest request = new CertificateRequest(CsrUtils.toPemFormat(csr));
            Log.d("CSRprincipal", CsrUtils.toPemFormat(csr));
            getCompositeDisposable().add(getDataManager()
                    .requestSignCsr(request)
                    .subscribeOn(getSchedulerProvider().io())
                    .observeOn(getSchedulerProvider().ui())
                    .subscribe(new Consumer<CertificateResponse>() {
                        @Override
                        public void accept(CertificateResponse certificateResponse) throws Exception {
                            File file = new File(getDataManager().getCertificateLocation());
                            try {
                                if (!file.exists()) {
                                    file.getParentFile().mkdir();
                                    file.createNewFile();
                                }
                                FileWriter fileWriter = new FileWriter(file, true);
                                fileWriter.append(certificateResponse.getCertificate().getCertificatePem());
                                fileWriter.flush();
                                fileWriter.close();
                                getMvpView().hideLoading();
                                decideNextActivity();
                            } catch (IOException e) {
                                e.printStackTrace();
                                getMvpView().hideLoading();
                                getMvpView().showMessage(e.getMessage());
                            }
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            if (!isViewAttached()) {
                                return;
                            }

                            getMvpView().hideLoading();

                            // handle the error here
                            if (throwable instanceof ANError) {
                                ANError anError = (ANError) throwable;
                                handleApiError(anError);
                            }
                        }
                    })
            );
        } catch (Exception exception) {
            getMvpView().showMessage(exception.getMessage());
            getMvpView().hideLoading();
        }
    }

    private void decideNextActivity(){
        if (
            getDataManager().getCurrentUserLoggedInMode() != DataManager.LoggedInMode.LOGGED_IN_MODE_LOGGED_OUT.getType() &&
            new File(getDataManager().getCertificateLocation()).exists()
        ) {
            getMvpView().openMainActivity();
        }
    }
}
