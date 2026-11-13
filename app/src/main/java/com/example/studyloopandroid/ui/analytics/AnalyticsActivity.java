package com.example.studyloopandroid.ui.analytics;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import android.widget.ProgressBar;

import com.example.studyloopandroid.ui.common.BaseActivity;
import com.example.studyloopandroid.R;
import com.example.studyloopandroid.data.model.analytics.AnalyticsOverviewResponse;
import android.content.Intent;
import com.example.studyloopandroid.ui.learning.LessonDetailsActivity;
import com.example.studyloopandroid.data.model.lesson.RecentLessonResponse;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;


public class AnalyticsActivity extends BaseActivity {

    private AnalyticsViewModel viewModel;

    private TextView tvGlobalCompletion;
    private TextView tvCompletedTopicsSummary;
    private TextView tvTotalLessons;
    private TextView tvTotalTopics;
    private TextView tvTotalAnnotations;
    private TextView tvTotalQuizzes;
    private TextView tvAverageScore;
    private TextView tvAnalyticsError;
    private TextView tvNoRecentActivity;

    private ProgressBar pbGlobalCompletion;
    private ProgressBar analyticsProgressBar;

    private Button btnRetryAnalytics;
    private LinearLayout recentActivityContainer;
    private ProgressBar pbAverageScore;


