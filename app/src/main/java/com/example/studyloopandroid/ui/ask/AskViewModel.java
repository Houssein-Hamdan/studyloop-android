package com.example.studyloopandroid.ui.ask;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.studyloopandroid.data.repository.AskRepository;

public class AskViewModel extends AndroidViewModel {

    private final AskRepository repository;

    private final MutableLiveData<Boolean> loading =
            new MutableLiveData<>(false);

    private final MutableLiveData<String> answer =
            new MutableLiveData<>();

    private final MutableLiveData<String> error =
            new MutableLiveData<>();

    public AskViewModel(@NonNull Application application) {
        super(application);

        repository = new AskRepository(application);
    }

    public LiveData<Boolean> getLoading() {
        return loading;
    }

    public LiveData<String> getAnswer() {
        return answer;
    }

    public LiveData<String> getError() {
        return error;
    }

    public void askQuestion(String questionText) {
        if (questionText == null || questionText.trim().isEmpty()) {
            error.setValue("Please enter a question.");
            return;
        }

        loading.setValue(true);
        answer.setValue(null);
        error.setValue(null);

        repository.askQuestion(
                questionText.trim(),
                new AskRepository.AskCallback() {

                    @Override
                    public void onSuccess(String result) {
                        loading.setValue(false);
                        answer.setValue(result);
                    }

                    @Override
                    public void onError(String message) {
                        loading.setValue(false);
                        error.setValue(message);
                    }
                }
        );
    }
}