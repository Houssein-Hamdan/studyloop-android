package com.example.studyloopandroid.ui.learning;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studyloopandroid.R;
import com.example.studyloopandroid.data.model.lesson.LessonResponse;

import java.util.ArrayList;
import java.util.List;

public class LessonAdapter
        extends RecyclerView.Adapter<LessonAdapter.LessonViewHolder> {


    public interface OnLessonClickListener {
        void onLessonClick(LessonResponse lesson);

        void onEditLesson(LessonResponse lesson);

        void onDeleteLesson(LessonResponse lesson);
    }

    private final List<LessonResponse> lessons = new ArrayList<>();
    private final OnLessonClickListener listener;

    public LessonAdapter(OnLessonClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public LessonViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_lesson, parent, false);

        return new LessonViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull LessonViewHolder holder,
            int position
    ) {
        LessonResponse lesson = lessons.get(position);

        holder.tvLessonTitle.setText(lesson.getTitle());

        String createdAt = lesson.getCreatedAt();

        holder.tvLessonDate.setText(
                createdAt != null && !createdAt.isEmpty()
                        ? createdAt
                        : holder.itemView.getContext()
                                .getString(R.string.lesson_date_fallback)
        );

        // Open lesson details.
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onLessonClick(lesson);
            }
        });

        // Edit lesson.
        holder.btnEditLesson.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEditLesson(lesson);
            }
        });

        // Delete lesson.
        holder.btnDeleteLesson.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeleteLesson(lesson);
            }
        });
    }

    @Override
    public int getItemCount() {
        return lessons.size();
    }

    public void updateLessons(List<LessonResponse> newLessons) {
        lessons.clear();

        if (newLessons != null) {
            lessons.addAll(newLessons);
        }

        notifyDataSetChanged();
    }

    static class LessonViewHolder extends RecyclerView.ViewHolder {

        TextView tvLessonTitle;
        TextView tvLessonDate;

        Button btnEditLesson;
        Button btnDeleteLesson;

        public LessonViewHolder(@NonNull View itemView) {
            super(itemView);

            tvLessonTitle = itemView.findViewById(R.id.tvLessonTitle);
            tvLessonDate = itemView.findViewById(R.id.tvLessonDate);

            btnEditLesson = itemView.findViewById(R.id.btnEditLesson);
            btnDeleteLesson = itemView.findViewById(R.id.btnDeleteLesson);
        }
    }


}
