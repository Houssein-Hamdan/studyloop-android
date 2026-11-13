package com.example.studyloopandroid.ui.learning.details;

import android.content.Context;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.lifecycle.LifecycleOwner;

import com.example.studyloopandroid.R;
import com.example.studyloopandroid.data.model.lesson.LessonProgressResponse;
import com.example.studyloopandroid.data.model.lesson.TopicProgressResponse;
import com.example.studyloopandroid.data.model.lesson.TopicResponse;
import com.example.studyloopandroid.ui.learning.LessonDetailsViewModel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * UI coordinator for the topic section of the lesson details screen.
 *
 * <p>Owns topic rendering, expand/collapse state, and the optimistic checkbox
 * state. All API work is delegated to {@link LessonDetailsViewModel}.</p>
 */
public final class TopicSectionController {

    private final Context context;
    private final LessonDetailsViewModel viewModel;
    private final LinearLayout topicsContainer;
    private final TextView tvTopicsCount;
    private final View cardEmptyTopics;
    private final String containerId;
    private final String lessonId;

    private final Map<String, Boolean> topicCompletionStates =
            new HashMap<>();

    private String pendingProgressTopicId;
    private boolean pendingProgressPreviousState;

    public TopicSectionController(
            Context context,
            LifecycleOwner lifecycleOwner,
            LessonDetailsViewModel viewModel,
            LinearLayout topicsContainer,
            TextView tvTopicsCount,
            View cardEmptyTopics,
            String containerId,
            String lessonId
    ) {
        this.context = context;
        this.viewModel = viewModel;
        this.topicsContainer = topicsContainer;
        this.tvTopicsCount = tvTopicsCount;
        this.cardEmptyTopics = cardEmptyTopics;
        this.containerId = containerId;
        this.lessonId = lessonId;

        observeProgress(lifecycleOwner);
    }

    public void showCreateTopicDialog() {
        TopicDialogs.showCreate(
                context,
                (title, description) -> viewModel.createTopic(
                        containerId,
                        lessonId,
                        title,
                        description
                )
        );
    }

    public void renderTopics(List<TopicResponse> topics) {
        int count = topics == null ? 0 : topics.size();

        tvTopicsCount.setText(
                count == 1
                        ? context.getString(
                                R.string.lesson_topics_count_one,
                                count
                        )
                        : context.getString(
                                R.string.lesson_topics_count_many,
                                count
                        )
        );

        topicsContainer.removeAllViews();

        if (count == 0) {
            cardEmptyTopics.setVisibility(View.VISIBLE);
            return;
        }

        cardEmptyTopics.setVisibility(View.GONE);

        LayoutInflater inflater = LayoutInflater.from(context);

        for (TopicResponse topic : topics) {
            View topicView = inflater.inflate(
                    R.layout.item_topic,
                    topicsContainer,
                    false
            );
            topicView.setTag(topic.getId());

            CheckBox cbTopicCompleted =
                    topicView.findViewById(R.id.cbTopicCompleted);
            cbTopicCompleted.setButtonTintList(null);

            TextView tvTopicTitle =
                    topicView.findViewById(R.id.tvTopicTitle);

            TextView tvTopicDescription =
                    topicView.findViewById(R.id.tvTopicDescription);

            TextView tvTopicArrow =
                    topicView.findViewById(R.id.tvTopicArrow);

            View topicHeader =
                    topicView.findViewById(R.id.topicHeader);

            ImageButton btnEdit =
                    topicView.findViewById(R.id.btnEditTopic);
            ImageButton btnDelete =
                    topicView.findViewById(R.id.btnDeleteTopic);

            btnEdit.setOnClickListener(v ->
                    TopicDialogs.showEdit(
                            context,
                            topic,
                            (title, description) -> viewModel.updateTopic(
                                    containerId,
                                    lessonId,
                                    topic.getId(),
                                    title,
                                    description
                            )
                    )
            );

            btnDelete.setOnClickListener(v ->
                    TopicDialogs.showDelete(
                            context,
                            topic,
                            () -> viewModel.deleteTopic(
                                    containerId,
                                    lessonId,
                                    topic.getId()
                            )
                    )
            );

            tvTopicTitle.setText(topic.getTitle());

            cbTopicCompleted.setOnCheckedChangeListener(null);

            boolean isCompleted = Boolean.TRUE.equals(
                    topicCompletionStates.get(topic.getId())
            );

            cbTopicCompleted.setChecked(isCompleted);

            cbTopicCompleted.setOnCheckedChangeListener(
                    (buttonView, checked) ->
                            handleTopicProgressToggle(
                                    topic.getId(),
                                    checked
                            )
            );

            String description = topic.getDescription();

            if (description == null || description.trim().isEmpty()) {
                description = context.getString(
                        R.string.topic_no_description
                );
            }

            tvTopicDescription.setText(description);

            tvTopicDescription.setVisibility(View.GONE);
            tvTopicArrow.setText(
                    context.getString(R.string.symbol_chevron_down)
            );

            topicHeader.setOnClickListener(v -> {
                boolean isExpanded =
                        tvTopicDescription.getVisibility() == View.VISIBLE;

                AutoTransition transition = new AutoTransition();
                transition.setDuration(200);

                TransitionManager.beginDelayedTransition(
                        topicsContainer,
                        transition
                );

                if (isExpanded) {
                    tvTopicDescription.setVisibility(View.GONE);
                    tvTopicArrow.setText(
                            context.getString(R.string.symbol_chevron_down)
                    );
                } else {
                    tvTopicDescription.setVisibility(View.VISIBLE);
                    tvTopicArrow.setText(
                            context.getString(R.string.symbol_chevron_up)
                    );
                }
            });

            topicsContainer.addView(topicView);
        }
    }

