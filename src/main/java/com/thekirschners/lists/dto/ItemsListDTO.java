package com.thekirschners.lists.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ItemsListDTO extends IdentifierBaseDTO<UserDTO> {
    @JsonProperty("value")
    private ItemsListValuesDTO value;

    private List<ItemDTO> items;

    public ItemsListDTO() {
    }

    public ItemsListValuesDTO getValue() {
        return value;
    }

    public ItemsListDTO setValue(ItemsListValuesDTO value) {
        this.value = value;
        return this;
    }

    public List<ItemDTO> getItems() {
        if (items == null)
            items = new ArrayList<>();
        return items;
    }

    public ItemsListDTO setItems(List<ItemDTO> items) {
        this.items = items;
        return this;
    }

    public Optional<ItemDTO> findItemDTOById(String itemId) {
        return items.stream().filter(item -> item.id.equals(itemId)).findFirst();
    }

}
