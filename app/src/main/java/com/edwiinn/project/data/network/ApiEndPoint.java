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

import com.edwiinn.project.BuildConfig;

/**
 * Created by amitshekhar on 01/02/17.
 */

public final class ApiEndPoint {

    public static final String ENDPOINT_GOOGLE_LOGIN_AUTH = BuildConfig.GOOGLE_ACCOUNT_URL
            + "/o/oauth2/v2/auth";

    public static final String ENDPOINT_GOOGLE_LOGIN_TOKEN = BuildConfig.GOOGLE_API_URL
            + "/oauth2/v4/token";

    public static final String ENDPOINT_GOOGLE_USER_INFO = BuildConfig.GOOGLE_API_URL
            + "/oauth2/v3/userinfo";

    public static final String ENDPOINT_ITS_LOGIN_TOKEN = BuildConfig.ITS_URL
            + "/token";

    public static final String ENDPOINT_ITS_LOGIN_AUTH =  BuildConfig.ITS_URL
            + "/authorize";

    public static final String ENDPOINT_ITS_USER_INFO = BuildConfig.ITS_URL
            + "/userinfo";

    public static final String ENDPOINT_ITS_LOGOUT =  BuildConfig.ITS_URL
            + "/signout/global";

    public static final String ENDPOINT_DOCUMENTS = BuildConfig.BASE_URL
            + "/documents";

    public static final String ENDPOINT_SIGNED_DOCUMENTS = BuildConfig.BASE_URL
            + "/signed-documents";

    public static final String ENDPOINT_SIGN_CSR = BuildConfig.ITS_CFSSL_URL
            + "/api/v1/cfssl/sign";

    private ApiEndPoint() {
        // This class is not publicly instantiable
    }

}
