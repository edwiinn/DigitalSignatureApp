package com.edwiinn.project.data.prefs;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.edwiinn.project.di.ApplicationContext;
import com.edwiinn.project.di.PreferenceInfo;

import net.openid.appauth.AuthState;
import net.openid.appauth.AuthorizationException;
import net.openid.appauth.TokenResponse;

import org.json.JSONException;

import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantLock;

import javax.inject.Inject;

public class AppAuthStateManager implements AuthStateManager {

    private static final String PREF_KEY_AUTH_STATE_STORE_NAME = "PREF_KEY_AUTH_STATE_STORE_NAME";

    private static final String PREF_KEY_AUTH_STATE_KEY_STATE = "PREF_KEY_AUTH_STATE_KEY_STATE";

    private final SharedPreferences mPrefs;

    private final ReentrantLock mPrefsLock;

    private final AtomicReference<AuthState> mCurrentAuthState;

    @Inject
    public AppAuthStateManager(@ApplicationContext Context context) {
        mPrefs = context.getSharedPreferences(PREF_KEY_AUTH_STATE_STORE_NAME, Context.MODE_PRIVATE);
        mCurrentAuthState = new AtomicReference<>();
        mPrefsLock = new ReentrantLock();
    }


    @Override
    public AuthState getCurrentAuthState() {
        if (mCurrentAuthState.get() != null) {
            return mCurrentAuthState.get();
        }

        AuthState state = readState();
        if (mCurrentAuthState.compareAndSet(null, state)) {
            return state;
        } else {
            return mCurrentAuthState.get();
        }
    }

    @Override
    public void clearAuthState() {
        mPrefs.edit().remove(PREF_KEY_AUTH_STATE_STORE_NAME).apply();
        mCurrentAuthState.set(null);
    }

    @Override
    public AuthState replaceAuthState(@NonNull AuthState state) {
        writeState(state);
        mCurrentAuthState.set(state);
        return state;
    }

    @Override
    public AuthState updateAuthState(@Nullable TokenResponse response, @Nullable AuthorizationException ex) {
        AuthState current = getCurrentAuthState();
        current.update(response, ex);
        return replaceAuthState(current);
    }

    private AuthState readState() {
        mPrefsLock.lock();
        try {
            String currentState = mPrefs.getString(PREF_KEY_AUTH_STATE_KEY_STATE, null);
            if (currentState == null) {
                return new AuthState();
            }

            try {
                return AuthState.fromJson(currentState);
            } catch (JSONException ex) {
                Log.w("AuthState", "Failed to deserialize stored auth state - discarding");
                return new AuthState();
            }
        } finally {
            mPrefsLock.unlock();
        }
    }

    private void writeState(@Nullable AuthState state) {
        mPrefsLock.lock();
        try {
            SharedPreferences.Editor editor = mPrefs.edit();
            if (state == null) {
                editor.remove(PREF_KEY_AUTH_STATE_KEY_STATE);
            } else {
                editor.putString(PREF_KEY_AUTH_STATE_KEY_STATE, state.toJsonString());
            }

            if (!editor.commit()) {
                throw new IllegalStateException("Failed to write state to shared prefs");
            }
        } finally {
            mPrefsLock.unlock();
        }
    }
}

