package com.thekirschners.lists.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.thekirschners.lists.model.IdentifierBase;

public class IdentifierBaseDTO<T extends IdentifierBaseDTO<T>> {
    @JsonProperty("id")
    String id;


    public IdentifierBaseDTO() {
    }

    public String getId() {
        return id;
    }

    public T setId(String id) {
        this.id = id;
        return (T)this;
    }

}
