package com.example.studyloopandroid.ui.auth;

import com.example.studyloopandroid.R;
import com.example.studyloopandroid.ui.dashboard.DashboardActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.studyloopandroid.data.local.SessionExpirationHandler;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {


    private EditText etEmail;
    private EditText etPassword;
    private Button btnLogin;
    private ProgressBar progressBar;
    private TextView tvError;

    private AuthViewModel authViewModel;

    private TextView tvGoToRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // A new login screen is now visible, so future session expirations
        // may redirect again.
        SessionExpirationHandler.reset();

        if (getIntent() != null
                && getIntent().getBooleanExtra("sessionExpired", false)) {
            Toast.makeText(
                    this,
                    getString(R.string.session_expired_message),
                    Toast.LENGTH_LONG
            ).show();
        }

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        progressBar = findViewById(R.id.progressBar);
        tvError = findViewById(R.id.tvError);
        tvGoToRegister = findViewById(R.id.tvGoToRegister);

        authViewModel = new ViewModelProvider(this)
                .get(AuthViewModel.class);

        observeViewModel();

        btnLogin.setOnClickListener(v -> {
            String email = etEmail.getText().toString();
            String password = etPassword.getText().toString();

            authViewModel.login(email, password);
        });

        tvGoToRegister.setOnClickListener(v -> {
            Intent intent = new Intent(
                    MainActivity.this,
                    RegisterActivity.class
            );

            startActivity(intent);
        });
    }

    private void observeViewModel() {

        authViewModel.getLoading().observe(this, isLoading -> {
            boolean loading = Boolean.TRUE.equals(isLoading);

            progressBar.setVisibility(
                    loading ? View.VISIBLE : View.GONE
            );

            btnLogin.setEnabled(!loading);
        });

        authViewModel.getError().observe(this, error -> {
            if (error == null || error.isEmpty()) {
                tvError.setVisibility(View.GONE);
                return;
            }

            tvError.setText(error);
            tvError.setVisibility(View.VISIBLE);
        });

        authViewModel.getLoginResult().observe(this, result -> {
            if (result != null) {
                authViewModel.loadCurrentUser();
            }
        });

        authViewModel.getCurrentUser().observe(this, user -> {
            if (user != null) {
                openHome();
            }
        });

    }
    private void openHome() {
        android.content.Intent intent =
                new android.content.Intent(this, DashboardActivity.class);

        startActivity(intent);
        finish();
    }


}