    private void observeProgress(LifecycleOwner lifecycleOwner) {
        viewModel.getLessonProgress().observe(lifecycleOwner, response -> {
            if (response == null) {
                return;
            }

            pendingProgressTopicId = null;

            topicCompletionStates.clear();

            List<TopicProgressResponse> topicProgress =
                    response.getTopicProgress();

            if (topicProgress != null) {
                for (TopicProgressResponse progress : topicProgress) {
                    topicCompletionStates.put(
                            progress.getTopicId(),
                            progress.isCompleted()
                    );
                }
            }

            for (int i = 0; i < topicsContainer.getChildCount(); i++) {
                View topicView = topicsContainer.getChildAt(i);

                String topicId = (String) topicView.getTag();

                if (topicId == null) {
                    continue;
                }

                CheckBox checkBox =
                        topicView.findViewById(R.id.cbTopicCompleted);

                boolean completed = Boolean.TRUE.equals(
                        topicCompletionStates.get(topicId)
                );

                checkBox.setOnCheckedChangeListener(null);
                checkBox.setChecked(completed);

                checkBox.setOnCheckedChangeListener(
                        (buttonView, checked) ->
                                handleTopicProgressToggle(topicId, checked)
                );
            }
        });

        viewModel.getTopicProgressUpdateError().observe(
                lifecycleOwner,
                error -> {
                    if (error != null && !error.isEmpty()) {
                        revertPendingTopicProgress();
                    }
                }
        );
    }

    private void handleTopicProgressToggle(
            String topicId,
            boolean checked
    ) {
        pendingProgressTopicId = topicId;
        pendingProgressPreviousState = !checked;

        topicCompletionStates.put(topicId, checked);

        viewModel.updateTopicProgress(
                containerId,
                lessonId,
                topicId,
                checked
        );
    }

    private void revertPendingTopicProgress() {
        if (pendingProgressTopicId == null) {
            return;
        }

        String topicId = pendingProgressTopicId;
        boolean previousState = pendingProgressPreviousState;

        pendingProgressTopicId = null;

        topicCompletionStates.put(topicId, previousState);

        for (int i = 0; i < topicsContainer.getChildCount(); i++) {
            View topicView = topicsContainer.getChildAt(i);

            if (!topicId.equals(topicView.getTag())) {
                continue;
            }

            CheckBox checkBox =
                    topicView.findViewById(R.id.cbTopicCompleted);

            checkBox.setOnCheckedChangeListener(null);
            checkBox.setChecked(previousState);
            checkBox.setOnCheckedChangeListener(
                    (buttonView, checked) ->
                            handleTopicProgressToggle(topicId, checked)
            );

            break;
        }
    }
}
