package com.thekirschners.lists.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ItemDTO extends IdentifierBaseDTO<ItemDTO> {
    @JsonProperty("value")
    ItemValuesDTO value;

    public ItemDTO() {
    }

    public ItemValuesDTO getValue() {
        return value;
    }

    public ItemDTO setValue(ItemValuesDTO value) {
        this.value = value;
        return this;
    }
}