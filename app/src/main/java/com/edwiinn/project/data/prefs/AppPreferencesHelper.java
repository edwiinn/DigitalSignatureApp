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

package com.edwiinn.project.data.prefs;

import android.content.Context;
import android.content.SharedPreferences;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Log;

import com.edwiinn.project.data.DataManager;
import com.edwiinn.project.di.ApplicationContext;
import com.edwiinn.project.di.PreferenceInfo;
import com.edwiinn.project.utils.AppConstants;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.UnrecoverableEntryException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by janisharali on 27/01/17.
 */

@Singleton
public class AppPreferencesHelper implements PreferencesHelper {

    private static final String PREF_KEY_USER_LOGGED_IN_MODE = "PREF_KEY_USER_LOGGED_IN_MODE";
    private static final String PREF_KEY_CURRENT_USER_ID = "PREF_KEY_CURRENT_USER_ID";
    private static final String PREF_KEY_CURRENT_USER_NAME = "PREF_KEY_CURRENT_USER_NAME";
    private static final String PREF_KEY_CURRENT_USER_EMAIL = "PREF_KEY_CURRENT_USER_EMAIL";
    private static final String PREF_KEY_CURRENT_USER_PROFILE_PIC_URL
            = "PREF_KEY_CURRENT_USER_PROFILE_PIC_URL";
    private static final String PREF_KEY_ACCESS_TOKEN = "PREF_KEY_ACCESS_TOKEN";

    private final SharedPreferences mPrefs;

    @Inject
    public AppPreferencesHelper(@ApplicationContext Context context,
                                @PreferenceInfo String prefFileName) {
        mPrefs = context.getSharedPreferences(prefFileName, Context.MODE_PRIVATE);
    }

    @Inject
    KeyPairGenerator mKeyPairGenerator;

    @Override
    public String getCurrentUserId() {
        String userId = mPrefs.getString(PREF_KEY_CURRENT_USER_ID, "");
        return userId == "" ? null : userId;
    }

    @Override
    public void setCurrentUserId(String userId) {
        String id = userId == null ? "": userId;
        mPrefs.edit().putString(PREF_KEY_CURRENT_USER_ID, id).apply();
    }

    @Override
    public String getCurrentUserName() {
        return mPrefs.getString(PREF_KEY_CURRENT_USER_NAME, null);
    }

    @Override
    public void setCurrentUserName(String userName) {
        mPrefs.edit().putString(PREF_KEY_CURRENT_USER_NAME, userName).apply();
    }

    @Override
    public String getCurrentUserEmail() {
        return mPrefs.getString(PREF_KEY_CURRENT_USER_EMAIL, null);
    }

    @Override
    public void setCurrentUserEmail(String email) {
        mPrefs.edit().putString(PREF_KEY_CURRENT_USER_EMAIL, email).apply();
    }

    @Override
    public String getCurrentUserProfilePicUrl() {
        return mPrefs.getString(PREF_KEY_CURRENT_USER_PROFILE_PIC_URL, null);
    }

    @Override
    public void setCurrentUserProfilePicUrl(String profilePicUrl) {
        mPrefs.edit().putString(PREF_KEY_CURRENT_USER_PROFILE_PIC_URL, profilePicUrl).apply();
    }

    @Override
    public int getCurrentUserLoggedInMode() {
        return mPrefs.getInt(PREF_KEY_USER_LOGGED_IN_MODE,
                DataManager.LoggedInMode.LOGGED_IN_MODE_LOGGED_OUT.getType());
    }

    @Override
    public void setCurrentUserLoggedInMode(DataManager.LoggedInMode mode) {
        mPrefs.edit().putInt(PREF_KEY_USER_LOGGED_IN_MODE, mode.getType()).apply();
    }

    @Override
    public String getAccessToken() {
        return mPrefs.getString(PREF_KEY_ACCESS_TOKEN, null);
    }

    @Override
    public void setAccessToken(String accessToken) {
        mPrefs.edit().putString(PREF_KEY_ACCESS_TOKEN, accessToken).apply();
    }

    @Override
    public KeyPair getDocumentKeyPair(String keyAlias)
            throws
            CertificateException,
            NoSuchAlgorithmException,
            KeyStoreException,
            IOException,
            InvalidAlgorithmParameterException,
            UnrecoverableEntryException {
        if(!isDocumentKeyPairAvailable(keyAlias)){
            return generateDocumentKeyPair(keyAlias);
        }
        KeyStore store = KeyStore.getInstance(AppConstants.ANDROID_KEYSTORE);
        store.load(null);

        KeyStore.Entry entry = store.getEntry(keyAlias, null);
        if (!(entry instanceof KeyStore.PrivateKeyEntry)) {
            return generateDocumentKeyPair(keyAlias);
        }
        KeyStore.PrivateKeyEntry privateKey = (KeyStore.PrivateKeyEntry) entry;
        Certificate cert = store.getCertificate(keyAlias);
        PublicKey publicKey = cert.getPublicKey();

        return new KeyPair(publicKey, privateKey.getPrivateKey());
    }

    private Boolean isDocumentKeyPairAvailable(String keyAlias) throws KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException {
        KeyStore store = KeyStore.getInstance(AppConstants.ANDROID_KEYSTORE);
        store.load(null);
        return store.containsAlias(keyAlias);
    }

    private KeyPair generateDocumentKeyPair(String keyAlias) throws InvalidAlgorithmParameterException {
        mKeyPairGenerator.initialize(
            new KeyGenParameterSpec.Builder(
                    keyAlias,
                    KeyProperties.PURPOSE_SIGN)
                    .setDigests(KeyProperties.DIGEST_SHA256)
                    .setSignaturePaddings(KeyProperties.SIGNATURE_PADDING_RSA_PKCS1)
                    .setKeySize(2048)
                    .build());
        return mKeyPairGenerator.generateKeyPair();
    }
}
