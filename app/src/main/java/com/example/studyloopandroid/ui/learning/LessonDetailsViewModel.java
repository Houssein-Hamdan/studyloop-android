package com.example.studyloopandroid.ui.learning;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.studyloopandroid.data.model.lesson.LessonResponse;
import com.example.studyloopandroid.data.repository.ApiCallback;
import com.example.studyloopandroid.data.repository.LessonDetailsRepository;
import com.example.studyloopandroid.data.model.lesson.CreateTopicRequest;
import com.example.studyloopandroid.data.model.lesson.LessonProgressResponse;

public class LessonDetailsViewModel extends AndroidViewModel {


    private final LessonDetailsRepository repository;

    private final MutableLiveData<LessonResponse> lesson =
            new MutableLiveData<>();

    private final MutableLiveData<Boolean> loading =
            new MutableLiveData<>(false);

    private final MutableLiveData<String> error =
            new MutableLiveData<>(null);

    private final MutableLiveData<LessonProgressResponse> lessonProgress =
            new MutableLiveData<>();

    private final MutableLiveData<String> topicProgressUpdateError =
            new MutableLiveData<>();

    public LiveData<LessonProgressResponse> getLessonProgress() {
        return lessonProgress;
    }

    public LiveData<String> getTopicProgressUpdateError() {
        return topicProgressUpdateError;
    }

    public LessonDetailsViewModel(@NonNull Application application) {
        super(application);
        repository = new LessonDetailsRepository(application);
    }

    public LiveData<LessonResponse> getLesson() {
        return lesson;
    }

    public LiveData<Boolean> getLoading() {
        return loading;
    }

    public LiveData<String> getError() {
        return error;
    }

    public void loadLessonDetails(
            String containerId,
            String lessonId
    ) {
        loading.setValue(true);
        error.setValue(null);
        lesson.setValue(null);

        repository.getLessonDetails(
                containerId,
                lessonId,
                new ApiCallback<LessonResponse>() {

                    @Override
                    public void onSuccess(LessonResponse result) {
                        lesson.postValue(result);
                        loading.postValue(false);
                    }

                    @Override
                    public void onError(String message) {
                        error.postValue(message);
                        loading.postValue(false);
                    }
                }
        );
    }
    public void createTopic(
            String containerId,
            String lessonId,
            String title,
            String description
    ) {
        loading.setValue(true);
        error.setValue(null);

        CreateTopicRequest request = new CreateTopicRequest(title, description);

        repository.createTopic(containerId, lessonId, request,
                new ApiCallback<Void>() {
                    @Override
                    public void onSuccess(Void result) {
                        loadLessonDetails(containerId, lessonId);
                    }

                    @Override
                    public void onError(String message) {
                        error.postValue(message);
                        loading.postValue(false);
                    }
                });
    }

    public void updateTopic(
            String containerId,
            String lessonId,
            String topicId,
            String title,
            String description
    ) {
        loading.setValue(true);
        error.setValue(null);

        CreateTopicRequest request =
                new CreateTopicRequest(title, description);

        repository.updateTopic(
                containerId,
                lessonId,
                topicId,
                request,
                new ApiCallback<Void>() {
                    @Override
                    public void onSuccess(Void result) {
                        loadLessonDetails(containerId, lessonId);
                    }

                    @Override
                    public void onError(String message) {
                        error.postValue(message);
                        loading.postValue(false);
                    }
                }
        );
    }

    public void deleteTopic(
            String containerId,
            String lessonId,
            String topicId
    ) {
        loading.setValue(true);
        error.setValue(null);

        repository.deleteTopic(
                containerId,
                lessonId,
                topicId,
                new ApiCallback<Void>() {
                    @Override
                    public void onSuccess(Void result) {
                        loadLessonDetails(containerId, lessonId);
                    }

                    @Override
                    public void onError(String message) {
                        error.postValue(message);
                        loading.postValue(false);
                    }
                }
        );
    }


    public void loadLessonProgress(
            String containerId,
            String lessonId
    ) {
        repository.getOrCreateProgress(
                containerId,
                lessonId,
                new ApiCallback<com.example.studyloopandroid.data.model.lesson.ProgressInitializationResponse>() {

                    @Override
                    public void onSuccess(
                            com.example.studyloopandroid.data.model.lesson.ProgressInitializationResponse result
                    ) {
                        repository.getLessonProgress(
                                containerId,
                                lessonId,
                                new ApiCallback<LessonProgressResponse>() {

                                    @Override
                                    public void onSuccess(LessonProgressResponse result) {
                                        lessonProgress.postValue(result);
                                    }

                                    @Override
                                    public void onError(String message) {
                                        error.postValue(message);
                                    }
                                }
                        );
                    }

                    @Override
                    public void onError(String message) {
                        error.postValue(message);
                    }
                }
        );
    }

    public void updateTopicProgress(
            String containerId,
            String lessonId,
            String topicId,
            boolean isCompleted
    ) {
        error.setValue(null);
        topicProgressUpdateError.setValue(null);

        repository.updateTopicProgress(
                containerId,
                lessonId,
                topicId,
                isCompleted,
                new ApiCallback<Void>() {

                    @Override
                    public void onSuccess(Void result) {
                        loadLessonProgress(containerId, lessonId);
                    }

                    @Override
                    public void onError(String message) {
                        error.postValue(message);
                        topicProgressUpdateError.postValue(message);
                    }
                }
        );
    }


}
