package com.example.studyloopandroid.data.local;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Central place for reacting to an invalid or expired authentication token.
 *
 * <p>When the API answers with HTTP 401 the stored token is cleared and the
 * login screen is opened with a clean back stack, instead of leaving the user
 * on a screen where every request keeps failing.</p>
 */
public final class SessionExpirationHandler {

    private static final String TAG = "SessionExpirationHandler";

    private static final String LOGIN_ACTIVITY =
            "com.example.studyloopandroid.ui.auth.MainActivity";

    private static final AtomicBoolean redirecting = new AtomicBoolean(false);

    private SessionExpirationHandler() {
    }

    public static void handleExpiredSession(
            Context context,
            TokenStorage tokenStorage
    ) {
        if (context == null || tokenStorage == null) {
            return;
        }

        // Several requests can fail at the same time; only redirect once.
        if (!redirecting.compareAndSet(false, true)) {
            return;
        }

        try {
            tokenStorage.clearToken();
        } catch (Exception e) {
            Log.e(TAG, "Unable to clear the expired session token.", e);
        }

        Context appContext = context.getApplicationContext();

        new Handler(Looper.getMainLooper()).post(() -> {
            try {
                Intent intent = new Intent();
                intent.setClassName(
                        appContext.getPackageName(),
                        LOGIN_ACTIVITY
                );
                intent.addFlags(
                        Intent.FLAG_ACTIVITY_NEW_TASK
                                | Intent.FLAG_ACTIVITY_CLEAR_TASK
                );
                intent.putExtra("sessionExpired", true);

                appContext.startActivity(intent);
            } catch (Exception e) {
                redirecting.set(false);
                Log.e(TAG, "Unable to open the login screen.", e);
            }
        });
    }

    /**
     * Allows a future expiration to be handled again, for example after the
     * login screen has been shown and the user authenticated once more.
     */
    public static void reset() {
        redirecting.set(false);
    }
}
