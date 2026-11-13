package com.example.studyloopandroid.data.repository;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;

import retrofit2.Response;

/**
 * Single place that converts failed HTTP responses and transport exceptions
 * into short, user-facing messages.
 *
 * <p>Repositories and ViewModels must never surface raw exception text.</p>
 */
public final class ApiErrorMapper {

    /**
     * Sentinel used by {@code AuthRepository.getCurrentUser} so that the
     * startup flow can distinguish an expired token from other failures.
     */
    public static final String SESSION_EXPIRED = "SESSION_EXPIRED";

    private static final String NETWORK_ERROR_MESSAGE =
            "Network error. Please check your connection.";

    private static final String UNEXPECTED_ERROR_MESSAGE =
            "Something went wrong. Please try again.";

    private static final String SESSION_EXPIRED_MESSAGE =
            "Your session has expired. Please sign in again.";

    private static final String FORBIDDEN_MESSAGE =
            "You do not have permission to perform this action.";

    private ApiErrorMapper() {
    }

    /**
     * Maps an unsuccessful response to a user-facing message.
     *
     * @param response the Retrofit response
     * @param fallback short action-specific prefix such as
     *                 {@code "Failed to load lessons."}
     */
    public static String fromResponse(
            Response<?> response,
            String fallback
    ) {
        if (response == null) {
            return fallback;
        }

        int code = response.code();

        if (code == 401) {
            return SESSION_EXPIRED_MESSAGE;
        }

        if (code == 403) {
            return FORBIDDEN_MESSAGE;
        }

        String backendMessage = backendMessage(response);

        if (backendMessage != null) {
            return backendMessage;
        }

        return fallback + " HTTP " + code;
    }

    /**
     * Maps a transport or unexpected exception to a user-facing message
     * without exposing the exception details.
     */
    public static String fromThrowable(Throwable throwable) {
        if (throwable instanceof IOException) {
            return NETWORK_ERROR_MESSAGE;
        }

        return UNEXPECTED_ERROR_MESSAGE;
    }

    /**
     * Reads the backend's {@code message} field, which may be a string or an
     * array of validation messages.
     *
     * @return the message, or {@code null} when the body is missing or does
     *         not follow the expected contract.
     */
    public static String backendMessage(Response<?> response) {
        if (response == null || response.errorBody() == null) {
            return null;
        }

        try {
            String body = response.errorBody().string();

            if (body == null || body.trim().isEmpty()) {
                return null;
            }

            JsonElement parsed = JsonParser.parseString(body);

            if (!parsed.isJsonObject()) {
                return null;
            }

            JsonObject json = parsed.getAsJsonObject();
            JsonElement message = json.get("message");

            if (message == null || message.isJsonNull()) {
                return null;
            }

            if (message.isJsonPrimitive()) {
                String value = message.getAsString();

                return value.isEmpty() ? null : value;
            }

            if (message.isJsonArray()) {
                StringBuilder result = new StringBuilder();

                for (JsonElement item : message.getAsJsonArray()) {
                    if (!item.isJsonPrimitive()) {
                        continue;
                    }

                    if (result.length() > 0) {
                        result.append("\n");
                    }

                    result.append(item.getAsString());
                }

                return result.length() > 0 ? result.toString() : null;
            }

        } catch (Exception e) {
            // Malformed error body: let the caller use its fallback.
        }

        return null;
    }
}
