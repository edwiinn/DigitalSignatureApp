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

package com.edwiinn.project.di.module;

import android.app.Application;
import android.content.Context;
import android.security.keystore.KeyProperties;

import com.edwiinn.project.BuildConfig;
import com.edwiinn.project.R;
import com.edwiinn.project.data.AppDataManager;
import com.edwiinn.project.data.DataManager;
import com.edwiinn.project.data.db.AppDbHelper;
import com.edwiinn.project.data.db.DbHelper;
import com.edwiinn.project.data.network.ApiHeader;
import com.edwiinn.project.data.network.ApiHelper;
import com.edwiinn.project.data.network.AppApiHelper;
import com.edwiinn.project.data.prefs.AppAuthStateManager;
import com.edwiinn.project.data.prefs.AppPreferencesHelper;
import com.edwiinn.project.data.prefs.AuthStateManager;
import com.edwiinn.project.data.prefs.PreferencesHelper;
import com.edwiinn.project.di.ApiInfo;
import com.edwiinn.project.di.ApplicationContext;
import com.edwiinn.project.di.DatabaseInfo;
import com.edwiinn.project.di.PreferenceInfo;
import com.edwiinn.project.utils.AppConstants;

import net.openid.appauth.AuthorizationService;

import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by janisharali on 27/01/17.
 */

@Module
public class ApplicationModule {

    private final Application mApplication;

    public ApplicationModule(Application application) {
        mApplication = application;
    }

    @Provides
    @ApplicationContext
    Context provideContext() {
        return mApplication;
    }

    @Provides
    Application provideApplication() {
        return mApplication;
    }

    @Provides
    @DatabaseInfo
    String provideDatabaseName() {
        return AppConstants.DB_NAME;
    }

    @Provides
    @ApiInfo
    String provideApiKey() {
        return BuildConfig.API_KEY;
    }

    @Provides
    @PreferenceInfo
    String providePreferenceName() {
        return AppConstants.PREF_NAME;
    }

    @Provides
    @Singleton
    DataManager provideDataManager(AppDataManager appDataManager) {
        return appDataManager;
    }

    @Provides
    @Singleton
    DbHelper provideDbHelper(AppDbHelper appDbHelper) {
        return appDbHelper;
    }

    @Provides
    @Singleton
    AuthStateManager provideAuthStateManager(AppAuthStateManager authStateManager){
        return authStateManager;
    }

    @Provides
    @Singleton
    PreferencesHelper providePreferencesHelper(AppPreferencesHelper appPreferencesHelper) {
        return appPreferencesHelper;
    }

    @Provides
    @Singleton
    ApiHelper provideApiHelper(AppApiHelper appApiHelper) {
        return appApiHelper;
    }

    @Provides
    @Singleton
    ApiHeader.ProtectedApiHeader provideProtectedApiHeader(@ApiInfo String apiKey,
                                                           PreferencesHelper preferencesHelper) {
        return new ApiHeader.ProtectedApiHeader(
                apiKey,
                preferencesHelper.getCurrentUserId(),
                preferencesHelper.getAccessToken());
    }

    @Provides
    @Singleton
    CalligraphyConfig provideCalligraphyDefaultConfig() {
        return new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/source-sans-pro/SourceSansPro-Regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build();
    }

    @Provides
    KeyPairGenerator provideKeyPairGenerator(){
        try {
            return KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_RSA, AppConstants.ANDROID_KEYSTORE);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
            return null;
        }
    }
}
