package com.example.studyloopandroid.ui.summary;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.studyloopandroid.data.model.summary.CreateSummaryRequest;
import com.example.studyloopandroid.data.model.summary.CreateSummaryResponse;
import com.example.studyloopandroid.data.model.summary.Summary;
import com.example.studyloopandroid.data.model.summary.SummaryListResponse;
import com.example.studyloopandroid.data.repository.ApiCallback;
import com.example.studyloopandroid.data.repository.SummaryRepository;

import java.util.ArrayList;
import java.util.List;

public class SummaryViewModel extends AndroidViewModel {

    private final SummaryRepository repository;

    private final MutableLiveData<List<Summary>> summaries =
            new MutableLiveData<>(new ArrayList<>());

    private final MutableLiveData<Summary> selectedSummary =
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

    public SummaryViewModel(@NonNull Application application) {
        super(application);
        repository = new SummaryRepository(application);
    }

    public LiveData<List<Summary>> getSummaries() {
        return summaries;
    }

    public LiveData<Summary> getSelectedSummary() {
        return selectedSummary;
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

    public void loadSummaries(
            String containerId,
            String lessonId
    ) {
        loading.setValue(true);
        error.setValue(null);

        repository.getSummaries(
                containerId,
                lessonId,
                new ApiCallback<SummaryListResponse>() {

                    @Override
                    public void onSuccess(SummaryListResponse result) {

                        List<Summary> resultSummaries =
                                result.getSummaries();

                        summaries.postValue(
                                resultSummaries != null
                                        ? resultSummaries
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

    public void loadSummary(
            String containerId,
            String lessonId,
            String summaryId
    ) {
        loading.setValue(true);
        error.setValue(null);

        repository.getSummary(
                containerId,
                lessonId,
                summaryId,
                new ApiCallback<CreateSummaryResponse>() {

                    @Override
                    public void onSuccess(CreateSummaryResponse result) {
                        selectedSummary.postValue(result.getSummary());
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

    public void createSummary(
            String containerId,
            String lessonId,
            CreateSummaryRequest request
    ) {
        operationLoading.setValue(true);
        operationError.setValue(null);
        operationSuccess.setValue(null);

        repository.createSummary(
                containerId,
                lessonId,
                request,
                new ApiCallback<CreateSummaryResponse>() {

                    @Override
                    public void onSuccess(CreateSummaryResponse result) {
                        operationLoading.postValue(false);

                        operationSuccess.postValue(
                                "Summary generated successfully."
                        );

                        loadSummaries(containerId, lessonId);
                    }

                    @Override
                    public void onError(String message) {
                        operationLoading.postValue(false);
                        operationError.postValue(message);
                    }
                }
        );
    }

    public void deleteSummary(
            String containerId,
            String lessonId,
            String summaryId
    ) {
        operationLoading.setValue(true);
        operationError.setValue(null);
        operationSuccess.setValue(null);

        repository.deleteSummary(
                containerId,
                lessonId,
                summaryId,
                new ApiCallback<Void>() {

                    @Override
                    public void onSuccess(Void result) {
                        operationLoading.postValue(false);

                        operationSuccess.postValue(
                                "Summary deleted successfully."
                        );

                        loadSummaries(containerId, lessonId);
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

    public void clearSelectedSummary() {
        selectedSummary.setValue(null);
    }
}