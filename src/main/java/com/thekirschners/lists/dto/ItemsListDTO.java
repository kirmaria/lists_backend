package com.thekirschners.lists.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ItemsListDTO extends IdentifierBaseDTO<UserDTO> {
    @JsonProperty("value")
    private ItemsListValuesDTO value;

    @JsonProperty("items")
    private List<ItemDTO> items;

    // list of all the nickname's user invites
    @JsonProperty("invites")
    private List<String> invites;


    public ItemsListDTO() {
    }


    /* valuesDTO */
    public ItemsListValuesDTO getValue() {
        return value;
    }

    public ItemsListDTO setValue(ItemsListValuesDTO value) {
        this.value = value;
        return this;
    }


    /* items */
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


    /* invites */
    public List<String> getInvites() {
        if (invites == null)
            invites = new ArrayList<>();
        return invites;
    }

    public ItemsListDTO setInvites(List<String> invites) {
        this.invites = invites;
        return this;
    }

    public boolean hasInvite(String nickName) {
        return invites.contains(nickName);
    }

}
