package com.example.studyloopandroid.data.local;

import android.content.Context;
import android.content.SharedPreferences;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Base64;

import java.nio.charset.StandardCharsets;
import java.security.KeyStore;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;

public class TokenStorage {

    private static final String PREFS_NAME = "studyloop_session";
    private static final String TOKEN_KEY = "encrypted_token";
    private static final String IV_KEY = "token_iv";
    private static final String KEY_ALIAS = "studyloop_token_key";

    private final SharedPreferences preferences;

    public TokenStorage(Context context) {
        preferences = context.getApplicationContext()
                .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public void saveToken(String token) throws Exception {
        SecretKey key = getOrCreateKey();

        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE, key);

        byte[] encrypted = cipher.doFinal(
                token.getBytes(StandardCharsets.UTF_8)
        );

        String encryptedToken = Base64.encodeToString(
                encrypted, Base64.NO_WRAP
        );

        String iv = Base64.encodeToString(
                cipher.getIV(), Base64.NO_WRAP
        );

        preferences.edit()
                .putString(TOKEN_KEY, encryptedToken)
                .putString(IV_KEY, iv)
                .apply();
    }

    public String getToken() throws Exception {
        String encryptedToken = preferences.getString(TOKEN_KEY, null);
        String ivString = preferences.getString(IV_KEY, null);

        if (encryptedToken == null || ivString == null) {
            return null;
        }

        SecretKey key = getOrCreateKey();

        byte[] encrypted = Base64.decode(
                encryptedToken, Base64.NO_WRAP
        );

        byte[] iv = Base64.decode(ivString, Base64.NO_WRAP);

        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");

        cipher.init(
                Cipher.DECRYPT_MODE,
                key,
                new GCMParameterSpec(128, iv)
        );

        byte[] decrypted = cipher.doFinal(encrypted);

        return new String(decrypted, StandardCharsets.UTF_8);
    }

    public void clearToken() {
        preferences.edit()
                .remove(TOKEN_KEY)
                .remove(IV_KEY)
                .apply();
    }

    private SecretKey getOrCreateKey() throws Exception {
        KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
        keyStore.load(null);

        if (keyStore.containsAlias(KEY_ALIAS)) {
            return (SecretKey) keyStore.getKey(KEY_ALIAS, null);
        }

        KeyGenerator keyGenerator = KeyGenerator.getInstance(
                KeyProperties.KEY_ALGORITHM_AES,
                "AndroidKeyStore"
        );

        KeyGenParameterSpec spec = new KeyGenParameterSpec.Builder(
                KEY_ALIAS,
                KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT
        )
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setRandomizedEncryptionRequired(true)
                .build();

        keyGenerator.init(spec);

        return keyGenerator.generateKey();
    }
}