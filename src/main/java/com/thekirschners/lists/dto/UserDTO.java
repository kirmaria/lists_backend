package com.thekirschners.lists.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UserDTO extends IdentifierBaseDTO<UserDTO> {
    @JsonProperty("value")
    UserValuesDTO value;

    public UserDTO() {
    }

    public UserValuesDTO getValue() {
        return value;
    }

    public UserDTO setValue(UserValuesDTO value) {
        this.value = value;
        return this;
    }
}
