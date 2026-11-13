package com.example.studyloopandroid.data.model.container;

import java.util.List;

public class ContainersResponse {

    private String message;
    private int count;
    private List<ContainerResponse> containers;

    public String getMessage() {
        return message;
    }

    public int getCount() {
        return count;
    }

    public List<ContainerResponse> getContainers() {
        return containers;
    }

}
