package com.example.studyloopandroid.ui.auth;

import com.example.studyloopandroid.R;
import com.example.studyloopandroid.ui.learning.HomeActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.studyloopandroid.data.model.auth.AuthResponse;

public class RegisterActivity extends AppCompatActivity {


    private EditText etFirstName;
    private EditText etLastName;
    private EditText etEmail;
    private EditText etPassword;

    private Button btnRegister;
    private TextView tvError;
    private TextView tvGoToLogin;
    private ProgressBar progressBar;

    private AuthViewModel authViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initializeViews();

        authViewModel = new ViewModelProvider(this)
                .get(AuthViewModel.class);

        observeViewModel();

        btnRegister.setOnClickListener(v -> submitRegistration());

        tvGoToLogin.setOnClickListener(v -> {
            finish();
        });
    }

    private void initializeViews() {

        etFirstName = findViewById(R.id.etFirstName);
        etLastName = findViewById(R.id.etLastName);
        etEmail = findViewById(R.id.etRegisterEmail);
        etPassword = findViewById(R.id.etRegisterPassword);

        btnRegister = findViewById(R.id.btnRegister);
        tvError = findViewById(R.id.tvRegisterError);
        tvGoToLogin = findViewById(R.id.tvGoToLogin);
        progressBar = findViewById(R.id.registerProgressBar);
    }

    private void submitRegistration() {

        String firstName = etFirstName.getText().toString();
        String lastName = etLastName.getText().toString();
        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();

        authViewModel.register(
                firstName,
                lastName,
                email,
                password
        );
    }

    private void observeViewModel() {

        authViewModel.getLoading().observe(this, isLoading -> {

            boolean loading = Boolean.TRUE.equals(isLoading);

            progressBar.setVisibility(
                    loading ? View.VISIBLE : View.GONE
            );

            btnRegister.setEnabled(!loading);
        });

        authViewModel.getError().observe(this, error -> {

            if (error == null || error.isEmpty()) {
                tvError.setVisibility(View.GONE);
                tvError.setText("");
            } else {
                tvError.setText(error);
                tvError.setVisibility(View.VISIBLE);
            }
        });

        authViewModel.getRegisterResult().observe(this, result -> {

            if (result != null) {
                authViewModel.loadCurrentUser();
            }
        });

        authViewModel.getCurrentUser().observe(this, user -> {

            if (user != null) {
                Intent intent = new Intent(
                        RegisterActivity.this,
                        HomeActivity.class
                );

                // Clear the task so Back from the home screen does not
                // return to the login screen after a successful sign-up.
                intent.setFlags(
                        Intent.FLAG_ACTIVITY_NEW_TASK
                                | Intent.FLAG_ACTIVITY_CLEAR_TASK
                );

                startActivity(intent);
                finish();
            }
        });
    }

}
