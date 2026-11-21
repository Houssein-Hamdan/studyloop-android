package com.example.studyloopandroid.ui.learning;

import com.example.studyloopandroid.R;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.studyloopandroid.data.model.lesson.CreateLessonRequest;
import com.example.studyloopandroid.data.model.lesson.CreateTopicRequest;

import java.util.ArrayList;
import java.util.List;

public class CreateLessonActivity extends AppCompatActivity {

    private EditText etLessonTitle;
    private EditText etRawContent;

    private RadioButton rbStructured;
    private RadioButton rbUnstructured;

    private LinearLayout layoutStructured;
    private LinearLayout layoutUnstructured;
    private LinearLayout layoutTopics;

    private Button btnAddTopic;
    private Button btnSaveLesson;

    private ProgressBar progressBar;
    private TextView tvError;

    private LessonsViewModel lessonsViewModel;

    private String containerId;

    private final List<TopicForm> topicForms = new ArrayList<>();

    private int selectedContentType = View.NO_ID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_lesson);

        containerId = getIntent().getStringExtra("containerId");

        bindViews();
        setupViewModel();
        setupContentTypeSelector();
        setupListeners();

        if (containerId == null || containerId.trim().isEmpty()) {
            showError(getString(R.string.lesson_space_identify_error));
            btnSaveLesson.setEnabled(false);
        }
    }

    private void bindViews() {

        etLessonTitle = findViewById(R.id.etLessonTitle);
        etRawContent = findViewById(R.id.etRawContent);

        rbStructured = findViewById(R.id.rbStructured);
        rbUnstructured = findViewById(R.id.rbUnstructured);

        layoutStructured = findViewById(R.id.layoutStructured);
        layoutUnstructured = findViewById(R.id.layoutUnstructured);
        layoutTopics = findViewById(R.id.layoutTopics);

        btnAddTopic = findViewById(R.id.btnAddTopic);
        btnSaveLesson = findViewById(R.id.btnSaveLesson);

        progressBar = findViewById(R.id.createLessonProgress);
        tvError = findViewById(R.id.tvCreateLessonError);

        findViewById(R.id.tvCreateLessonBack)
                .setOnClickListener(v -> finish());
    }

    private void setupViewModel() {

        lessonsViewModel = new ViewModelProvider(this)
                .get(LessonsViewModel.class);

        lessonsViewModel.getOperationLoading().observe(this, isLoading -> {

            boolean loading = Boolean.TRUE.equals(isLoading);

            progressBar.setVisibility(
                    loading ? View.VISIBLE : View.GONE
            );

            btnSaveLesson.setEnabled(!loading);
            btnAddTopic.setEnabled(!loading);

            if (loading) {
                tvError.setVisibility(View.GONE);
            }
        });

        lessonsViewModel.getOperationError().observe(this, message -> {

            if (message != null && !message.isEmpty()) {
                showError(message);
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

                setResult(RESULT_OK);
                lessonsViewModel.clearOperationMessages();
                finish();
            }
        });
    }

    private void setupContentTypeSelector() {


        rbStructured.setOnClickListener(v -> selectContentType(R.id.rbStructured));
        rbUnstructured.setOnClickListener(v -> selectContentType(R.id.rbUnstructured));
    }

    private void selectContentType(int selectedId) {

        selectedContentType = selectedId;

        rbStructured.setChecked(selectedId == R.id.rbStructured);
        rbUnstructured.setChecked(selectedId == R.id.rbUnstructured);

        layoutStructured.setVisibility(
                selectedId == R.id.rbStructured
                        ? View.VISIBLE
                        : View.GONE
        );

        layoutUnstructured.setVisibility(
                selectedId == R.id.rbUnstructured
                        ? View.VISIBLE
                        : View.GONE
        );

        if (selectedId == R.id.rbStructured && topicForms.isEmpty()) {
            addTopicForm();
        }
    }

    private void setupListeners() {

        btnAddTopic.setOnClickListener(v -> addTopicForm());

        btnSaveLesson.setOnClickListener(v -> createLesson());
    }

    private void addTopicForm() {

        TopicForm form = new TopicForm();

        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);

        int padding = dp(16);

        card.setPadding(padding, padding, padding, padding);
        card.setBackgroundColor(ContextCompat.getColor(this, R.color.white));

        LinearLayout.LayoutParams cardParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        cardParams.topMargin = dp(12);

        TextView tvTopicNumber = new TextView(this);
        tvTopicNumber.setText(getString(R.string.topic_number_format, topicForms.size() + 1));
        tvTopicNumber.setTextSize(16);
        tvTopicNumber.setTextColor(ContextCompat.getColor(this, R.color.text_topic_number));

        tvTopicNumber.setTypeface(null, android.graphics.Typeface.BOLD);

        form.etTitle = new EditText(this);
        form.etTitle.setSingleLine(true);
        form.etTitle.setHint(getString(R.string.topic_title_required_hint));
        form.etTitle.setInputType(
                android.text.InputType.TYPE_CLASS_TEXT
                        | android.text.InputType.TYPE_TEXT_FLAG_CAP_SENTENCES
        );

        form.etDescription = new EditText(this);
        form.etDescription.setHint(getString(R.string.topic_description_optional_hint));
        form.etDescription.setMinLines(2);
        form.etDescription.setGravity(
                Gravity.TOP | Gravity.START
        );
        form.etDescription.setInputType(
                android.text.InputType.TYPE_CLASS_TEXT
                        | android.text.InputType.TYPE_TEXT_FLAG_CAP_SENTENCES
                        | android.text.InputType.TYPE_TEXT_FLAG_MULTI_LINE
        );

        Button btnRemove = new Button(this);
        btnRemove.setText(getString(R.string.remove_topic));

        card.addView(tvTopicNumber);
        card.addView(form.etTitle);
        card.addView(form.etDescription);
        card.addView(btnRemove);

        layoutTopics.addView(card, cardParams);

        form.card = card;
        form.tvNumber = tvTopicNumber;

        topicForms.add(form);

        btnRemove.setOnClickListener(v -> {

            if (topicForms.size() == 1) {
                Toast.makeText(
                        this,
                        getString(R.string.topic_form_required),
                        Toast.LENGTH_SHORT
                ).show();
                return;
            }

            topicForms.remove(form);
            layoutTopics.removeView(card);

            updateTopicNumbers();
        });
    }

    private void updateTopicNumbers() {

        for (int i = 0; i < topicForms.size(); i++) {
            topicForms.get(i).tvNumber.setText(getString(R.string.topic_number_format, i + 1));
        }
    }

    private void createLesson() {

        clearError();

        String title = etLessonTitle.getText()
                .toString()
                .trim();

        if (title.isEmpty()) {
            etLessonTitle.setError(getString(R.string.lesson_title_required));
            etLessonTitle.requestFocus();
            return;
        }

        if (containerId == null || containerId.trim().isEmpty()) {
            showError(getString(R.string.lesson_space_identify_error));
            return;
        }

        CreateLessonRequest request = null;

        int selectedType = selectedContentType;

        if (selectedType != R.id.rbStructured
                && selectedType != R.id.rbUnstructured) {

            showError(
                    getString(R.string.lesson_type_required)
            );
            return;
        }

        if (selectedType == R.id.rbStructured) {

            if (topicForms.isEmpty()) {
                showError(getString(R.string.lesson_topics_required));
                return;
            }

            List<CreateTopicRequest> topics = new ArrayList<>();

            for (int i = 0; i < topicForms.size(); i++) {

                TopicForm form = topicForms.get(i);

                String topicTitle = form.etTitle.getText()
                        .toString()
                        .trim();

                String description = form.etDescription.getText()
                        .toString()
                        .trim();

                if (topicTitle.isEmpty()) {
                    form.etTitle.setError(getString(R.string.topic_title_required));
                    form.etTitle.requestFocus();
                    return;
                }

                topics.add(new CreateTopicRequest(
                        topicTitle,
                        description.isEmpty() ? null : description
                ));
            }

            request = new CreateLessonRequest(
                    title,
                    null,
                    topics
            );

        } else if (selectedType == R.id.rbUnstructured) {

            String rawContent = etRawContent.getText()
                    .toString()
                    .trim();

            if (rawContent.isEmpty()) {
                etRawContent.setError(getString(R.string.lesson_content_required));
                etRawContent.requestFocus();
                return;
            }

            request = new CreateLessonRequest(
                    title,
                    rawContent,
                    null
            );

        }

        if (request == null) {
            showError(
                    getString(R.string.lesson_type_required)
            );
            return;
        }

        lessonsViewModel.createLesson(containerId, request);
    }

    private void showError(String message) {

        tvError.setText(message);
        tvError.setVisibility(View.VISIBLE);
    }

    private void clearError() {

        tvError.setText("");
        tvError.setVisibility(View.GONE);
    }

    private int dp(int value) {

        return (int) (value * getResources()
                .getDisplayMetrics().density + 0.5f);
    }

    private static class TopicForm {

        EditText etTitle;
        EditText etDescription;

        LinearLayout card;
        TextView tvNumber;
    }

}
