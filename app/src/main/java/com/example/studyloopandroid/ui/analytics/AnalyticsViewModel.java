package com.example.studyloopandroid.ui.analytics;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.studyloopandroid.data.model.analytics.AnalyticsOverviewResponse;
import com.example.studyloopandroid.data.repository.AnalyticsRepository;
import com.example.studyloopandroid.data.repository.ApiErrorMapper;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AnalyticsViewModel extends AndroidViewModel {

    private final AnalyticsRepository repository;

    private final MutableLiveData<AnalyticsOverviewResponse> analyticsData =
            new MutableLiveData<>();

    private final MutableLiveData<Boolean> isLoading =
            new MutableLiveData<>(false);

    private final MutableLiveData<String> errorMessage =
            new MutableLiveData<>();

    public AnalyticsViewModel(@NonNull Application application) {
        super(application);
        repository = new AnalyticsRepository(application);
    }

    public LiveData<AnalyticsOverviewResponse> getAnalyticsData() {
        return analyticsData;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public void loadAnalytics() {
        isLoading.setValue(true);
        errorMessage.setValue(null);

        repository.getOverview().enqueue(
                new Callback<AnalyticsOverviewResponse>() {

                    @Override
                    public void onResponse(
                            @NonNull Call<AnalyticsOverviewResponse> call,
                            @NonNull Response<AnalyticsOverviewResponse> response) {

                        isLoading.setValue(false);

                        if (response.isSuccessful() && response.body() != null) {
                            analyticsData.setValue(response.body());
                        } else {
                            errorMessage.setValue(
                                    ApiErrorMapper.fromResponse(
                                            response,
                                            "Failed to load analytics."
                                    )
                            );
                        }
                    }

                    @Override
                    public void onFailure(
                            @NonNull Call<AnalyticsOverviewResponse> call,
                            @NonNull Throwable t) {

                        isLoading.setValue(false);
                        errorMessage.setValue(
                                ApiErrorMapper.fromThrowable(t)
                        );
                    }
                }
        );
    }

}
