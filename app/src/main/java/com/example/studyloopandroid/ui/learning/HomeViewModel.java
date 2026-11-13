package com.example.studyloopandroid.ui.learning;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.studyloopandroid.data.model.container.ContainerResponse;
import com.example.studyloopandroid.data.repository.ApiCallback;
import com.example.studyloopandroid.data.repository.ContainerRepository;

import java.util.ArrayList;
import java.util.List;

public class HomeViewModel extends AndroidViewModel {

    private final ContainerRepository repository;

    private final MutableLiveData<List<ContainerResponse>> containers =
            new MutableLiveData<>(new ArrayList<>());

    private final MutableLiveData<Boolean> loading =
            new MutableLiveData<>(false);

    private final MutableLiveData<String> error =
            new MutableLiveData<>(null);

    public HomeViewModel(@NonNull Application application) {
        super(application);

        repository = new ContainerRepository(application);
    }

    public LiveData<List<ContainerResponse>> getContainers() {
        return containers;
    }

    public LiveData<Boolean> getLoading() {
        return loading;
    }

    public LiveData<String> getError() {
        return error;
    }

    public void loadContainers() {

        loading.setValue(true);
        error.setValue(null);

        repository.getContainers(
                new ApiCallback<List<ContainerResponse>>() {

                    @Override
                    public void onSuccess(List<ContainerResponse> result) {

                        containers.postValue(result);
                        loading.postValue(false);

                        android.util.Log.d(
                                "HomeViewModel",
                                "Containers loaded: "
                                        + (result == null ? 0 : result.size())
                        );
                    }

                    @Override
                    public void onError(String message) {

                        error.postValue(message);
                        loading.postValue(false);

                        android.util.Log.e(
                                "HomeViewModel",
                                "Failed to load containers: " + message
                        );
                    }
                }
        );

    }


}
