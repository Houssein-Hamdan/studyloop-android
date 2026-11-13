package com.example.studyloopandroid.ui.review;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.studyloopandroid.data.model.progress.DueReview;
import com.example.studyloopandroid.data.model.progress.DueReviewResponse;
import com.example.studyloopandroid.data.repository.ApiErrorMapper;
import com.example.studyloopandroid.data.repository.ReviewRepository;

import android.util.Log;
import com.example.studyloopandroid.data.model.progress.ReviewCompleteResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ReviewViewModel extends AndroidViewModel {

    private final ReviewRepository repository;

    private final MutableLiveData<List<DueReview>> dueReviews =
            new MutableLiveData<>();

    private final MutableLiveData<Boolean> isLoading =
            new MutableLiveData<>(false);

    private final MutableLiveData<String> errorMessage =
            new MutableLiveData<>();

    private final MutableLiveData<String> reviewActionMessage = new MutableLiveData<>();

    private final MutableLiveData<String> completingLessonId = new MutableLiveData<>();
    public LiveData<String> getCompletingLessonId() {
        return completingLessonId;
    }

    private final MutableLiveData<ReviewCompleteResponse> completedReview =
            new MutableLiveData<>();

    public LiveData<ReviewCompleteResponse> getCompletedReview() {
        return completedReview;
    }

    public LiveData<String> getReviewActionMessage() {
        return reviewActionMessage;
    }

    public ReviewViewModel(@NonNull Application application) {
        super(application);
        repository = new ReviewRepository(application);
    }

    public LiveData<List<DueReview>> getDueReviews() {
        return dueReviews;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public void loadDueReviews() {
        isLoading.setValue(true);
        errorMessage.setValue(null);

        repository.getDueReviews().enqueue(new Callback<DueReviewResponse>() {

            @Override
            public void onResponse(
                    @NonNull Call<DueReviewResponse> call,
                    @NonNull Response<DueReviewResponse> response
            ) {
                isLoading.setValue(false);

                if (response.isSuccessful() && response.body() != null) {
                    dueReviews.setValue(response.body().getReviews());
                } else {
                    errorMessage.setValue(
                            ApiErrorMapper.fromResponse(
                                    response,
                                    "Failed to load reviews."
                            )
                    );
                }
            }

            @Override
            public void onFailure(
                    @NonNull Call<DueReviewResponse> call,
                    @NonNull Throwable t
            ) {
                isLoading.setValue(false);
                errorMessage.setValue(
                        ApiErrorMapper.fromThrowable(t)
                );
            }
        });
    }

    public void completeReview(DueReview review) {
        if (review == null || review.getLesson() == null) {
            reviewActionMessage.setValue("Lesson information is unavailable.");
            return;
        }

        String containerId = review.getLesson().getContainerId();
        String lessonId = review.getLessonId();

        if (containerId == null || lessonId == null) {
            reviewActionMessage.setValue("Lesson information is incomplete.");
            return;
        }
        completingLessonId.setValue(lessonId);

        repository.completeReview(containerId, lessonId)
                .enqueue(new Callback<ReviewCompleteResponse>() {
                    @Override
                    public void onResponse(
                            @NonNull Call<ReviewCompleteResponse> call,
                            @NonNull Response<ReviewCompleteResponse> response
                    ) {
                        completingLessonId.setValue(null);

                        if (response.isSuccessful() && response.body() != null) {
                            completedReview.setValue(response.body());
                            reviewActionMessage.setValue("Review completed successfully!");

                            new android.os.Handler(android.os.Looper.getMainLooper())
                                    .postDelayed(() -> loadDueReviews(), 3000);
                        } else {
                            Log.e("ReviewViewModel",
                                    "Complete review failed: " + response.code());
                            reviewActionMessage.setValue(
                                    ApiErrorMapper.fromResponse(
                                            response,
                                            "Failed to complete review."
                                    )
                            );
                        }
                    }

                    @Override
                    public void onFailure(
                            @NonNull Call<ReviewCompleteResponse> call,
                            @NonNull Throwable t
                    ) {
                        completingLessonId.setValue(null);
                        Log.e("ReviewViewModel",
                                "Complete review network error", t);
                        reviewActionMessage.setValue(
                                ApiErrorMapper.fromThrowable(t)
                        );
                    }
                });
    }
}