package com.thekirschners.lists.dto;

public class ApiErrorDTO {
    String text;

    public ApiErrorDTO() {
    }

    public ApiErrorDTO(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
