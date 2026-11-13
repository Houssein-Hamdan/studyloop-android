package com.example.studyloopandroid.ui.dashboard;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.studyloopandroid.data.model.lesson.RecentLessonResponse;
import com.example.studyloopandroid.data.model.lesson.RecentLessonsResponse;
import com.example.studyloopandroid.data.repository.AnalyticsRepository;
import com.example.studyloopandroid.data.repository.ApiErrorMapper;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecentLessonsViewModel extends AndroidViewModel {

    private final AnalyticsRepository repository;

    private final MutableLiveData<List<RecentLessonResponse>> recentLessons =
            new MutableLiveData<>();

    private final MutableLiveData<Boolean> isLoading =
            new MutableLiveData<>(false);

    private final MutableLiveData<String> errorMessage =
            new MutableLiveData<>();

    public RecentLessonsViewModel(@NonNull Application application) {
        super(application);
        repository = new AnalyticsRepository(application);
    }

    public LiveData<List<RecentLessonResponse>> getRecentLessons() {
        return recentLessons;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public void loadRecentLessons() {
        isLoading.setValue(true);
        errorMessage.setValue(null);

        repository.getRecentLessons().enqueue(new Callback<RecentLessonsResponse>() {

            @Override
            public void onResponse(
                    @NonNull Call<RecentLessonsResponse> call,
                    @NonNull Response<RecentLessonsResponse> response
            ) {
                isLoading.setValue(false);

                if (response.isSuccessful() && response.body() != null) {
                    recentLessons.setValue(response.body().getRecentActivity());
                } else {
                    errorMessage.setValue(
                            ApiErrorMapper.fromResponse(
                                    response,
                                    "Failed to load recent lessons."
                            )
                    );
                }
            }

            @Override
            public void onFailure(
                    @NonNull Call<RecentLessonsResponse> call,
                    @NonNull Throwable t
            ) {
                isLoading.setValue(false);
                errorMessage.setValue(ApiErrorMapper.fromThrowable(t));
            }
        });
    }

}
