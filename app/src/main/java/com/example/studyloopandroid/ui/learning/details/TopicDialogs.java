package com.example.studyloopandroid.ui.learning.details;

import android.content.Context;
import android.view.Gravity;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.appcompat.app.AlertDialog;

import com.example.studyloopandroid.R;
import com.example.studyloopandroid.data.model.lesson.TopicResponse;

/**
 * Topic create, edit, and delete dialogs for the lesson details screen.
 *
 * <p>This class only builds dialogs and validates their input. The caller
 * remains responsible for invoking the ViewModel.</p>
 */
public final class TopicDialogs {

    private TopicDialogs() {
    }

    public static void showCreate(
            Context context,
            OnTopicSubmittedListener listener
    ) {
        showTopicForm(
                context,
                context.getString(R.string.create_topic_title),
                context.getString(R.string.action_create),
                null,
                null,
                listener
        );
    }

    public static void showEdit(
            Context context,
            TopicResponse topic,
            OnTopicSubmittedListener listener
    ) {
        showTopicForm(
                context,
                context.getString(R.string.edit_topic_title),
                context.getString(R.string.action_save),
                topic.getTitle(),
                topic.getDescription(),
                listener
        );
    }

    public static void showDelete(
            Context context,
            TopicResponse topic,
            Runnable onConfirm
    ) {
        new AlertDialog.Builder(context)
                .setTitle(context.getString(R.string.delete_topic_title))
                .setMessage(
                        context.getString(
                                R.string.delete_topic_message,
                                topic.getTitle()
                        )
                )
                .setNegativeButton(
                        context.getString(R.string.action_cancel),
                        null
                )
                .setPositiveButton(
                        context.getString(R.string.action_delete),
                        (dialog, which) -> onConfirm.run()
                )
                .show();
    }

    private static void showTopicForm(
            Context context,
            String dialogTitle,
            String positiveLabel,
            String initialTitle,
            String initialDescription,
            OnTopicSubmittedListener listener
    ) {
        EditText inputTitle = new EditText(context);
        inputTitle.setHint(context.getString(R.string.topic_title_placeholder));
        inputTitle.setSingleLine(true);

        if (initialTitle != null) {
            inputTitle.setText(initialTitle);
        }

        EditText inputDescription = new EditText(context);
        inputDescription.setHint(
                context.getString(R.string.description_optional_hint)
        );
        inputDescription.setMinLines(2);
        inputDescription.setGravity(Gravity.TOP);
        inputDescription.setInputType(
                android.text.InputType.TYPE_CLASS_TEXT
                        | android.text.InputType.TYPE_TEXT_FLAG_MULTI_LINE
        );

        if (initialDescription != null) {
            inputDescription.setText(initialDescription);
        }

        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(48, 16, 48, 0);
        layout.addView(inputTitle);
        layout.addView(inputDescription);

        AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle(dialogTitle)
                .setView(layout)
                .setNegativeButton(
                        context.getString(R.string.action_cancel),
                        null
                )
                .setPositiveButton(positiveLabel, null)
                .create();

        dialog.setOnShowListener(ignored ->
                dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                        .setOnClickListener(v -> {
                            String title = inputTitle.getText()
                                    .toString().trim();

                            String description = inputDescription.getText()
                                    .toString().trim();

                            if (title.isEmpty()) {
                                inputTitle.setError(
                                        context.getString(
                                                R.string.topic_title_required_dialog
                                        )
                                );
                                inputTitle.requestFocus();
                                return;
                            }

                            listener.onTopicSubmitted(
                                    title,
                                    description.isEmpty()
                                            ? null
                                            : description
                            );

                            dialog.dismiss();
                        })
        );

        dialog.show();
    }

    public interface OnTopicSubmittedListener {
        void onTopicSubmitted(String title, String description);
    }
}
