package com.example.studyloopandroid.ui.auth;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.studyloopandroid.data.local.TokenStorage;
import com.example.studyloopandroid.data.model.auth.AuthResponse;
import com.example.studyloopandroid.data.model.auth.LoginRequest;
import com.example.studyloopandroid.data.model.auth.RegisterRequest;
import com.example.studyloopandroid.data.repository.ApiCallback;
import com.example.studyloopandroid.data.repository.AuthRepository;
import com.example.studyloopandroid.data.model.auth.UserResponse;
import com.example.studyloopandroid.data.local.TokenStorage;

public class AuthViewModel extends AndroidViewModel {
    public enum SessionStatus { VALID, INVALID, NETWORK_ERROR }

    private final AuthRepository repository;
    private final TokenStorage tokenStorage;

    private final MutableLiveData<Boolean> loading =
            new MutableLiveData<>(false);

    private final MutableLiveData<String> error =
            new MutableLiveData<>();

    private final MutableLiveData<AuthResponse> loginResult =
            new MutableLiveData<>();

    private final MutableLiveData<UserResponse> currentUser =
            new MutableLiveData<>();

    private final MutableLiveData<AuthResponse> registerResult =
            new MutableLiveData<>();

    private final MutableLiveData<SessionStatus> sessionStatus = new MutableLiveData<>();

    public LiveData<SessionStatus> getSessionStatus() { return sessionStatus; }

    public AuthViewModel(@NonNull Application application) {
        super(application);

        repository = new AuthRepository(application);
        tokenStorage = new TokenStorage(application);
    }

    public LiveData<Boolean> getLoading() {
        return loading;
    }

    public LiveData<String> getError() {
        return error;
    }

    public LiveData<UserResponse> getCurrentUser() {
        return currentUser;
    }


    public LiveData<AuthResponse> getLoginResult() {
        return loginResult;
    }

    public LiveData<AuthResponse> getRegisterResult() {
        return registerResult;
    }

    public void login(String email, String password) {

        if (email.trim().isEmpty() || password.isEmpty()) {
            error.setValue("Email and password are required.");
            return;
        }

        loading.setValue(true);
        error.setValue(null);
        loginResult.setValue(null);

        LoginRequest request =
                new LoginRequest(email.trim(), password);

        repository.login(request, new ApiCallback<AuthResponse>() {

            @Override
            public void onSuccess(AuthResponse result) {
                String token = result.getAccessToken();

                if (token == null || token.trim().isEmpty()) {
                    loading.postValue(false);
                    error.postValue("Login succeeded, but the server returned no token.");
                    return;
                }

                try {
                    tokenStorage.saveToken(token);

                    loading.postValue(false);
                    loginResult.postValue(result);

                } catch (Exception e) {
                    tokenStorage.clearToken();

                    loading.postValue(false);
                    error.postValue("Unable to save your session. Please try again.");
                }
            }

            @Override
            public void onError(String message) {
                loading.postValue(false);
                error.postValue(message);
            }
        });
    }

    public void register(
            String firstName,
            String lastName,
            String email,
            String password
    ) {
        if (firstName.trim().isEmpty()
                || lastName.trim().isEmpty()
                || email.trim().isEmpty()
                || password.isEmpty()) {

            error.setValue("Please fill in all fields.");
            return;

        }

        if (!android.util.Patterns.EMAIL_ADDRESS
                .matcher(email.trim())
                .matches()) {

            error.setValue("Please enter a valid email address.");
            return;

        }

        if (password.length() < 8
                || !password.matches(".*[A-Z].*")
                || !password.matches(".*[a-z].*")
                || !password.matches(".*\\d.*")
                || !password.matches(".*[@$!%*?&].*")) {

            error.setValue(
                    "Password must contain at least 8 characters, " +
                            "an uppercase letter, a lowercase letter, a number, " +
                            "and a special character (@$!%*?&)."
            );
            return;

        }


        loading.setValue(true);
        error.setValue(null);
        registerResult.setValue(null);

        RegisterRequest request = new RegisterRequest(
                email.trim(),
                password,
                firstName.trim(),
                lastName.trim()
        );

        repository.register(request, new ApiCallback<AuthResponse>() {

            @Override
            public void onSuccess(AuthResponse response) {

                if (response.getAccessToken() == null
                        || response.getAccessToken().trim().isEmpty()) {

                    loading.postValue(false);
                    error.postValue(
                            "Account created, but authentication token is missing."
                    );
                    return;
                }

                try {
                    tokenStorage.saveToken(response.getAccessToken());

                    loading.postValue(false);
                    registerResult.postValue(response);

                } catch (Exception e) {
                    tokenStorage.clearToken();
                    loading.postValue(false);
                    error.postValue(
                            "Unable to save your session. Please try again."
                    );
                }
            }

            @Override
            public void onError(String message) {
                loading.postValue(false);
                error.postValue(message);
            }
        });

    }

    public void loadCurrentUser() {

        error.setValue(null);

        repository.getCurrentUser(new ApiCallback<UserResponse>() {

            @Override
            public void onSuccess(UserResponse user) {
                currentUser.postValue(user);
                sessionStatus.postValue(SessionStatus.VALID);
            }

            @Override
            public void onError(String message) {

                if ("SESSION_EXPIRED".equals(message)) {
                    clearSession();
                    sessionStatus.postValue(SessionStatus.INVALID);
                    return;
                }

                if (message != null
                        && message.toLowerCase().contains("network error")) {
                    sessionStatus.postValue(SessionStatus.NETWORK_ERROR);
                } else {
                    // Any other failure (HTTP 500/404, malformed response, etc.)
                    // must still terminate the startup check so the splash
                    // screen cannot hang indefinitely.
                    sessionStatus.postValue(SessionStatus.INVALID);
                }

                error.postValue(message);
            }
        });
    }

    public void checkSession() {

        sessionStatus.setValue(null);

        try {
            String token = tokenStorage.getToken();

            if (token == null || token.trim().isEmpty()) {
                sessionStatus.setValue(SessionStatus.INVALID);
                return;
            }

            loadCurrentUser();

        } catch (Exception e) {
            tokenStorage.clearToken();
            sessionStatus.setValue(SessionStatus.INVALID);
        }
    }


    public void clearSession() {
        tokenStorage.clearToken();
        currentUser.postValue(null);
        loginResult.postValue(null);
    }

}