package com.edwiinn.project.data.prefs;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import net.openid.appauth.AuthState;
import net.openid.appauth.AuthorizationException;
import net.openid.appauth.TokenResponse;

public interface AuthStateManager {

    AuthState getCurrentAuthState();

    void clearAuthState();

    AuthState replaceAuthState(@NonNull AuthState state);

    AuthState updateAuthState(@Nullable TokenResponse response, @Nullable AuthorizationException ex);
}
