package com.example.studyloopandroid.ui.summary;

import com.example.studyloopandroid.R;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.studyloopandroid.data.model.summary.Summary;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import io.noties.markwon.Markwon;
import io.noties.markwon.ext.tables.TablePlugin;
import android.text.method.ScrollingMovementMethod;

public class SummaryDetailsActivity extends AppCompatActivity {

    private SummaryViewModel summaryViewModel;

    private TextView tvSummaryTitle;
    private TextView tvSummaryDepth;
    private TextView tvSummaryDate;
    private TextView tvSummaryContent;

    private MaterialButton btnBackToLesson;
    private MaterialButton btnDeleteSummary;

    private String containerId;
    private String lessonId;
    private String summaryId;

    private Markwon markwon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary_details);


        tvSummaryTitle = findViewById(R.id.tvSummaryTitle);
        tvSummaryDepth = findViewById(R.id.tvSummaryDepth);
        tvSummaryDate = findViewById(R.id.tvSummaryDate);
        tvSummaryContent = findViewById(R.id.tvSummaryContent);

        btnBackToLesson = findViewById(R.id.btnBackToLesson);
        btnDeleteSummary = findViewById(R.id.btnDeleteSummary);

        tvSummaryContent.setMovementMethod(
                ScrollingMovementMethod.getInstance()
        );
        markwon = Markwon.builder(this)
                .usePlugin(TablePlugin.create(this))
                .build();
        containerId = getIntent().getStringExtra("containerId");
        lessonId = getIntent().getStringExtra("lessonId");
        summaryId = getIntent().getStringExtra("summaryId");

        if (containerId == null || lessonId == null || summaryId == null) {
            Toast.makeText(
                    this,
                    getString(R.string.summary_missing_info),
                    Toast.LENGTH_SHORT
            ).show();
            finish();
            return;
        }

        summaryViewModel = new ViewModelProvider(this)
                .get(SummaryViewModel.class);

        observeSummaryViewModel();

        btnBackToLesson.setOnClickListener(v -> finish());

        btnDeleteSummary.setOnClickListener(v -> showDeleteConfirmation());

        summaryViewModel.loadSummary(
                containerId,
                lessonId,
                summaryId
        );
    }

    private void observeSummaryViewModel() {

        summaryViewModel.getSelectedSummary().observe(this, summary -> {
            if (summary == null) {
                return;
            }

            tvSummaryTitle.setText(getString(R.string.summary_title));

            tvSummaryDepth.setText(
                    formatDepthLabel(summary.getDepth())
            );

            tvSummaryDate.setText(
                    getString(
                            R.string.summary_created_format,
                            summary.getCreatedAt()
                    )
            );

            markwon.setMarkdown(
                    tvSummaryContent,
                    summary.getContent()
            );
        });

        summaryViewModel.getError().observe(this, error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(
                        this,
                        error,
                        Toast.LENGTH_LONG
                ).show();
            }
        });

        summaryViewModel.getOperationLoading().observe(this, isLoading -> {
            boolean loading = Boolean.TRUE.equals(isLoading);

            btnDeleteSummary.setEnabled(!loading);
            btnDeleteSummary.setText(
                    loading ? getString(R.string.summary_deleting) : getString(R.string.summary_delete)
            );
        });

        summaryViewModel.getOperationSuccess().observe(this, message -> {
            if (message != null && message.contains("deleted")) {
                Toast.makeText(
                        this,
                        message,
                        Toast.LENGTH_SHORT
                ).show();

                finish();
            }
        });

        summaryViewModel.getOperationError().observe(this, error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(
                        this,
                        error,
                        Toast.LENGTH_LONG
                ).show();

                summaryViewModel.clearOperationMessages();
            }
        });
    }

    private String formatDepthLabel(String depth) {

        if (depth == null || depth.trim().isEmpty()) {
            return getString(R.string.summary_title_fallback);
        }

        String value = depth.trim();

        return getString(
                R.string.summary_depth_format,
                Character.toUpperCase(value.charAt(0)) + value.substring(1)
        );
    }

    private void showDeleteConfirmation() {

        new MaterialAlertDialogBuilder(this)
                .setTitle(getString(R.string.summary_delete))
                .setMessage(
                        getString(R.string.summary_delete_message)
                )
                .setNegativeButton(getString(R.string.action_cancel), null)
                .setPositiveButton(getString(R.string.action_delete), (dialog, which) -> {

                    summaryViewModel.deleteSummary(
                            containerId,
                            lessonId,
                            summaryId
                    );
                })
                .show();
    }
}
