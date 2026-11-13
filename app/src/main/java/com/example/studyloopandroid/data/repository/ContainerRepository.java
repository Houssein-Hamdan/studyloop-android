package com.example.studyloopandroid.data.repository;

import android.content.Context;
import android.util.Log;

import com.example.studyloopandroid.data.model.container.ContainerResponse;
import com.example.studyloopandroid.data.model.container.ContainersResponse;
import com.example.studyloopandroid.data.model.container.CreateContainerRequest;
import com.example.studyloopandroid.data.model.container.CreateContainerResponse;
import com.example.studyloopandroid.data.remote.ContainersApi;
import com.example.studyloopandroid.data.remote.RetrofitClient;
import okhttp3.ResponseBody;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ContainerRepository {

    private static final String TAG = "ContainersAPI";

    private final ContainersApi containersApi;

    public ContainerRepository(Context context) {
        containersApi = RetrofitClient
                .getInstance(context)
                .create(ContainersApi.class);
    }

    public void getContainers(
            ApiCallback<List<ContainerResponse>> callback
    ) {
        containersApi.getContainers().enqueue(
                new Callback<ContainersResponse>() {

                    @Override
                    public void onResponse(
                            Call<ContainersResponse> call,
                            Response<ContainersResponse> response
                    ) {
                        if (response.isSuccessful()
                                && response.body() != null) {

                            List<ContainerResponse> containers =
                                    response.body().getContainers();

                            if (containers == null) {
                                callback.onError(
                                        "Invalid response: containers list is missing."
                                );
                                return;
                            }

                            callback.onSuccess(containers);

                        } else {
                            Log.e(
                                    TAG,
                                    "HTTP " + response.code()
                                            + ", URL: "
                                            + call.request().url()
                            );

                            callback.onError(
                                    ApiErrorMapper.fromResponse(
                                            response,
                                            "Failed to load learning spaces."
                                    )
                            );
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<ContainersResponse> call,
                            Throwable t
                    ) {
                        Log.e(
                                TAG,
                                "Request failed: "
                                        + call.request().url(),
                                t
                        );

                        callback.onError(
                                ApiErrorMapper.fromThrowable(t)
                        );
                    }
                }
        );
    }

    public void createContainer(
            CreateContainerRequest request,
            ApiCallback<ContainerResponse> callback
    ) {
        containersApi.createContainer(request).enqueue(
                new Callback<CreateContainerResponse>() {

                    @Override
                    public void onResponse(
                            Call<CreateContainerResponse> call,
                            Response<CreateContainerResponse> response
                    ) {
                        if (response.isSuccessful()
                                && response.body() != null) {

                            ContainerResponse container =
                                    response.body().getContainer();

                            if (container == null) {
                                callback.onError(
                                        "Invalid response: created container is missing."
                                );
                                return;
                            }

                            callback.onSuccess(container);

                        } else {
                            Log.e(
                                    TAG,
                                    "Create container failed. HTTP "
                                            + response.code()
                                            + ", URL: "
                                            + call.request().url()
                            );

                            callback.onError(
                                    ApiErrorMapper.fromResponse(
                                            response,
                                            "Failed to create learning space."
                                    )
                            );
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<CreateContainerResponse> call,
                            Throwable t
                    ) {
                        Log.e(
                                TAG,
                                "Create container request failed: "
                                        + call.request().url(),
                                t
                        );

                        callback.onError(
                                ApiErrorMapper.fromThrowable(t)
                        );
                    }
                }
        );
    }

    public void updateContainer(
            String id,
            CreateContainerRequest request,
            ApiCallback<Boolean> callback
    ) {
        containersApi.updateContainer(id, request).enqueue(
                new Callback<ResponseBody>() {

                    @Override
                    public void onResponse(
                            Call<ResponseBody> call,
                            Response<ResponseBody> response
                    ) {
                        if (response.isSuccessful()) {
                            callback.onSuccess(true);

                        } else {
                            Log.e(TAG, "Update container failed. HTTP "
                                    + response.code()
                                    + ", URL: " + call.request().url());

                            callback.onError(
                                    ApiErrorMapper.fromResponse(
                                            response,
                                            "Failed to update learning space."
                                    )
                            );
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<ResponseBody> call,
                            Throwable t
                    ) {
                        Log.e(TAG, "Update container request failed: "
                                + call.request().url(), t);

                        callback.onError(
                                ApiErrorMapper.fromThrowable(t)
                        );
                    }
                }
        );
    }

    public void deleteContainer(
            String id,
            ApiCallback<Boolean> callback
    ) {
        containersApi.deleteContainer(id).enqueue(
                new Callback<ResponseBody>() {

                    @Override
                    public void onResponse(
                            Call<ResponseBody> call,
                            Response<ResponseBody> response
                    ) {
                        if (response.isSuccessful()) {
                            callback.onSuccess(true);

                        } else {
                            Log.e(TAG, "Delete container failed. HTTP "
                                    + response.code()
                                    + ", URL: " + call.request().url());

                            callback.onError(
                                    ApiErrorMapper.fromResponse(
                                            response,
                                            "Failed to delete learning space."
                                    )
                            );
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<ResponseBody> call,
                            Throwable t
                    ) {
                        Log.e(TAG, "Delete container request failed: "
                                + call.request().url(), t);

                        callback.onError(
                                ApiErrorMapper.fromThrowable(t)
                        );
                    }
                }
        );
    }


}
