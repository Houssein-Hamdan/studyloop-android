package com.example.studyloopandroid.ui.learning;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.studyloopandroid.data.model.lesson.CreateLessonRequest;
import com.example.studyloopandroid.data.model.lesson.LessonResponse;
import com.example.studyloopandroid.data.model.lesson.UpdateLessonRequest;
import com.example.studyloopandroid.data.repository.ApiCallback;
import com.example.studyloopandroid.data.repository.LessonRepository;

import java.util.ArrayList;
import java.util.List;

public class LessonsViewModel extends AndroidViewModel {

    private final LessonRepository repository;

    private final MutableLiveData<List<LessonResponse>> lessons =
            new MutableLiveData<>(new ArrayList<>());

    private final MutableLiveData<Boolean> loading =
            new MutableLiveData<>(false);

    private final MutableLiveData<Boolean> operationLoading =
            new MutableLiveData<>(false);

    private final MutableLiveData<String> error =
            new MutableLiveData<>(null);

    private final MutableLiveData<String> operationError =
            new MutableLiveData<>(null);

    private final MutableLiveData<String> operationSuccess =
            new MutableLiveData<>(null);

    public LessonsViewModel(@NonNull Application application) {
        super(application);
        repository = new LessonRepository(application);
    }

    public LiveData<List<LessonResponse>> getLessons() {
        return lessons;
    }

    public LiveData<Boolean> getLoading() {
        return loading;
    }

    public LiveData<String> getError() {
        return error;
    }

    public LiveData<Boolean> getOperationLoading() {
        return operationLoading;
    }

    public LiveData<String> getOperationError() {
        return operationError;
    }

    public LiveData<String> getOperationSuccess() {
        return operationSuccess;
    }

    public void loadLessons(String containerId) {

        loading.setValue(true);
        error.setValue(null);

        repository.getLessons(
                containerId,
                new ApiCallback<List<LessonResponse>>() {

                    @Override
                    public void onSuccess(List<LessonResponse> result) {
                        lessons.postValue(result);
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

    public void createLesson(
            String containerId,
            CreateLessonRequest request
    ) {
        operationLoading.setValue(true);
        operationError.setValue(null);
        operationSuccess.setValue(null);

        repository.createLesson(
                containerId,
                request,
                new ApiCallback<Boolean>() {

                    @Override
                    public void onSuccess(Boolean result) {
                        operationLoading.postValue(false);
                        operationSuccess.postValue(
                                "Lesson created successfully."
                        );
                        loadLessons(containerId);
                    }

                    @Override
                    public void onError(String message) {
                        operationLoading.postValue(false);
                        operationError.postValue(message);
                    }
                }
        );
    }

    public void updateLesson(
            String containerId,
            String lessonId,
            String title
    ) {
        operationLoading.setValue(true);
        operationError.setValue(null);
        operationSuccess.setValue(null);

        UpdateLessonRequest request =
                new UpdateLessonRequest(title);

        repository.updateLesson(
                containerId,
                lessonId,
                request,
                new ApiCallback<Boolean>() {

                    @Override
                    public void onSuccess(Boolean result) {
                        operationLoading.postValue(false);
                        operationSuccess.postValue(
                                "Lesson updated successfully."
                        );
                        loadLessons(containerId);
                    }

                    @Override
                    public void onError(String message) {
                        operationLoading.postValue(false);
                        operationError.postValue(message);
                    }
                }
        );
    }

    public void deleteLesson(
            String containerId,
            String lessonId
    ) {
        operationLoading.setValue(true);
        operationError.setValue(null);
        operationSuccess.setValue(null);

        repository.deleteLesson(
                containerId,
                lessonId,
                new ApiCallback<Boolean>() {

                    @Override
                    public void onSuccess(Boolean result) {
                        operationLoading.postValue(false);
                        operationSuccess.postValue(
                                "Lesson deleted successfully."
                        );
                        loadLessons(containerId);
                    }

                    @Override
                    public void onError(String message) {
                        operationLoading.postValue(false);
                        operationError.postValue(message);
                    }
                }
        );
    }

    public void clearOperationMessages() {
        operationError.setValue(null);
        operationSuccess.setValue(null);
    }

}
