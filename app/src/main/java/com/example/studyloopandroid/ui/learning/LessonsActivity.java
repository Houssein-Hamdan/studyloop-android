package com.example.studyloopandroid.ui.learning;

import com.example.studyloopandroid.R;

import android.content.Intent;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studyloopandroid.data.model.lesson.LessonResponse;

import java.util.List;

public class LessonsActivity extends AppCompatActivity {


    private LessonsViewModel lessonsViewModel;
    private LessonAdapter lessonAdapter;

    private ProgressBar progressBar;
    private TextView tvLessonsError;
    private TextView tvEmptyLessons;
    private TextView tvLessonsCount;
    private Button btnCreateLesson;

    private String containerId;

    private ActivityResultLauncher<Intent> createLessonLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lessons);

        createLessonLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        lessonsViewModel.loadLessons(containerId);
                    }
                }
        );

        containerId = getIntent().getStringExtra("containerId");
        String containerName = getIntent().getStringExtra("containerName");

        TextView tvLessonsHeader = findViewById(R.id.tvLessonsHeader);
        TextView tvLessonsSubtitle = findViewById(R.id.tvLessonsSubtitle);

        tvLessonsCount = findViewById(R.id.tvLessonsCount);
        progressBar = findViewById(R.id.lessonsProgressBar);
        tvLessonsError = findViewById(R.id.tvLessonsError);
        tvEmptyLessons = findViewById(R.id.tvEmptyLessons);
        btnCreateLesson = findViewById(R.id.btnCreateLesson);

        TextView tvBackToHome = findViewById(R.id.tvBackToHome);
        RecyclerView rvLessons = findViewById(R.id.rvLessons);

        tvBackToHome.setOnClickListener(v -> finish());

        if (containerName != null && !containerName.isEmpty()) {
            tvLessonsHeader.setText(containerName);
        }

        tvLessonsSubtitle.setText(
                getString(R.string.lessons_subtitle_active)
        );

        lessonsViewModel = new ViewModelProvider(this)
                .get(LessonsViewModel.class);

        lessonAdapter = new LessonAdapter(
                new LessonAdapter.OnLessonClickListener() {

                    @Override
                    public void onLessonClick(LessonResponse lesson) {
                        openLessonDetails(lesson);
                    }

                    @Override
                    public void onEditLesson(LessonResponse lesson) {
                        showEditLessonDialog(lesson);
                    }

                    @Override
                    public void onDeleteLesson(LessonResponse lesson) {
                        showDeleteLessonDialog(lesson);
                    }
                }
        );

        rvLessons.setLayoutManager(new LinearLayoutManager(this));
        rvLessons.setAdapter(lessonAdapter);

        btnCreateLesson.setOnClickListener(v -> {
            Intent intent = new Intent(
                    LessonsActivity.this,
                    CreateLessonActivity.class
            );

            intent.putExtra("containerId", containerId);

            createLessonLauncher.launch(intent);
        });

        observeViewModel();

        if (containerId == null || containerId.isEmpty()) {
            tvLessonsError.setText(
                    getString(R.string.lessons_open_error)
            );
            tvLessonsError.setVisibility(View.VISIBLE);
            btnCreateLesson.setEnabled(false);
        } else {
            lessonsViewModel.loadLessons(containerId);
        }
    }

    private void observeViewModel() {

        lessonsViewModel.getLessons().observe(this, lessons -> {
            lessonAdapter.updateLessons(lessons);

            int count = lessons == null ? 0 : lessons.size();
            tvLessonsCount.setText(
                    getString(R.string.lessons_count_format, count)
            );

            updateEmptyState(lessons);
        });

        lessonsViewModel.getLoading().observe(this, isLoading -> {
            boolean loading = Boolean.TRUE.equals(isLoading);

            progressBar.setVisibility(
                    loading ? View.VISIBLE : View.GONE
            );

            if (loading) {
                tvEmptyLessons.setVisibility(View.GONE);
            } else {
                updateEmptyState(
                        lessonsViewModel.getLessons().getValue()
                );
            }
        });

        lessonsViewModel.getError().observe(this, error -> {
            if (error == null || error.isEmpty()) {
                tvLessonsError.setVisibility(View.GONE);
                tvLessonsError.setText("");
            } else {
                tvLessonsError.setVisibility(View.VISIBLE);
                tvLessonsError.setText(error);
                tvEmptyLessons.setVisibility(View.GONE);
            }
        });

        lessonsViewModel.getOperationLoading().observe(this, isLoading -> {
            boolean loading = Boolean.TRUE.equals(isLoading);
            btnCreateLesson.setEnabled(!loading);
        });

        lessonsViewModel.getOperationError().observe(this, message -> {
            if (message != null && !message.isEmpty()) {
                Toast.makeText(
                        this,
                        message,
                        Toast.LENGTH_LONG
                ).show();

                lessonsViewModel.clearOperationMessages();
            }
        });

        lessonsViewModel.getOperationSuccess().observe(this, message -> {
            if (message != null && !message.isEmpty()) {
                Toast.makeText(
                        this,
                        message,
                        Toast.LENGTH_SHORT
                ).show();

                lessonsViewModel.clearOperationMessages();
            }
        });
    }

    private void openLessonDetails(LessonResponse lesson) {
        android.content.Intent intent = new android.content.Intent(
                LessonsActivity.this,
                LessonDetailsActivity.class
        );

        intent.putExtra("containerId", containerId);
        intent.putExtra("lessonId", lesson.getId());
        intent.putExtra("lessonTitle", lesson.getTitle());

        startActivity(intent);
    }

    private void showEditLessonDialog(LessonResponse lesson) {

        EditText etTitle = new EditText(this);
        etTitle.setSingleLine(true);
        etTitle.setHint(getString(R.string.lesson_title_field_hint));
        etTitle.setText(lesson.getTitle());

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(getString(R.string.edit_lesson_title))
                .setView(etTitle)
                .setNegativeButton(getString(R.string.action_cancel), null)
                .setPositiveButton(getString(R.string.action_save), null)
                .create();

        dialog.setOnShowListener(ignored ->
                dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                        .setOnClickListener(v -> {

                            String title = etTitle.getText()
                                    .toString()
                                    .trim();

                            if (title.isEmpty()) {
                                etTitle.setError(
                                        getString(R.string.lesson_title_empty)
                                );
                                etTitle.requestFocus();
                                return;
                            }

                            lessonsViewModel.updateLesson(
                                    containerId,
                                    lesson.getId(),
                                    title
                            );

                            dialog.dismiss();
                        })
        );

        dialog.show();
    }

    private void showDeleteLessonDialog(LessonResponse lesson) {

        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.delete_lesson_title))
                .setMessage(
                        getString(
                                R.string.delete_lesson_message,
                                lesson.getTitle()
                        )
                )
                .setNegativeButton(getString(R.string.action_cancel), null)
                .setPositiveButton(getString(R.string.action_delete), (dialog, which) ->
                        lessonsViewModel.deleteLesson(
                                containerId,
                                lesson.getId()
                        )
                )
                .show();
    }

    private void updateEmptyState(List<LessonResponse> lessons) {

        boolean isEmpty = lessons == null || lessons.isEmpty();

        Boolean loading = lessonsViewModel.getLoading().getValue();
        String error = lessonsViewModel.getError().getValue();

        boolean hasError = error != null && !error.isEmpty();

        if (isEmpty
                && !Boolean.TRUE.equals(loading)
                && !hasError) {
            tvEmptyLessons.setVisibility(View.VISIBLE);
        } else {
            tvEmptyLessons.setVisibility(View.GONE);
        }
    }

}
