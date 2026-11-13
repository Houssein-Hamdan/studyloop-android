package com.example.studyloopandroid.ui.ask;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.content.Context;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import io.noties.markwon.Markwon;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.studyloopandroid.R;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.button.MaterialButton;
import io.noties.markwon.ext.tables.TablePlugin;

public class AskAIActivity extends AppCompatActivity {

    private AskViewModel viewModel;

    private LinearLayout askMessagesContainer;
    private ScrollView askChatScroll;

    private View askWelcomeContainer;
    private View askLoadingContainer;

    private EditText etAskQuestion;
    private MaterialButton btnSendQuestion;

    private Markwon markwon;

    private boolean requestInProgress = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_ask_aiactivity);

        markwon = Markwon.builder(this)
                .usePlugin(TablePlugin.create(this))
                .build();
        setupInsets();
        initializeViews();
        initializeViewModel();
        setupListeners();
        observeViewModel();
    }

    private void setupInsets() {
        View root = findViewById(R.id.askRoot);

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

    private void initializeViews() {
        askMessagesContainer = findViewById(R.id.askMessagesContainer);
        askChatScroll = findViewById(R.id.askChatScroll);

        askWelcomeContainer = findViewById(R.id.askWelcomeContainer);
        askLoadingContainer = findViewById(R.id.askLoadingContainer);

        etAskQuestion = findViewById(R.id.etAskQuestion);
        btnSendQuestion = findViewById(R.id.btnSendQuestion);
    }

    private void initializeViewModel() {
        viewModel = new ViewModelProvider(this)
                .get(AskViewModel.class);
    }

    private void setupListeners() {

        findViewById(R.id.btnAskBack).setOnClickListener(v ->
                finish()
        );

        btnSendQuestion.setOnClickListener(v ->
                sendQuestion()
        );

        etAskQuestion.setOnEditorActionListener((v, actionId, event) -> {

            if (actionId == EditorInfo.IME_ACTION_SEND
                    || actionId == EditorInfo.IME_ACTION_DONE) {

                sendQuestion();
                return true;
            }

            return false;
        });

        findViewById(R.id.suggestionJava).setOnClickListener(v ->
                etAskQuestion.setText(
                        getString(R.string.ask_suggestion_java_prompt)
                )
        );

        findViewById(R.id.suggestionStudy).setOnClickListener(v ->
                etAskQuestion.setText(
                        getString(R.string.ask_suggestion_study_prompt)
                )
        );
    }

    private void observeViewModel() {

        viewModel.getLoading().observe(this, isLoading -> {

            boolean loading = Boolean.TRUE.equals(isLoading);

            requestInProgress = loading;

            askLoadingContainer.setVisibility(
                    loading ? View.VISIBLE : View.GONE
            );

            btnSendQuestion.setEnabled(!loading);
            etAskQuestion.setEnabled(!loading);

            btnSendQuestion.setAlpha(loading ? 0.6f : 1f);
        });

        viewModel.getAnswer().observe(this, answer -> {

            if (answer == null || answer.trim().isEmpty()) {
                return;
            }

            addMessage(new AskMessage(
                    answer,
                    AskMessage.Type.AI
            ));
        });

        viewModel.getError().observe(this, error -> {

            if (error == null || error.trim().isEmpty()) {
                return;
            }

            Toast.makeText(
                    this,
                    error,
                    Toast.LENGTH_LONG
            ).show();
        });
    }

    private void sendQuestion() {

        if (requestInProgress) {
            return;
        }

        String question = etAskQuestion.getText()
                .toString()
                .trim();

        if (question.isEmpty()) {
            etAskQuestion.setError(getString(R.string.ask_enter_question));
            return;
        }

        // Hide the welcome suggestions after the first question.
        askWelcomeContainer.setVisibility(View.GONE);

        // Display the user's message immediately.
        addMessage(new AskMessage(
                question,
                AskMessage.Type.USER
        ));

        // Clear the input and hide the keyboard.
        etAskQuestion.setText("");

        InputMethodManager imm =
                (InputMethodManager) getSystemService(
                        Context.INPUT_METHOD_SERVICE
                );

        if (imm != null) {
            imm.hideSoftInputFromWindow(
                    etAskQuestion.getWindowToken(),
                    0
            );
        }

        // Send the question to the backend.
        viewModel.askQuestion(question);
    }

    private void addMessage(AskMessage message) {

        View messageView = getLayoutInflater()
                .inflate(
                        R.layout.item_ask_message,
                        askMessagesContainer,
                        false
                );

        LinearLayout messageRow =
                messageView.findViewById(R.id.messageRow);

        MaterialCardView messageBubble =
                messageView.findViewById(R.id.messageBubble);

        TextView tvMessageText =
                messageView.findViewById(R.id.tvMessageText);

        if (message.getType() == AskMessage.Type.AI) {
            markwon.setMarkdown(tvMessageText, message.getText());
        } else {
            tvMessageText.setText(message.getText());
        }

        LinearLayout.LayoutParams params =
                (LinearLayout.LayoutParams) messageRow.getLayoutParams();

        if (message.getType() == AskMessage.Type.USER) {

            messageRow.setGravity(Gravity.END);

            params.setMargins(
                    dpToPx(32),
                    dpToPx(6),
                    0,
                    dpToPx(6)
            );

            messageBubble.setCardBackgroundColor(
                    ContextCompat.getColor(this, R.color.lavender_chip)
            );

            messageBubble.setStrokeWidth(0);

        } else {

            messageRow.setGravity(Gravity.START);

            params.setMargins(
                    0,
                    dpToPx(6),
                    dpToPx(32),
                    dpToPx(6)
            );

            messageBubble.setCardBackgroundColor(ContextCompat.getColor(this, R.color.white));
        }

        messageRow.setLayoutParams(params);

        askMessagesContainer.addView(messageView);

        askChatScroll.post(() ->
                askChatScroll.fullScroll(View.FOCUS_DOWN)
        );
    }

    private int dpToPx(int dp) {
        return (int) (
                dp * getResources().getDisplayMetrics().density + 0.5f
        );
    }
}