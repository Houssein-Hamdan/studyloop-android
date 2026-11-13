package com.example.studyloopandroid.ui.analytics;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.example.studyloopandroid.R;

public class CircularProgressView extends View {

    private final Paint backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint progressPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint percentagePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint labelPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private final RectF arcBounds = new RectF();

    private int progress = 0;

    private int backgroundColor;
    private int progressColor;
    private int textColor;
    private int labelColor;

    public CircularProgressView(Context context) {
        super(context);
        init();
    }

    public CircularProgressView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CircularProgressView(
            Context context,
            @Nullable AttributeSet attrs,
            int defStyleAttr
    ) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        backgroundColor = ContextCompat.getColor(
                getContext(), R.color.progress_track);
        progressColor = ContextCompat.getColor(
                getContext(), R.color.accent_purple);
        textColor = ContextCompat.getColor(
                getContext(), R.color.text_primary_deep_alt);
        labelColor = ContextCompat.getColor(
                getContext(), R.color.text_tertiary_alt);

        backgroundPaint.setStyle(Paint.Style.STROKE);
        backgroundPaint.setStrokeCap(Paint.Cap.ROUND);
        backgroundPaint.setStrokeWidth(dpToPx(10));

        progressPaint.setStyle(Paint.Style.STROKE);
        progressPaint.setStrokeCap(Paint.Cap.ROUND);
        progressPaint.setStrokeWidth(dpToPx(10));

        percentagePaint.setColor(textColor);
        percentagePaint.setTextSize(spToPx(27));
        percentagePaint.setTypeface(
                android.graphics.Typeface.create(
                        "sans-serif-medium",
                        android.graphics.Typeface.NORMAL
                )
        );
        percentagePaint.setTextAlign(Paint.Align.CENTER);

        labelPaint.setColor(labelColor);
        labelPaint.setTextSize(spToPx(10));
        labelPaint.setTypeface(
                android.graphics.Typeface.create(
                        "sans-serif-medium",
                        android.graphics.Typeface.NORMAL
                )
        );
        labelPaint.setTextAlign(Paint.Align.CENTER);

        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    }

    public void setProgress(int progress) {
        this.progress = Math.max(0, Math.min(100, progress));
        invalidate();
    }

    public int getProgress() {
        return progress;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float centerX = getWidth() / 2f;
        float centerY = getHeight() / 2f;

        float strokeWidth = dpToPx(10);
        float radius = Math.min(getWidth(), getHeight()) / 2f
                - strokeWidth - dpToPx(2);

        if (radius <= 0) return;

        backgroundPaint.setColor(backgroundColor);
        progressPaint.setColor(progressColor);

        arcBounds.set(
                centerX - radius,
                centerY - radius,
                centerX + radius,
                centerY + radius
        );

        // Background circle
        canvas.drawArc(
                arcBounds,
                0,
                360,
                false,
                backgroundPaint
        );

        // Progress arc
        if (progress > 0) {
            canvas.drawArc(
                    arcBounds,
                    -90,
                    360f * progress / 100f,
                    false,
                    progressPaint
            );
        }

        // Percentage in the center
        Paint.FontMetrics percentageMetrics =
                percentagePaint.getFontMetrics();

        float percentageY = centerY
                - (percentageMetrics.ascent + percentageMetrics.descent) / 2f;

        canvas.drawText(
                getContext().getString(
                        R.string.analytics_score_format,
                        progress
                ),
                centerX,
                percentageY - dpToPx(5),
                percentagePaint
        );

        // Small label below the percentage
        Paint.FontMetrics labelMetrics = labelPaint.getFontMetrics();

        float labelY = centerY
                - (labelMetrics.ascent + labelMetrics.descent) / 2f
                + dpToPx(20);

        canvas.drawText(
                getContext().getString(R.string.analytics_complete_label),
                centerX,
                labelY,
                labelPaint
        );
    }

    private float dpToPx(float dp) {
        return dp * getResources().getDisplayMetrics().density;
    }

    private float spToPx(float sp) {
        return sp * getResources().getDisplayMetrics().scaledDensity;
    }

}
