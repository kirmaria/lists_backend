package com.thekirschners.lists.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.thekirschners.lists.model.ListType;

public class ItemsListValuesDTO {

    @JsonProperty("name")
    String name;

    @JsonProperty("description")
    String description;

    @JsonProperty("type")
    ListType type;


    public ItemsListValuesDTO() {
    }

    public ItemsListValuesDTO(String name, String description, ListType type) {
        this.name = name;
        this.description = description;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public ItemsListValuesDTO setName(String name) {
        this.name = name;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public ItemsListValuesDTO setDescription(String description) {
        this.description = description;
        return this;
    }

    public ListType getType() {
        return type;
    }

    public ItemsListValuesDTO setType(ListType type) {
        this.type = type;
        return this;
    }

}
