package com.example.studyloopandroid.ui.auth;

import com.example.studyloopandroid.R;
import com.example.studyloopandroid.ui.dashboard.DashboardActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.studyloopandroid.data.local.TokenStorage;
import androidx.core.splashscreen.SplashScreen;

public class StartupActivity extends AppCompatActivity {

    private static final long SESSION_CHECK_TIMEOUT_MS = 20000L;

    private AuthViewModel authViewModel;
    private boolean navigationHandled = false;

    private final Handler sessionTimeoutHandler =
            new Handler(Looper.getMainLooper());

    private final Runnable sessionTimeoutRunnable = () -> {
        if (navigationHandled || isFinishing()) {
            return;
        }

        Toast.makeText(
                this,
                getString(R.string.startup_session_verify_failed),
                Toast.LENGTH_LONG
        ).show();

        openLogin();
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SplashScreen.installSplashScreen(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startup);

        authViewModel = new ViewModelProvider(this)
                .get(AuthViewModel.class);

        observeSession();
        checkSession();

        // Safety net: if the session check never reports a result, do not
        // leave the user stuck on the splash screen forever.
        sessionTimeoutHandler.postDelayed(
                sessionTimeoutRunnable,
                SESSION_CHECK_TIMEOUT_MS
        );
    }

    @Override
    protected void onDestroy() {
        sessionTimeoutHandler.removeCallbacks(sessionTimeoutRunnable);
        super.onDestroy();
    }

    private void checkSession() {
        try {
            TokenStorage tokenStorage = new TokenStorage(this);
            String token = tokenStorage.getToken();

            if (token == null || token.trim().isEmpty()) {
                openLogin();
                return;
            }

            authViewModel.checkSession();

        } catch (Exception e) {
            new TokenStorage(this).clearToken();
            openLogin();
        }
    }

    private void observeSession() {
        authViewModel.getSessionStatus().observe(this, status -> {
            if (status == null || navigationHandled) {
                return;
            }

            switch (status) {
                case VALID:
                    openDashboard();
                    break;

                case INVALID:
                    openLogin();
                    break;

                case NETWORK_ERROR:
                    Toast.makeText(
                            this,
                            getString(R.string.startup_network_error),
                            Toast.LENGTH_LONG
                    ).show();

                    openLogin();
                    break;
            }
        });
    }

    private void openLogin() {
        if (navigationHandled || isFinishing()) {
            return;
        }

        navigationHandled = true;

        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    private void openDashboard() {
        if (navigationHandled || isFinishing()) {
            return;
        }

        navigationHandled = true;

        startActivity(new Intent(this, DashboardActivity.class));
        finish();
    }

}