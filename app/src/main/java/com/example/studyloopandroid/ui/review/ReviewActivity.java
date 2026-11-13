package com.example.studyloopandroid.ui.review;

import com.example.studyloopandroid.R;
import com.example.studyloopandroid.ui.common.BaseActivity;
import com.example.studyloopandroid.ui.learning.LessonDetailsActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import android.graphics.Color;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import com.example.studyloopandroid.data.model.progress.DueReview;

public class ReviewActivity extends BaseActivity {

    private ReviewViewModel viewModel;

    private TextView tvDueReviewCount;
    private TextView tvReviewError;
    private TextView tvNoDueReviews;

    private ProgressBar reviewProgressBar;
    private Button btnRetryReviews;
    private LinearLayout dueReviewsContainer;

    private final Map<String, TextView> nextReviewDateViews = new HashMap<>();
    private final Map<String, Button> completeReviewButtons = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        setupBottomNavigation(R.id.nav_review);

        View root = findViewById(R.id.reviewRoot);

        ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
            Insets systemBars = insets.getInsets(
                    WindowInsetsCompat.Type.systemBars()
            );

            v.setPadding(
                    systemBars.left,
                    systemBars.top,
                    systemBars.right,
                    0
            );

            return insets;
        });

        tvDueReviewCount = findViewById(R.id.tvDueReviewCount);
        tvReviewError = findViewById(R.id.tvReviewError);
        tvNoDueReviews = findViewById(R.id.tvNoDueReviews);

        reviewProgressBar = findViewById(R.id.reviewProgressBar);
        btnRetryReviews = findViewById(R.id.btnRetryReviews);
        dueReviewsContainer = findViewById(R.id.dueReviewsContainer);

        viewModel = new ViewModelProvider(this)
                .get(ReviewViewModel.class);

        observeViewModel();

        btnRetryReviews.setOnClickListener(v ->
                viewModel.loadDueReviews()
        );

        viewModel.getReviewActionMessage().observe(this, message -> {
            if (message != null) {
                android.widget.Toast.makeText(
                        this,
                        message,
                        android.widget.Toast.LENGTH_SHORT
                ).show();
            }
        });

        viewModel.loadDueReviews();
    }

    private void observeViewModel() {

        viewModel.getIsLoading().observe(this, isLoading -> {
            if (isLoading != null) {
                reviewProgressBar.setVisibility(
                        isLoading ? View.VISIBLE : View.GONE
                );
            }
        });

        viewModel.getErrorMessage().observe(this, error -> {
            boolean hasError = error != null && !error.isEmpty();

            tvReviewError.setVisibility(
                    hasError ? View.VISIBLE : View.GONE
            );

            btnRetryReviews.setVisibility(
                    hasError ? View.VISIBLE : View.GONE
            );

            if (hasError) {
                tvReviewError.setText(error);
            }
        });

        // Register these observers once, not once per card. The card views
        // are looked up by lesson id when an update arrives.
        viewModel.getCompletedReview().observe(this, result -> {
            if (result == null || result.getProgress() == null) {
                return;
            }

            String lessonId = result.getProgress().getLessonId();

            if (lessonId == null) {
                return;
            }

            TextView tvNextReviewDate = nextReviewDateViews.get(lessonId);

            if (tvNextReviewDate == null) {
                return;
            }

            String nextReviewDate =
                    result.getProgress().getNextReviewDate();

            if (nextReviewDate != null) {
                tvNextReviewDate.setText(
                        getString(R.string.review_next_review_format, formatReviewDate(nextReviewDate))
                );
                tvNextReviewDate.setVisibility(View.VISIBLE);
            }
        });

        viewModel.getCompletingLessonId().observe(this, completingId -> {
            for (Map.Entry<String, Button> entry
                    : completeReviewButtons.entrySet()) {

                boolean isCompleting =
                        Objects.equals(entry.getKey(), completingId);

                Button button = entry.getValue();

                button.setEnabled(!isCompleting);
                button.setText(
                        isCompleting
                                ? getString(R.string.review_completing)
                                : getString(R.string.review_complete)
                );
            }
        });

        viewModel.getDueReviews().observe(this, reviews -> {
            dueReviewsContainer.removeAllViews();
            nextReviewDateViews.clear();
            completeReviewButtons.clear();

            if (reviews == null || reviews.isEmpty()) {
                tvDueReviewCount.setText(getString(R.string.review_count_many, 0));
                tvNoDueReviews.setVisibility(View.VISIBLE);
                return;
            }

            tvDueReviewCount.setText(
                    reviews.size() == 1
                            ? getString(
                                    R.string.review_count_one,
                                    reviews.size()
                            )
                            : getString(
                                    R.string.review_count_many,
                                    reviews.size()
                            )
            );

            tvNoDueReviews.setVisibility(View.GONE);

            for (DueReview review : reviews) {
                addReviewCard(review);
            }
        });
    }

    private void addReviewCard(@NonNull DueReview review) {

        TextView title = new TextView(this);

        if (review.getLesson() != null
                && review.getLesson().getTitle() != null) {

            title.setText(review.getLesson().getTitle());

        } else {
            title.setText(getString(R.string.review_untitled_lesson));
        }

        title.setTextColor(ContextCompat.getColor(this, R.color.text_primary_deep));
        title.setTextSize(17);
        title.setTypeface(null, android.graphics.Typeface.BOLD);

        title.setPadding(20, 20, 20, 12);

        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setBackgroundColor(ContextCompat.getColor(this, R.color.white));

        LinearLayout.LayoutParams cardParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        TextView tvNextReviewDate = new TextView(this);
        tvNextReviewDate.setTextColor(ContextCompat.getColor(this, R.color.brand_indigo_dark));
        tvNextReviewDate.setTextSize(13);
        tvNextReviewDate.setVisibility(View.GONE);

        card.addView(tvNextReviewDate);

        nextReviewDateViews.put(review.getLessonId(), tvNextReviewDate);

        cardParams.bottomMargin = dpToPx(12);

        card.setLayoutParams(cardParams);
        card.addView(title);

        TextView progress = new TextView(this);

        progress.setText(
                getString(
                        R.string.dashboard_topics_completed,
                        review.getCompletedTopicsCount(),
                        review.getTotalTopicsCount()
                )
        );

        progress.setTextColor(ContextCompat.getColor(this, R.color.text_secondary));
        progress.setTextSize(14);
        progress.setPadding(20, 0, 20, 12);

        card.addView(progress);

        TextView interval = new TextView(this);

        interval.setText(
                getString(
                        R.string.review_interval_format,
                        review.getReviewIntervalDays()
                )
        );

        interval.setTextColor(ContextCompat.getColor(this, R.color.brand_indigo_dark));
        interval.setTextSize(13);
        interval.setPadding(20, 0, 20, 20);

        card.addView(interval);

        Button completeButton = new Button(this);
        completeButton.setText(getString(R.string.review_complete));

        completeButton.setOnClickListener(v -> {
            completeButton.setEnabled(false);
            completeButton.setText(getString(R.string.review_completing));
            viewModel.completeReview(review);
        });

        completeReviewButtons.put(review.getLessonId(), completeButton);

        card.addView(completeButton);

        card.setOnClickListener(v -> {
            if (review.getLesson() == null) {
                return;
            }

            android.content.Intent intent = new android.content.Intent(
                    ReviewActivity.this,
                    LessonDetailsActivity.class
            );

            intent.putExtra(
                    "containerId",
                    review.getLesson().getContainerId()
            );

            intent.putExtra(
                    "lessonId",
                    review.getLessonId()
            );

            startActivity(intent);
        });

        dueReviewsContainer.addView(card);
    }

    private int dpToPx(int dp) {
        return (int) (dp * getResources()
                .getDisplayMetrics().density);
    }

    private String formatReviewDate(String dateString) {
        if (dateString == null || dateString.isEmpty()) {
            return getString(R.string.review_not_scheduled);
        }

        try {
            Instant instant = Instant.parse(dateString);

            DateTimeFormatter formatter = DateTimeFormatter
                    .ofPattern("MMM dd, yyyy", Locale.ENGLISH)
                    .withZone(ZoneId.systemDefault());

            return formatter.format(instant);

        } catch (Exception e) {
            return dateString;
        }
    }

}