    private CircularProgressView circularProgressView;;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analytics);

        bindViews();

        setupBottomNavigation(R.id.nav_analytics);

        viewModel = new ViewModelProvider(this)
                .get(AnalyticsViewModel.class);

        observeViewModel();

        btnRetryAnalytics.setOnClickListener(v ->
                viewModel.loadAnalytics());

        viewModel.loadAnalytics();
    }

    private void bindViews() {
        tvCompletedTopicsSummary = findViewById(R.id.tvCompletedTopicsSummary);
        tvTotalLessons = findViewById(R.id.tvTotalLessons);
        tvTotalTopics = findViewById(R.id.tvTotalTopics);
        tvTotalAnnotations = findViewById(R.id.tvTotalAnnotations);
        tvTotalQuizzes = findViewById(R.id.tvTotalQuizzes);
        tvAverageScore = findViewById(R.id.tvAverageScore);
        tvAnalyticsError = findViewById(R.id.tvAnalyticsError);
        tvNoRecentActivity = findViewById(R.id.tvNoRecentActivity);
        circularProgressView = findViewById(R.id.circularProgressView);

        analyticsProgressBar = findViewById(R.id.analyticsProgressBar);

        pbAverageScore = findViewById(R.id.pbAverageScore);

        btnRetryAnalytics = findViewById(R.id.btnRetryAnalytics);
        recentActivityContainer =
                findViewById(R.id.recentActivityContainer);
    }

    private void observeViewModel() {
        viewModel.getAnalyticsData().observe(this, data -> {
            if (data == null) return;

            displayAnalytics(data);
        });

        viewModel.getIsLoading().observe(this, isLoading -> {
            boolean loading = Boolean.TRUE.equals(isLoading);

            analyticsProgressBar.setVisibility(
                    loading ? View.VISIBLE : View.GONE);

            if (loading) {
                tvAnalyticsError.setVisibility(View.GONE);
                btnRetryAnalytics.setVisibility(View.GONE);
            }
        });

        viewModel.getErrorMessage().observe(this, error -> {
            if (error == null || error.isEmpty()) return;

            tvAnalyticsError.setText(error);
            tvAnalyticsError.setVisibility(View.VISIBLE);
            btnRetryAnalytics.setVisibility(View.VISIBLE);
        });
    }

    private void displayAnalytics(AnalyticsOverviewResponse data) {
        AnalyticsOverviewResponse.Overview overview =
                data.getOverview();

        if (overview != null) {
            int completionRate = overview.getGlobalCompletionRate();

            circularProgressView.setProgress(completionRate);

            tvCompletedTopicsSummary.setText(
                    getString(
                            R.string.dashboard_topics_completed,
                            overview.getCompletedTopics(),
                            overview.getTotalTopics()
                    )
            );

            tvTotalLessons.setText(
                    String.valueOf(overview.getTotalLessons()));

            tvTotalTopics.setText(
                    String.valueOf(overview.getTotalTopics()));

            tvTotalAnnotations.setText(
                    String.valueOf(overview.getTotalAnnotations()));
        }

        AnalyticsOverviewResponse.QuizStats quizzes =
                data.getQuizzes();

        if (quizzes != null) {
            tvTotalQuizzes.setText(
                    String.valueOf(quizzes.getTotalTaken()));

            double averageScore = quizzes.getAverageScore();

            int scoreProgress = (int) Math.round(
                    Math.max(0, Math.min(100, averageScore))
            );

            tvAverageScore.setText(
                    getString(R.string.analytics_score_format, scoreProgress)
            );
            pbAverageScore.setProgress(scoreProgress);
        }

        recentActivityContainer.removeAllViews();

        if (data.getRecentActivity() == null
                || data.getRecentActivity().isEmpty()) {

            tvNoRecentActivity.setVisibility(View.VISIBLE);

        } else {

            tvNoRecentActivity.setVisibility(View.GONE);

            LayoutInflater inflater = LayoutInflater.from(this);

            for (int i = 0; i < data.getRecentActivity().size(); i++) {

                com.example.studyloopandroid.data.model.lesson.RecentLessonResponse lesson =
                        data.getRecentActivity().get(i);

                View lessonView = inflater.inflate(
                        R.layout.item_analytics_recent_lesson,
                        recentActivityContainer,
                        false
                );

                TextView tvLessonTitle =
                        lessonView.findViewById(R.id.tvRecentLessonTitle);

                TextView tvLessonDate =
                        lessonView.findViewById(R.id.tvRecentLessonDate);

                TextView tvLessonTopics =
                        lessonView.findViewById(R.id.tvRecentLessonTopics);

                TextView tvLessonPercentage =
                        lessonView.findViewById(R.id.tvRecentLessonPercentage);

                ProgressBar pbLessonProgress =
                        lessonView.findViewById(R.id.pbRecentLessonProgress);

                int percentage = Math.max(
                        0,
                        Math.min(100, lesson.getProgressPercentage())
                );

                tvLessonTitle.setText(lesson.getLessonTitle());

                tvLessonDate.setText(
                        formatLessonDate(lesson.getLastAccessedAt())
                );

                tvLessonTopics.setText(
                        getString(
                                R.string.dashboard_topics_completed,
                                lesson.getCompletedTopics(),
                                lesson.getTotalTopics()
                        )
                );

                tvLessonPercentage.setText(
                        getString(R.string.analytics_score_format, percentage)
                );
                pbLessonProgress.setProgress(percentage);

                lessonView.setOnClickListener(v -> {
                    Intent intent = new Intent(
                            AnalyticsActivity.this,
                            LessonDetailsActivity.class
                    );

                    intent.putExtra("containerId", lesson.getContainerId());
                    intent.putExtra("lessonId", lesson.getLessonId());

                    startActivity(intent);
                });

                recentActivityContainer.addView(lessonView);

            }

        }
    }

    private String formatLessonDate(String dateString) {
        if (dateString == null || dateString.trim().isEmpty()) {
            return getString(R.string.analytics_date_unavailable);
        }

        try {
            Instant instant;

            try {
                instant = Instant.parse(dateString);
            } catch (Exception ignored) {
                instant = OffsetDateTime.parse(dateString).toInstant();
            }

            String formattedDate = DateTimeFormatter
                    .ofPattern("MMM dd", Locale.ENGLISH)
                    .withZone(java.time.ZoneId.systemDefault())
                    .format(instant);

            return getString(R.string.analytics_date_format, formattedDate);

        } catch (Exception e) {
            return getString(R.string.analytics_date_unavailable);
        }

    }


}
