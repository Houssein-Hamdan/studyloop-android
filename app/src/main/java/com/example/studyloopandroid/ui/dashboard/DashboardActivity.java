package com.example.studyloopandroid.ui.dashboard;

import com.example.studyloopandroid.R;
import com.example.studyloopandroid.ui.common.BaseActivity;
import com.example.studyloopandroid.ui.learning.HomeActivity;
import com.example.studyloopandroid.ui.auth.MainActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.content.Intent;
import android.widget.Toast;
import androidx.lifecycle.ViewModelProvider;

import com.example.studyloopandroid.ui.analytics.AnalyticsActivity;
import com.example.studyloopandroid.ui.auth.AuthViewModel;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.studyloopandroid.data.model.lesson.RecentLessonResponse;
import com.example.studyloopandroid.ui.ask.AskAIActivity;


public class DashboardActivity extends BaseActivity  {

    private RecentLessonsViewModel viewModel;

    private LinearLayout recentLessonsContainer;
    private ProgressBar progressBar;
    private TextView tvError;
    private View tvEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dashboard);

        setupBottomNavigation(R.id.nav_home);
        setupDashboardActions();
        setupLogout();

        View root = findViewById(R.id.dashboardRoot);

        if (root != null) {
            ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
                Insets systemBars = insets.getInsets(
                        WindowInsetsCompat.Type.systemBars()
                );

                v.setPadding(
                        systemBars.left,
                        systemBars.top,
                        systemBars.right,
                        systemBars.bottom
                );

                return insets;
            });
        }

        recentLessonsContainer = findViewById(R.id.recentLessonsContainer);
        progressBar = findViewById(R.id.dashboardProgressBar);
        tvError = findViewById(R.id.tvDashboardError);
        tvEmpty = findViewById(R.id.tvNoRecentLessons);

        viewModel = new ViewModelProvider(this)
                .get(RecentLessonsViewModel.class);

        observeViewModel();

        viewModel.loadRecentLessons();
    }

    private void observeViewModel() {
        viewModel.getIsLoading().observe(this, isLoading -> {
            if (isLoading != null) {
                progressBar.setVisibility(
                        isLoading ? View.VISIBLE : View.GONE
                );
            }
        });

        viewModel.getErrorMessage().observe(this, error -> {
            if (error != null) {
                tvError.setText(error);
                tvError.setVisibility(View.VISIBLE);
            } else {
                tvError.setVisibility(View.GONE);
            }
        });

        viewModel.getRecentLessons().observe(this, lessons -> {
            recentLessonsContainer.removeAllViews();

            if (lessons == null || lessons.isEmpty()) {
                tvEmpty.setVisibility(View.VISIBLE);
                return;
            }

            tvEmpty.setVisibility(View.GONE);

            for (RecentLessonResponse lesson : lessons) {
                addRecentLessonCard(lesson);
            }
        });
    }

    private void addRecentLessonCard(RecentLessonResponse lesson) {
        View card = getLayoutInflater().inflate(
                R.layout.item_recent_lesson,
                recentLessonsContainer,
                false
        );

        TextView tvTitle = card.findViewById(R.id.tvRecentLessonTitle);
        TextView tvTopics = card.findViewById(R.id.tvRecentLessonTopics);
        TextView tvProgress = card.findViewById(R.id.tvRecentLessonProgress);
        ProgressBar lessonProgress = card.findViewById(
                R.id.progressRecentLesson
        );

        tvTitle.setText(lesson.getLessonTitle());

        tvTopics.setText(
                getString(
                        R.string.dashboard_topics_completed,
                        lesson.getCompletedTopics(),
                        lesson.getTotalTopics()
                )
        );

        int percentage = lesson.getProgressPercentage();
        lessonProgress.setProgress(percentage);
        tvProgress.setText(
                getString(R.string.dashboard_percent_complete, percentage)
        );

        card.setOnClickListener(v -> {
            android.content.Intent intent = new android.content.Intent(
                    DashboardActivity.this,
                    com.example.studyloopandroid.ui.learning.LessonDetailsActivity.class
            );

            intent.putExtra("containerId", lesson.getContainerId());
            intent.putExtra("lessonId", lesson.getLessonId());

            startActivity(intent);
        });

        recentLessonsContainer.addView(card);
    }

    private void setupDashboardActions() {

        // Start Learning -> Learning Spaces
        findViewById(R.id.btnStartLearning).setOnClickListener(v ->
                openLearningSpaces()
        );

        // Create Learning Space -> Learning Spaces
        findViewById(R.id.actionCreateSpace).setOnClickListener(v ->
                openLearningSpaces()
        );

        // Explore Learning -> Learning Spaces
        findViewById(R.id.actionExploreLearning).setOnClickListener(v -> {
            Intent intent = new Intent(
                    DashboardActivity.this,
                    AnalyticsActivity.class
            );
            startActivity(intent);
        });

        // Review Due -> Review screen
        findViewById(R.id.actionReviewDue).setOnClickListener(v -> {
            Intent intent = new Intent(
                    DashboardActivity.this,
                    com.example.studyloopandroid.ui.review.ReviewActivity.class
            );
            startActivity(intent);
        });

        // Ask AI -> Ask AI chat screen
        findViewById(R.id.actionAskAI).setOnClickListener(v -> {
            Intent intent = new Intent(
                    DashboardActivity.this,
                    AskAIActivity.class
            );

            startActivity(intent);
        });
    }

    private void openLearningSpaces() {
        Intent intent = new Intent(
                DashboardActivity.this,
                HomeActivity.class
        );
        startActivity(intent);
    }

    private void setupLogout() {

        findViewById(R.id.btnLogout).setOnClickListener(v -> {

            AuthViewModel authViewModel =
                    new ViewModelProvider(this).get(AuthViewModel.class);

            authViewModel.clearSession();

            Toast.makeText(
                    this,
                    getString(R.string.dashboard_logged_out),
                    Toast.LENGTH_SHORT
            ).show();

            Intent intent = new Intent(
                    DashboardActivity.this,
                    MainActivity.class
            );

            intent.setFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK |
                            Intent.FLAG_ACTIVITY_CLEAR_TASK
            );

            startActivity(intent);
            finish();
        });
    }

}
