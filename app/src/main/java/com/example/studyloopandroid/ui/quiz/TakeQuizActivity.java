package com.example.studyloopandroid.ui.quiz;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;
import android.widget.Toast;
import com.example.studyloopandroid.data.model.quiz.QuizResult;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.studyloopandroid.R;
import com.example.studyloopandroid.data.model.quiz.Quiz;
import com.example.studyloopandroid.data.model.quiz.QuizAnswerRequest;
import com.example.studyloopandroid.data.model.quiz.SubmitQuizRequest;
import com.example.studyloopandroid.data.model.quiz.QuizQuestion;


import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;


public class TakeQuizActivity extends AppCompatActivity {

    private String containerId;
    private String lessonId;

    private Map<String, String> userAnswers = new HashMap<>();
    private String quizId;

    private TextView tvQuizTitle;
    private TextView tvAnswerCounter;
    private TextView tvQuizInfo;
    private TextView tvTakeQuizError;
    private ProgressBar takeQuizProgressBar;

    private LinearLayout questionsContainer;

    private QuizViewModel quizViewModel;
    private Quiz currentQuiz;

    private MaterialButton btnSubmitQuiz;

    private boolean quizSubmitted = false;
    private final List<View> answerViews = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_quiz);

        tvQuizTitle = findViewById(R.id.tvQuizTitle);
        tvQuizInfo = findViewById(R.id.tvQuizInfo);
        tvTakeQuizError = findViewById(R.id.tvTakeQuizError);
        takeQuizProgressBar = findViewById(R.id.takeQuizProgressBar);
        questionsContainer = findViewById(R.id.questionsContainer);
        btnSubmitQuiz = findViewById(R.id.btnSubmitQuiz);
        tvAnswerCounter = findViewById(R.id.tvAnswerCounter);

        btnSubmitQuiz.setOnClickListener(v -> submitQuiz());

        containerId = getIntent().getStringExtra("containerId");
        lessonId = getIntent().getStringExtra("lessonId");
        quizId = getIntent().getStringExtra("quizId");

        if (containerId == null || lessonId == null || quizId == null) {
            finish();
            return;
        }

        quizViewModel = new ViewModelProvider(this)
                .get(QuizViewModel.class);

        observeQuizViewModel();

        quizViewModel.loadQuiz(containerId, lessonId, quizId);
    }

    private void observeQuizViewModel() {

        quizViewModel.getSelectedQuiz().observe(this, quiz -> {

            if (quiz == null) return;

            currentQuiz = quiz;

            tvQuizTitle.setText(getString(R.string.quiz_title));

            tvQuizInfo.setText(
                    getString(
                            R.string.quiz_info_format,
                            quiz.getDifficulty(),
                            quiz.getFormat(),
                            quiz.getQuestionCount()
                    )
            );

            displayQuestions(quiz);
            updateAnswerCounter();
        });

        quizViewModel.getLoading().observe(this, isLoading -> {

            if (isLoading == null) return;

            takeQuizProgressBar.setVisibility(
                    isLoading ? View.VISIBLE : View.GONE
            );
        });

        quizViewModel.getError().observe(this, error -> {

            if (error == null || error.isEmpty()) {
                tvTakeQuizError.setVisibility(View.GONE);
                return;
            }

            tvTakeQuizError.setText(error);
            tvTakeQuizError.setVisibility(View.VISIBLE);
        });

        quizViewModel.getQuizResult().observe(this, response -> {

            if (response == null || response.getResults() == null) {
                return;
            }

            lockQuiz();

            QuizResult result = response.getResults();
            String percentage = result.getPercentage();

            new MaterialAlertDialogBuilder(this)
                    .setTitle(getString(R.string.quiz_completed_title))
                    .setMessage(
                            getString(
                                    R.string.quiz_result_message,
                                    percentage,
                                    result.getCorrectAnswers(),
                                    result.getTotalQuestions(),
                                    result.getScore()
                            )
                    )
                    .setPositiveButton(getString(R.string.action_done), (dialog, which) -> finish())
                    .setCancelable(false)
                    .show();
        });


        quizViewModel.getOperationLoading().observe(this, isLoading -> {

            if (isLoading == null) return;

            btnSubmitQuiz.setEnabled(!isLoading);

            btnSubmitQuiz.setText(
                    isLoading ? getString(R.string.quiz_submitting) : getString(R.string.quiz_submit)
            );
        });

        quizViewModel.getOperationError().observe(this, error -> {

            if (error == null || error.isEmpty()) return;

            new MaterialAlertDialogBuilder(this)
                    .setTitle(getString(R.string.quiz_submission_failed))
                    .setMessage(error)
                    .setPositiveButton(getString(R.string.action_ok), null)
                    .show();

            quizViewModel.clearOperationMessages();
        });

    }

    private void displayQuestions(Quiz quiz) {

        questionsContainer.removeAllViews();
        answerViews.clear();

        List<QuizQuestion> questions = quiz.getQuestions();

        if (questions == null || questions.isEmpty()) {
            TextView emptyMessage = new TextView(this);
            emptyMessage.setText(getString(R.string.quiz_no_questions));
            questionsContainer.addView(emptyMessage);
            return;
        }

        int questionNumber = 1;

        for (QuizQuestion question : questions) {

            LinearLayout questionLayout = new LinearLayout(this);
            questionLayout.setOrientation(LinearLayout.VERTICAL);

            questionLayout.setPadding(
                    dpToPx(16),
                    dpToPx(16),
                    dpToPx(16),
                    dpToPx(16)
            );

            TextView questionTitle = new TextView(this);

            questionTitle.setText(
                    getString(
                            R.string.quiz_question_format,
                            questionNumber++,
                            question.getQuestionText()
                    )
            );

            questionTitle.setTextSize(16);
            questionTitle.setTypeface(
                    null,
                    android.graphics.Typeface.BOLD
            );

            questionLayout.addView(questionTitle);

            String format = question.getFormat();

            if ("multiple_choice".equals(format) ||
                    "true_false".equals(format)) {

                RadioGroup radioGroup = new RadioGroup(this);
                radioGroup.setOrientation(RadioGroup.VERTICAL);

                List<String> options = question.getOptions();

                if (options != null) {
                    for (String option : options) {
                        RadioButton radioButton = new RadioButton(this);
                        radioButton.setText(option);
                        radioGroup.addView(radioButton);
                    }

                    answerViews.add(radioGroup);
                }

                radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
                    RadioButton selectedButton = group.findViewById(checkedId);

                    if (selectedButton != null) {
                        userAnswers.put(
                                question.getId(),
                                selectedButton.getText().toString()
                        );
                        updateAnswerCounter();
                    }

                });

                questionLayout.addView(radioGroup);


            } else if ("open_text".equals(format) ||
                    "fill_blanks".equals(format)) {


                EditText answerInput = new EditText(this);
                answerInput.setHint(getString(R.string.quiz_answer_hint));
                answerInput.setMinLines(2);
                answerViews.add(answerInput);

                answerInput.addTextChangedListener(new android.text.TextWatcher() {
                    @Override
                    public void beforeTextChanged(
                            CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(
                            CharSequence s, int start, int before, int count) {
                        userAnswers.put(question.getId(), s.toString());
                        updateAnswerCounter();
                    }

                    @Override
                    public void afterTextChanged(android.text.Editable s) {
                    }
                });

                answerInput.setGravity(
                        android.view.Gravity.TOP |
                                android.view.Gravity.START
                );

                questionLayout.addView(answerInput);

            }


            LinearLayout.LayoutParams params =
                    new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );

            params.setMargins(
                    0,
                    0,
                    0,
                    dpToPx(16)
            );

            questionLayout.setLayoutParams(params);

            questionsContainer.addView(questionLayout);
        }
    }

    private int dpToPx(int dp) {
        return (int) (dp * getResources()
                .getDisplayMetrics().density);
    }

    private void submitQuiz() {

        if (quizSubmitted) {
            return;
        }

        if (currentQuiz == null || currentQuiz.getQuestions() == null) {
            return;
        }

        List<QuizQuestion> questions = currentQuiz.getQuestions();

        if (userAnswers.size() < questions.size()) {
            Toast.makeText(
                    this,
                    getString(R.string.quiz_answer_all_required),
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        List<QuizAnswerRequest> answers = new ArrayList<>();

        for (QuizQuestion question : questions) {

            String answer = userAnswers.get(question.getId());

            if (answer == null || answer.trim().isEmpty()) {
                Toast.makeText(
                        this,
                        getString(R.string.quiz_answer_all_required),
                        Toast.LENGTH_SHORT
                ).show();
                return;
            }

            answers.add(
                    new QuizAnswerRequest(
                            question.getId(),
                            answer.trim()
                    )
            );
        }

        SubmitQuizRequest request = new SubmitQuizRequest(answers);

        quizViewModel.submitQuiz(
                containerId,
                lessonId,
                quizId,
                request
        );

    }



    private void updateAnswerCounter() {

        if (currentQuiz == null || currentQuiz.getQuestions() == null) {
            return;
        }

        int total = currentQuiz.getQuestions().size();
        int answered = 0;

        for (String answer : userAnswers.values()) {
            if (answer != null && !answer.trim().isEmpty()) {
                answered++;
            }
        }

        tvAnswerCounter.setText(
                getString(R.string.quiz_answer_counter_format, answered, total)
        );
    }


    private void lockQuiz() {
        quizSubmitted = true;

        for (View answerView : answerViews) {
            answerView.setEnabled(false);
        }

        btnSubmitQuiz.setEnabled(false);
        btnSubmitQuiz.setText(getString(R.string.quiz_submitted));
    }


}
