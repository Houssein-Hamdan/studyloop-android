package com.example.studyloopandroid.data.model.summary;

import java.util.List;

public class CreateSummaryRequest {

    private String depth;
    private List<String> selectedTopicIds;

    public CreateSummaryRequest(String depth, List<String> selectedTopicIds) {
        this.depth = depth;
        this.selectedTopicIds = selectedTopicIds;
    }

    public String getDepth() {
        return depth;
    }

    public List<String> getSelectedTopicIds() {
        return selectedTopicIds;
    }
}