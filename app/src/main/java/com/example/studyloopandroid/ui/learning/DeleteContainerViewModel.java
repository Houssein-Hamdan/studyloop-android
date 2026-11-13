package com.example.studyloopandroid.ui.learning;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.studyloopandroid.data.repository.ApiCallback;
import com.example.studyloopandroid.data.repository.ContainerRepository;

public class DeleteContainerViewModel extends AndroidViewModel {

    private final ContainerRepository repository;

    private final MutableLiveData<Boolean> loading =
            new MutableLiveData<>(false);

    private final MutableLiveData<String> error =
            new MutableLiveData<>();

    private final MutableLiveData<Boolean> deleted =
            new MutableLiveData<>(false);

    public DeleteContainerViewModel(@NonNull Application application) {
        super(application);
        repository = new ContainerRepository(application);
    }

    public LiveData<Boolean> getLoading() {
        return loading;
    }

    public LiveData<String> getError() {
        return error;
    }

    public LiveData<Boolean> getDeleted() {
        return deleted;
    }

    public void clearDeleted() { deleted.setValue(false); }

    public void deleteContainer(String id) {

        if (Boolean.TRUE.equals(loading.getValue())) {
            return;
        }

        String cleanId = id == null ? "" : id.trim();

        if (cleanId.isEmpty()) {
            error.setValue("Invalid learning space ID.");
            return;
        }

        error.setValue(null);
        deleted.setValue(false);
        loading.setValue(true);

        repository.deleteContainer(
                cleanId,
                new ApiCallback<Boolean>() {

                    @Override
                    public void onSuccess(Boolean result) {
                        loading.postValue(false);
                        deleted.postValue(true);
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
