package com.example.studyloopandroid.data.model.container;

public class CreateContainerRequest {

    private final String name;
    private final String description;

    public CreateContainerRequest(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

}
