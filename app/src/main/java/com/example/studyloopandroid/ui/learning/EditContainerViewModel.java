package com.example.studyloopandroid.ui.learning;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.studyloopandroid.data.model.container.CreateContainerRequest;
import com.example.studyloopandroid.data.repository.ApiCallback;
import com.example.studyloopandroid.data.repository.ContainerRepository;

public class EditContainerViewModel extends AndroidViewModel {

    private final ContainerRepository repository;

    private final MutableLiveData<Boolean> loading =
            new MutableLiveData<>(false);

    private final MutableLiveData<String> error =
            new MutableLiveData<>();

    private final MutableLiveData<Boolean> updated =
            new MutableLiveData<>(false);

    public EditContainerViewModel(@NonNull Application application) {
        super(application);
        repository = new ContainerRepository(application);
    }

    public LiveData<Boolean> getLoading() {
        return loading;
    }

    public LiveData<String> getError() {
        return error;
    }

    public LiveData<Boolean> getUpdated() {
        return updated;
    }

    public void clearUpdated() { updated.setValue(false); }

    public void updateContainer(
            String id,
            String name,
            String description
    ) {
        if (Boolean.TRUE.equals(loading.getValue())) {
            return;
        }

        String cleanId = id == null ? "" : id.trim();
        String cleanName = name == null ? "" : name.trim();
        String cleanDescription =
                description == null ? "" : description.trim();

        if (cleanId.isEmpty()) {
            error.setValue("Invalid learning space ID.");
            return;
        }

        if (cleanName.isEmpty()) {
            error.setValue("Please enter a learning space name.");
            return;
        }

        error.setValue(null);
        updated.setValue(false);
        loading.setValue(true);

        CreateContainerRequest request =
                new CreateContainerRequest(
                        cleanName,
                        cleanDescription
                );

        repository.updateContainer(
                cleanId,
                request,
                new ApiCallback<Boolean>() {

                    @Override
                    public void onSuccess(Boolean result) {
                        loading.postValue(false);
                        updated.postValue(true);
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
