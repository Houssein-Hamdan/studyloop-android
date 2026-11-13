package com.example.studyloopandroid.ui.common;

import android.content.Context;

/**
 * Small shared helpers for view-related calculations.
 */
public final class ViewUtils {

    private ViewUtils() {
    }

    /**
     * Converts a dp value to the equivalent pixel value for the given context.
     */
    public static int dpToPx(Context context, int dp) {
        return (int) (dp * context.getResources()
                .getDisplayMetrics()
                .density);
    }
}
