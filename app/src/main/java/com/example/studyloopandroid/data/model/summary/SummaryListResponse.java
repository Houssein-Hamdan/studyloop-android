package com.example.studyloopandroid.data.model.summary;

import java.util.List;

public class SummaryListResponse {

    private String message;
    private int count;
    private List<Summary> summaries;

    public String getMessage() {
        return message;
    }

    public int getCount() {
        return count;
    }

    public List<Summary> getSummaries() {
        return summaries;
    }
}