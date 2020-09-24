package com.thekirschners.lists.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class StringDTO {
    @JsonProperty("value")
    String value;

    public StringDTO() {
    }

    public StringDTO(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
