package com.example.studyloopandroid.data.model.lesson;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class RecentLessonsResponse {

    @SerializedName("recentActivity")
    private List<RecentLessonResponse> recentActivity;

    public List<RecentLessonResponse> getRecentActivity() {
        return recentActivity;
    }

}
