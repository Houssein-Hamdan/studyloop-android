package com.example.studyloopandroid.data.remote;

import com.example.studyloopandroid.data.model.container.ContainersResponse;
import com.example.studyloopandroid.data.model.container.CreateContainerRequest;
import com.example.studyloopandroid.data.model.container.CreateContainerResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import okhttp3.ResponseBody;

import retrofit2.http.DELETE;
import retrofit2.http.PUT;
import retrofit2.http.Path;
public interface ContainersApi {

    @GET("containers")
    Call<ContainersResponse> getContainers();

    @POST("containers")
    Call<CreateContainerResponse> createContainer(
            @Body CreateContainerRequest request
    );

    @PUT("containers/{id}")
    Call<ResponseBody> updateContainer(
            @Path("id") String id,
            @Body CreateContainerRequest request
    );

    @DELETE("containers/{id}")
    Call<ResponseBody> deleteContainer(
            @Path("id") String id
    );
}
