package com.example.studyloopandroid.ui.quiz;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.studyloopandroid.data.model.quiz.CreateQuizRequest;
import com.example.studyloopandroid.data.model.quiz.CreateQuizResponse;
import com.example.studyloopandroid.data.model.quiz.Quiz;
import com.example.studyloopandroid.data.model.quiz.QuizDetailsResponse;
import com.example.studyloopandroid.data.model.quiz.QuizListResponse;
import com.example.studyloopandroid.data.model.quiz.SubmitQuizRequest;
import com.example.studyloopandroid.data.model.quiz.SubmitQuizResponse;
import com.example.studyloopandroid.data.repository.ApiCallback;
import com.example.studyloopandroid.data.repository.QuizRepository;

import java.util.ArrayList;
import java.util.List;

public class QuizViewModel extends AndroidViewModel {

    private final QuizRepository repository;

    private final MutableLiveData<List<Quiz>> quizzes =
            new MutableLiveData<>(new ArrayList<>());

    private final MutableLiveData<Quiz> selectedQuiz =
            new MutableLiveData<>(null);

    private final MutableLiveData<SubmitQuizResponse> quizResult =
            new MutableLiveData<>(null);

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

    public QuizViewModel(@NonNull Application application) {
        super(application);
        repository = new QuizRepository(application);
    }

    public LiveData<List<Quiz>> getQuizzes() {
        return quizzes;
    }

    public LiveData<Quiz> getSelectedQuiz() {
        return selectedQuiz;
    }

    public LiveData<SubmitQuizResponse> getQuizResult() {
        return quizResult;
    }

    public LiveData<Boolean> getLoading() {
        return loading;
    }

    public LiveData<Boolean> getOperationLoading() {
        return operationLoading;
    }

    public LiveData<String> getError() {
        return error;
    }

    public LiveData<String> getOperationError() {
        return operationError;
    }

    public LiveData<String> getOperationSuccess() {
        return operationSuccess;
    }

    public void loadLessonQuizzes(
            String containerId,
            String lessonId
    ) {
        loading.setValue(true);
        error.setValue(null);

        repository.getLessonQuizzes(
                containerId,
                lessonId,
                new ApiCallback<QuizListResponse>() {

                    @Override
                    public void onSuccess(QuizListResponse result) {
                        List<Quiz> resultQuizzes = result.getQuizzes();

                        quizzes.postValue(
                                resultQuizzes != null
                                        ? resultQuizzes
                                        : new ArrayList<>()
                        );

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

    public void loadQuiz(
            String containerId,
            String lessonId,
            String quizId
    ) {
        loading.setValue(true);
        error.setValue(null);

        repository.getQuiz(
                containerId,
                lessonId,
                quizId,
                new ApiCallback<QuizDetailsResponse>() {

                    @Override
                    public void onSuccess(QuizDetailsResponse result) {
                        selectedQuiz.postValue(result.getQuiz());
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

    public void createQuiz(
            String containerId,
            String lessonId,
            CreateQuizRequest request
    ) {
        operationLoading.setValue(true);
        operationError.setValue(null);
        operationSuccess.setValue(null);

        repository.createQuiz(
                containerId,
                lessonId,
                request,
                new ApiCallback<CreateQuizResponse>() {

                    @Override
                    public void onSuccess(CreateQuizResponse result) {
                        operationLoading.postValue(false);

                        operationSuccess.postValue(
                                "Quiz created successfully."
                        );

                        loadLessonQuizzes(containerId, lessonId);
                    }

                    @Override
                    public void onError(String message) {
                        operationLoading.postValue(false);
                        operationError.postValue(message);
                    }
                }
        );
    }

    public void submitQuiz(
            String containerId,
            String lessonId,
            String quizId,
            SubmitQuizRequest request
    ) {
        operationLoading.setValue(true);
        operationError.setValue(null);
        operationSuccess.setValue(null);
        quizResult.setValue(null);

        repository.submitQuiz(
                containerId,
                lessonId,
                quizId,
                request,
                new ApiCallback<SubmitQuizResponse>() {

                    @Override
                    public void onSuccess(SubmitQuizResponse result) {
                        quizResult.postValue(result);
                        operationLoading.postValue(false);

                        operationSuccess.postValue(
                                "Quiz submitted successfully."
                        );
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

    public void clearQuizResult() {
        quizResult.setValue(null);
    }
}