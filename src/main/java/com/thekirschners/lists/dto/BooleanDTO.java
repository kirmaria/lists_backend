package com.thekirschners.lists.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BooleanDTO {
    @JsonProperty("value")
    boolean value;

    public BooleanDTO() {
    }

    public BooleanDTO(boolean value) {
        this.value = value;
    }

    public boolean getValue() {
        return value;
    }

    public void setValue(boolean value) {
        this.value = value;
    }
}
