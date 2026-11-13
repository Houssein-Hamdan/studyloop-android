package com.example.studyloopandroid.ui.ask;

public class AskMessage {

    public enum Type {
        USER,
        AI
    }

    private final String text;
    private final Type type;

    public AskMessage(String text, Type type) {
        this.text = text;
        this.type = type;
    }

    public String getText() {
        return text;
    }

    public Type getType() {
        return type;
    }
}