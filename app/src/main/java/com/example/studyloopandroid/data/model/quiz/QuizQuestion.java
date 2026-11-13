package com.example.studyloopandroid.data.model.quiz;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class QuizQuestion {

    private String id;

    @SerializedName("questionText")
    private String questionText;

    private String format;

    private int order;

    private List<String> options;

    public String getId() {
        return id;
    }

    public String getQuestionText() {
        return questionText;
    }

    public String getFormat() {
        return format;
    }

    public int getOrder() {
        return order;
    }

    public List<String> getOptions() {
        return options;
    }


}
