package com.thekirschners.lists.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EmptyDTO {
    @JsonProperty("nothing")
    String id = "nothing";

    public EmptyDTO() {
    }
}
