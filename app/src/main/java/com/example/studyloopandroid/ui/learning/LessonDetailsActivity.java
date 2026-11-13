package com.example.studyloopandroid.ui.learning;

import com.example.studyloopandroid.ui.summary.SummaryDetailsActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.lifecycle.ViewModelProvider;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ScrollView;
import android.graphics.Typeface;
import android.text.TextUtils;
import com.google.android.material.card.MaterialCardView;

import com.example.studyloopandroid.ui.quiz.QuizViewModel;
import com.example.studyloopandroid.ui.learning.details.TopicSectionController;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.studyloopandroid.R;
import com.example.studyloopandroid.data.model.lesson.LessonResponse;
import com.example.studyloopandroid.data.model.lesson.TopicResponse;
import com.example.studyloopandroid.data.model.summary.Summary;
import com.example.studyloopandroid.data.model.summary.CreateSummaryRequest;
import com.example.studyloopandroid.ui.summary.SummaryViewModel;
import android.widget.EditText;
import com.example.studyloopandroid.data.model.quiz.CreateQuizRequest;
import android.content.Intent;

import com.example.studyloopandroid.ui.quiz.TakeQuizActivity;



import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import androidx.appcompat.app.AlertDialog;
import com.google.android.material.button.MaterialButton;

import java.util.List;
import android.widget.Toast;
import java.util.ArrayList;

public class LessonDetailsActivity extends AppCompatActivity {

    private TextView tvLessonTitle;
    private TextView tvError;
    private ProgressBar progressBar;

    private LessonDetailsViewModel viewModel;

    private SummaryViewModel summaryViewModel;

    private MaterialButton btnViewSummaries;
    private MaterialButton btnCreateSummary;
    private LinearLayout summariesContainer;
    private ProgressBar summaryProgressBar;
    private TextView tvSummaryError;

    private boolean summariesVisible = false;

    private MaterialButton btnAddTopic;

    private String containerId;
    private String lessonId;
    private String lessonTitle;
    private MaterialButton btnViewQuizzes;
    private MaterialButton btnCreateQuiz;
    private LinearLayout quizzesContainer;
    private ProgressBar quizProgressBar;
    private TextView tvQuizError;

    private QuizViewModel quizViewModel;
    private boolean quizzesVisible = false;
    private String selectedSummaryDepth = "medium";
    private boolean summarizeSelectedTopics = false;

