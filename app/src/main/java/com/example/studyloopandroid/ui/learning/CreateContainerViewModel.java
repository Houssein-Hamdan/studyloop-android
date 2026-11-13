package com.example.studyloopandroid.ui.learning;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.studyloopandroid.data.model.container.ContainerResponse;
import com.example.studyloopandroid.data.model.container.CreateContainerRequest;
import com.example.studyloopandroid.data.repository.ApiCallback;
import com.example.studyloopandroid.data.repository.ContainerRepository;

public class CreateContainerViewModel extends AndroidViewModel {

    private final ContainerRepository containerRepository;

    private final MutableLiveData<Boolean> loading =
            new MutableLiveData<>(false);

    private final MutableLiveData<String> error =
            new MutableLiveData<>();

    private final MutableLiveData<ContainerResponse> createdContainer =
            new MutableLiveData<>();

    public CreateContainerViewModel(@NonNull Application application) {
        super(application);
        containerRepository = new ContainerRepository(application);
    }

    public LiveData<Boolean> getLoading() {
        return loading;
    }

    public LiveData<String> getError() {
        return error;
    }

    public LiveData<ContainerResponse> getCreatedContainer() {
        return createdContainer;
    }

    public void clearCreatedContainer() {
        createdContainer.setValue(null);
    }

    public void createContainer(String name, String description) {

        if (Boolean.TRUE.equals(loading.getValue())) {
            return;
        }

        String cleanName = name == null ? "" : name.trim();

        String cleanDescription =
                description == null ? "" : description.trim();

        if (cleanName.isEmpty()) {
            error.setValue("Please enter a learning space name.");
            return;
        }

        error.setValue(null);
        createdContainer.setValue(null);
        loading.setValue(true);

        CreateContainerRequest request =
                new CreateContainerRequest(
                        cleanName,
                        cleanDescription
                );

        containerRepository.createContainer(
                request,
                new ApiCallback<ContainerResponse>() {

                    @Override
                    public void onSuccess(ContainerResponse container) {
                        loading.postValue(false);
                        createdContainer.postValue(container);
                    }

                    @Override
                    public void onError(String message) {
                        loading.postValue(false);
                        error.postValue(message);
                    }
                }
        );
    }
}