    private TopicSectionController topicSectionController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson_details);

        TextView tvBackToLessons = findViewById(R.id.tvBackToLessons);
        tvLessonTitle = findViewById(R.id.tvDetailLessonTitle);
        tvError = findViewById(R.id.tvLessonDetailsError);
        progressBar = findViewById(R.id.lessonDetailsProgressBar);
        btnAddTopic = findViewById(R.id.btnAddTopic);
        TextView tvTopicsCount = findViewById(R.id.tvDetailTopicsCount);
        View cardEmptyTopics = findViewById(R.id.cardEmptyTopics);
        LinearLayout topicsContainer = findViewById(R.id.topicsContainer);
        btnViewQuizzes = findViewById(R.id.btnViewQuizzes);
        btnCreateQuiz = findViewById(R.id.btnCreateQuiz);
        quizzesContainer = findViewById(R.id.quizzesContainer);
        quizProgressBar = findViewById(R.id.quizProgressBar);
        tvQuizError = findViewById(R.id.tvQuizError);
        btnViewSummaries = findViewById(R.id.btnViewSummaries);
        btnCreateSummary = findViewById(R.id.btnCreateSummary);
        summariesContainer = findViewById(R.id.summariesContainer);
        summaryProgressBar = findViewById(R.id.summaryProgressBar);
        tvSummaryError = findViewById(R.id.tvSummaryError);

        // Create the ViewModels before wiring any click listener so they are
        // always available, even when the required Intent extras are missing.
        viewModel = new ViewModelProvider(this)
                .get(LessonDetailsViewModel.class);

        quizViewModel = new ViewModelProvider(this)
                .get(QuizViewModel.class);

        summaryViewModel = new ViewModelProvider(this)
                .get(SummaryViewModel.class);

        btnCreateQuiz.setOnClickListener(v -> showCreateQuizDialog());

        btnViewQuizzes.setOnClickListener(v -> {
            quizzesVisible = !quizzesVisible;

            if (quizzesVisible) {
                quizzesContainer.setVisibility(View.VISIBLE);
                btnViewQuizzes.setText(getString(R.string.lesson_hide_quizzes));

                quizViewModel.loadLessonQuizzes(containerId, lessonId);
            } else {
                quizzesContainer.setVisibility(View.GONE);
                btnViewQuizzes.setText(getString(R.string.lesson_view_quizzes));
            }
        });

        tvBackToLessons.setOnClickListener(v -> finish());

        containerId = getIntent().getStringExtra("containerId");
        lessonId = getIntent().getStringExtra("lessonId");
        lessonTitle = getIntent().getStringExtra("lessonTitle");

        if (lessonTitle != null && !lessonTitle.isEmpty()) {
            tvLessonTitle.setText(lessonTitle);
        }

        if (containerId == null || lessonId == null) {
            showError(getString(R.string.lesson_missing_info));

            // Keep the user on this screen but disable actions that would
            // otherwise call the API with incomplete identifiers.
            btnAddTopic.setEnabled(false);
            btnViewQuizzes.setEnabled(false);
            btnCreateQuiz.setEnabled(false);
            btnCreateSummary.setEnabled(false);
            return;
        }

        observeSummaryViewModel();

        observeQuizViewModel();

        topicSectionController = new TopicSectionController(
                this,
                this,
                viewModel,
                topicsContainer,
                tvTopicsCount,
                cardEmptyTopics,
                containerId,
                lessonId
        );

        btnAddTopic.setOnClickListener(
                v -> topicSectionController.showCreateTopicDialog()
        );

        btnCreateSummary.setOnClickListener(v -> showCreateSummaryDialog());

        btnViewSummaries.setOnClickListener(v -> {

            if (summariesVisible) {
                summariesContainer.setVisibility(View.GONE);
                summariesVisible = false;
                btnViewSummaries.setText(getString(R.string.lesson_view_summaries));
            } else {
                summariesVisible = true;
                summariesContainer.setVisibility(View.VISIBLE);
                btnViewSummaries.setText(getString(R.string.lesson_hide_summaries));

                summaryViewModel.loadSummaries(containerId, lessonId);
            }
        });


        quizzesContainer.setVisibility(View.GONE);
        quizProgressBar.setVisibility(View.GONE);
        tvQuizError.setVisibility(View.GONE);

        observeViewModel();

        viewModel.loadLessonDetails(containerId, lessonId);
    }

    private void observeViewModel() {

        viewModel.getLesson().observe(this, lesson -> {
            if (lesson == null) {
                return;
            }
            viewModel.loadLessonProgress(containerId, lessonId);

            tvLessonTitle.setText(lesson.getTitle());

            topicSectionController.renderTopics(lesson.getTopics());
        });

        viewModel.getLoading().observe(this, isLoading -> {
            progressBar.setVisibility(
                    Boolean.TRUE.equals(isLoading)
                            ? View.VISIBLE
                            : View.GONE
            );
        });

        viewModel.getError().observe(this, error -> {
            if (error != null && !error.isEmpty()) {
                showError(error);
            } else {
                tvError.setVisibility(View.GONE);
            }
        });
    }

    private void showError(String message) {
        tvError.setText(message);
        tvError.setVisibility(View.VISIBLE);
    }

    private void observeQuizViewModel() {

        quizViewModel.getQuizzes().observe(this, quizzes -> {
            if (quizzes == null) {
                return;
            }

            quizzesContainer.removeAllViews();

            if (quizzes.isEmpty()) {
                TextView emptyMessage = new TextView(this);
                emptyMessage.setTextSize(14);
                emptyMessage.setPadding(8, 16, 8, 16);

                quizzesContainer.addView(emptyMessage);
                return;
            }

            int quizNumber = 1;

            for (com.example.studyloopandroid.data.model.quiz.Quiz quiz : quizzes) {

                MaterialCardView quizCard = new MaterialCardView(this);

                LinearLayout.LayoutParams cardParams =
                        new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                        );

                cardParams.setMargins(0, 0, 0, dpToPx(12));

                quizCard.setLayoutParams(cardParams);
                quizCard.setRadius(dpToPx(12));
                quizCard.setCardElevation(dpToPx(2));
                quizCard.setClickable(true);
                quizCard.setFocusable(true);

                LinearLayout cardContent = new LinearLayout(this);
                cardContent.setOrientation(LinearLayout.VERTICAL);
                cardContent.setPadding(
                        dpToPx(16),
                        dpToPx(16),
                        dpToPx(16),
                        dpToPx(16)
                );

                TextView quizTitle = new TextView(this);
                quizTitle.setText(getString(R.string.quiz_number_format, quizNumber++));
                quizTitle.setTextSize(17);
                quizTitle.setTypeface(null, android.graphics.Typeface.BOLD);

                TextView quizInfo = new TextView(this);
                quizInfo.setText(
                        getString(
                                R.string.quiz_info_format,
                                quiz.getDifficulty(),
                                quiz.getFormat(),
                                quiz.getQuestionCount()
                        )
                );
                quizInfo.setTextSize(14);

                cardContent.addView(quizTitle);
                cardContent.addView(quizInfo);

                quizCard.addView(cardContent);

                quizCard.setOnClickListener(v -> {
                    Intent intent = new Intent(this, TakeQuizActivity.class);

                    intent.putExtra("containerId", containerId);
                    intent.putExtra("lessonId", lessonId);
                    intent.putExtra("quizId", quiz.getId());

                    startActivity(intent);
                });

                quizzesContainer.addView(quizCard);
            }
        });

        quizViewModel.getLoading().observe(this, isLoading -> {
            if (isLoading == null) {
                return;
            }

            quizProgressBar.setVisibility(
                    isLoading ? View.VISIBLE : View.GONE
            );
        });

        quizViewModel.getError().observe(this, error -> {
            if (error == null || error.isEmpty()) {
                tvQuizError.setVisibility(View.GONE);
                return;
            }

            tvQuizError.setText(error);
            tvQuizError.setVisibility(View.VISIBLE);
        });

        quizViewModel.getOperationSuccess().observe(this, success -> {

            if (Boolean.TRUE.equals(success)) {

                Toast.makeText(
                        this,
                        getString(R.string.quiz_created_success),
                        Toast.LENGTH_SHORT
                ).show();

                if (quizzesVisible) {
                    quizViewModel.loadLessonQuizzes(containerId, lessonId);
                }

                quizViewModel.clearOperationMessages();
            }
        });

        quizViewModel.getOperationLoading().observe(this, isLoading -> {

            if (Boolean.TRUE.equals(isLoading)) {
                btnCreateQuiz.setEnabled(false);
                btnCreateQuiz.setText(getString(R.string.lesson_creating_quiz));
            } else {
                btnCreateQuiz.setEnabled(true);
                btnCreateQuiz.setText(getString(R.string.lesson_create_quiz));
            }
        });

        quizViewModel.getOperationError().observe(this, error -> {

            if (error != null && !error.isEmpty()) {
                new MaterialAlertDialogBuilder(this)
                        .setTitle(getString(R.string.quiz_creation_failed))
                        .setMessage(error)
                        .setPositiveButton(getString(R.string.action_ok), null)
                        .show();

                quizViewModel.clearOperationMessages();
            }
        });

    }

    private void showCreateQuizDialog() {
        String[] scopes = {
                getString(R.string.quiz_scope_full_lesson),
                getString(R.string.quiz_scope_selected_topics),
                getString(R.string.quiz_scope_random)
        };

        String[] difficulties = {
                getString(R.string.quiz_difficulty_easy),
                getString(R.string.quiz_difficulty_medium),
                getString(R.string.quiz_difficulty_hard)
        };

        String[] formats = {
                getString(R.string.quiz_format_multiple_choice),
                getString(R.string.quiz_format_open_text),
                getString(R.string.quiz_format_fill_blanks),
                getString(R.string.quiz_format_true_false)
        };

        Spinner scopeSpinner = new Spinner(this);
        Spinner difficultySpinner = new Spinner(this);
        Spinner formatSpinner = new Spinner(this);
        EditText questionCountInput = new EditText(this);

        LinearLayout topicsSelectionContainer = new LinearLayout(this);

        ScrollView topicsScrollView = new ScrollView(this);

        LinearLayout.LayoutParams scrollParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        dpToPx(180)
                );

        topicsScrollView.setLayoutParams(scrollParams);
        topicsScrollView.setFillViewport(true);


        topicsSelectionContainer.setOrientation(LinearLayout.VERTICAL);

        TextView topicsSelectionTitle = new TextView(this);
        topicsSelectionTitle.setText(getString(R.string.quiz_select_topics_label));
        topicsSelectionTitle.setTextSize(16);
        topicsSelectionTitle.setVisibility(View.GONE);

        topicsSelectionContainer.addView(topicsSelectionTitle);
        topicsScrollView.addView(topicsSelectionContainer);
        topicsSelectionContainer.setVisibility(View.GONE);

        scopeSpinner.setAdapter(new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                scopes
        ));

        difficultySpinner.setAdapter(new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                difficulties
        ));

        formatSpinner.setAdapter(new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                formats
        ));

        TextView questionCountLabel = new TextView(this);
        questionCountLabel.setText(getString(R.string.quiz_number_of_questions_label));
        questionCountLabel.setTextSize(14);
        questionCountLabel.setPadding(0, dpToPx(12), 0, dpToPx(4));

        questionCountInput.setHint(getString(R.string.quiz_number_of_questions_hint));
        questionCountInput.setInputType(
                android.text.InputType.TYPE_CLASS_NUMBER
        );
        questionCountInput.setText("5");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        int padding = (int) (20 * getResources()
                .getDisplayMetrics().density);

        layout.setPadding(padding, padding, padding, padding);

        layout.addView(scopeSpinner);
        layout.addView(difficultySpinner);
        layout.addView(formatSpinner);
        layout.addView(questionCountLabel);
        layout.addView(questionCountInput);
        layout.addView(topicsScrollView);

        scopeSpinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(
                            AdapterView<?> parent,
                            View view,
                            int position,
                            long id
                    ) {
                        boolean selectedTopics = position == 1;

                        topicsSelectionContainer.setVisibility(
                                selectedTopics ? View.VISIBLE : View.GONE
                        );

                        topicsSelectionTitle.setVisibility(
                                selectedTopics ? View.VISIBLE : View.GONE
                        );

                        if (selectedTopics) {
                            loadTopicsIntoQuizDialog(
                                    topicsSelectionContainer,
                                    topicsSelectionTitle
                            );
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                }
        );

        AlertDialog dialog = new MaterialAlertDialogBuilder(this)
                .setTitle(getString(R.string.create_quiz_title))
                .setView(layout)
                .setNegativeButton(getString(R.string.action_cancel), null)
                .setPositiveButton(getString(R.string.action_create), null)
                .create();

        dialog.setOnShowListener(ignored ->
                dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                        .setOnClickListener(v -> {

                    int questionCount;

                    try {
                        questionCount = Integer.parseInt(
                                questionCountInput.getText().toString().trim()
                        );
                    } catch (NumberFormatException e) {
                        questionCountInput.setError(getString(R.string.quiz_question_count_invalid));
                        return;
                    }

                    if (questionCount < 1) {
                        questionCountInput.setError(
                                getString(R.string.quiz_question_count_min)
                        );
                        return;
                    }

                    String scope;
                    switch (scopeSpinner.getSelectedItemPosition()) {
                        case 1:
                            scope = "selected_topics";
                            break;
                        case 2:
                            scope = "random";
                            break;
                        default:
                            scope = "full_lesson";
                    }

                    String difficulty;
                    switch (difficultySpinner.getSelectedItemPosition()) {
                        case 1:
                            difficulty = "medium";
                            break;
                        case 2:
                            difficulty = "hard";
                            break;
                        default:
                            difficulty = "easy";
                    }

                    String format;
                    switch (formatSpinner.getSelectedItemPosition()) {
                        case 1:
                            format = "open_text";
                            break;
                        case 2:
                            format = "fill_blanks";
                            break;
                        case 3:
                            format = "true_false";
                            break;
                        default:
                            format = "multiple_choice";
                    }

                    List<String> selectedTopicIds = new ArrayList<>();

                    if ("selected_topics".equals(scope)) {

                        for (int i = 1; i < topicsSelectionContainer.getChildCount(); i++) {

                            View child = topicsSelectionContainer.getChildAt(i);

                            if (child instanceof CheckBox) {
                                CheckBox checkBox = (CheckBox) child;

                                if (checkBox.isChecked()) {
                                    selectedTopicIds.add(
                                            String.valueOf(checkBox.getTag())
                                    );
                                }
                            }
                        }

                        if (selectedTopicIds.isEmpty()) {
                            Toast.makeText(
                                    this,
                                    getString(R.string.select_at_least_one_topic),
                                    Toast.LENGTH_SHORT
                            ).show();
                            return;
                        }
                    }

                    CreateQuizRequest request = new CreateQuizRequest(
                            scope,
                            selectedTopicIds,
                            difficulty,
                            format,
                            questionCount
                    );

                    quizViewModel.createQuiz(
                            containerId,
                            lessonId,
                            request
                    );

                    dialog.dismiss();
                })
        );

        dialog.show();
    }

    private void loadTopicsIntoQuizDialog(
            LinearLayout topicsContainer,
            TextView topicsTitle
    ) {
        LessonResponse lesson = viewModel.getLesson().getValue();

        topicsContainer.removeViews(
                1,
                Math.max(0, topicsContainer.getChildCount() - 1)
        );

        if (lesson == null || lesson.getTopics() == null
                || lesson.getTopics().isEmpty()) {

            TextView emptyMessage = new TextView(this);
            emptyMessage.setText(getString(R.string.no_topics_available));
            topicsContainer.addView(emptyMessage);
            return;
        }

        for (TopicResponse topic : lesson.getTopics()) {
            CheckBox checkBox = new CheckBox(this);
            checkBox.setText(topic.getTitle());
            checkBox.setTag(topic.getId());

            topicsContainer.addView(checkBox);
        }
    }

    private int dpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }

    private void observeSummaryViewModel() {

        summaryViewModel.getSummaries().observe(this, summaries -> {
            summariesContainer.removeAllViews();

            if (summaries != null && !summaries.isEmpty()) {
                summariesContainer.setVisibility(View.VISIBLE);

                for (Summary summary : summaries) {
                    addSummaryCard(summary);
                }
            } else {
                summariesContainer.setVisibility(View.GONE);
            }
        });

        summaryViewModel.getLoading().observe(this, isLoading -> {
            summaryProgressBar.setVisibility(
                    Boolean.TRUE.equals(isLoading) ? View.VISIBLE : View.GONE
            );
        });

        summaryViewModel.getError().observe(this, error -> {
            if (error != null && !error.isEmpty()) {
                tvSummaryError.setText(error);
                tvSummaryError.setVisibility(View.VISIBLE);
            } else {
                tvSummaryError.setVisibility(View.GONE);
            }
        });

        summaryViewModel.getOperationLoading().observe(this, isLoading -> {
            btnCreateSummary.setEnabled(!Boolean.TRUE.equals(isLoading));

            if (Boolean.TRUE.equals(isLoading)) {
                tvSummaryError.setVisibility(View.GONE);
            }
        });

        summaryViewModel.getOperationSuccess().observe(this, message -> {
            if (message != null && !message.isEmpty()) {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                summaryViewModel.clearOperationMessages();
            }
        });

        summaryViewModel.getOperationError().observe(this, error -> {
            if (error != null && !error.isEmpty()) {
                tvSummaryError.setText(error);
                tvSummaryError.setVisibility(View.VISIBLE);
                summaryViewModel.clearOperationMessages();
            }
        });
    }


    private void addSummaryCard(Summary summary) {

        MaterialCardView card = new MaterialCardView(this);

        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );

        cardParams.setMargins(0, dpToPx(8), 0, dpToPx(8));

        card.setLayoutParams(cardParams);
        card.setCardBackgroundColor(ContextCompat.getColor(this, R.color.white));
        card.setRadius(dpToPx(16));
        card.setCardElevation(0);
        card.setStrokeWidth(dpToPx(1));
        card.setStrokeColor(ContextCompat.getColor(this, R.color.lavender_300));

        LinearLayout content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setPadding(
                dpToPx(16),
                dpToPx(16),
                dpToPx(16),
                dpToPx(16)
        );

        TextView title = new TextView(this);
        title.setText(formatSummaryTitle(summary.getDepth()));
        title.setTextColor(ContextCompat.getColor(this, R.color.text_primary));
        title.setTextSize(16);
        title.setTypeface(null, Typeface.BOLD);

        TextView preview = new TextView(this);
        preview.setText(summary.getContent());
        preview.setTextColor(ContextCompat.getColor(this, R.color.text_secondary_dark));
        preview.setTextSize(14);
        preview.setMaxLines(4);
        preview.setEllipsize(TextUtils.TruncateAt.END);

        TextView date = new TextView(this);
        date.setText(getString(R.string.summary_created_format, summary.getCreatedAt()));
        date.setTextColor(ContextCompat.getColor(this, R.color.text_tertiary_soft));
        date.setTextSize(12);

        content.addView(title);

        LinearLayout.LayoutParams previewParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
        previewParams.topMargin = dpToPx(8);
        content.addView(preview, previewParams);

        LinearLayout.LayoutParams dateParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
        dateParams.topMargin = dpToPx(10);
        content.addView(date, dateParams);

        card.addView(content);

        card.setOnClickListener(v -> {
            Intent intent = new Intent( LessonDetailsActivity.this,
                    SummaryDetailsActivity.class );
            intent.putExtra("containerId", containerId);
            intent.putExtra("lessonId", lessonId);
            intent.putExtra("summaryId", summary.getId());
            startActivity(intent); });

        summariesContainer.addView(card);
    }



    private String formatSummaryTitle(String depth) {

        if (depth == null || depth.trim().isEmpty()) {
            return getString(R.string.summary_title_fallback);
        }

        String value = depth.trim();

        return getString(
                R.string.summary_title_format,
                Character.toUpperCase(value.charAt(0)) + value.substring(1)
        );
    }

    private void showCreateSummaryDialog() {

        String[] depths = {
                getString(R.string.summary_depth_short),
                getString(R.string.summary_depth_medium),
                getString(R.string.summary_depth_detailed)
        };
        String[] depthValues = {"short", "medium", "detailed"};

        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.summary_depth_title))
                .setSingleChoiceItems(depths, 1, (dialog, which) -> {
                    selectedSummaryDepth = depthValues[which];
                })
                .setNegativeButton(getString(R.string.action_cancel), null)
                .setPositiveButton(getString(R.string.action_continue), (dialog, which) -> {
            showSummaryScopeDialog();
        })
                .show();
    }

    private void showSummaryScopeDialog() {

        String[] options = {
                getString(R.string.quiz_scope_full_lesson),
                getString(R.string.quiz_scope_selected_topics)
        };

        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.summary_scope_title))
                .setItems(options, (dialog, which) -> {

                    if (which == 0) {
                        // Summarize the entire lesson
                        generateSummary(null);

                    } else {
                        // Let the user select specific topics
                        showSummaryTopicSelectionDialog();
                    }
                })
                .show();
    }


    private void showSummaryTopicSelectionDialog() {

        LessonResponse lesson = viewModel.getLesson().getValue();

        List<TopicResponse> topics =
                lesson != null ? lesson.getTopics() : null;

        if (topics == null || topics.isEmpty()) {
            Toast.makeText(
                    this,
                    getString(R.string.no_topics_available_summary),
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        LinearLayout topicsLayout = new LinearLayout(this);
        topicsLayout.setOrientation(LinearLayout.VERTICAL);
        topicsLayout.setPadding(
                dpToPx(16), dpToPx(8),
                dpToPx(16), dpToPx(8)
        );

        List<CheckBox> topicCheckBoxes = new ArrayList<>();

        for (TopicResponse topic : topics) {

            CheckBox checkBox = new CheckBox(this);
            checkBox.setText(topic.getTitle());
            checkBox.setTag(topic.getId());
            checkBox.setTextColor(ContextCompat.getColor(this, R.color.text_primary));

            checkBox.setButtonTintList(
                    android.content.res.ColorStateList.valueOf(
                            ContextCompat.getColor(this, R.color.purple_500)
                    )
            );

            topicsLayout.addView(checkBox);
            topicCheckBoxes.add(checkBox);
        }

        ScrollView scrollView = new ScrollView(this);
        scrollView.addView(topicsLayout);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(getString(R.string.select_topics_title))
                .setView(scrollView)
                .setNegativeButton(getString(R.string.action_cancel), null)
                .setPositiveButton(getString(R.string.generate_summary), null)
                .create();

        dialog.setOnShowListener(ignored ->
                dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                        .setOnClickListener(v -> {

                            List<String> selectedTopicIds = new ArrayList<>();

                            for (CheckBox checkBox : topicCheckBoxes) {
                                if (checkBox.isChecked()) {
                                    selectedTopicIds.add(
                                            String.valueOf(checkBox.getTag())
                                    );
                                }
                            }

                            if (selectedTopicIds.isEmpty()) {
                                Toast.makeText(
                                        this,
                                        getString(R.string.select_at_least_one_topic),
                                        Toast.LENGTH_SHORT
                                ).show();
                                return;
                            }

                            generateSummary(selectedTopicIds);
                            dialog.dismiss();
                        })
        );

        dialog.show();
    }


    private void generateSummary(List<String> selectedTopicIds) {

        CreateSummaryRequest request = new CreateSummaryRequest(
                selectedSummaryDepth,
                selectedTopicIds
        );

        summaryViewModel.createSummary(
                containerId,
                lessonId,
                request
        );
    }



}
