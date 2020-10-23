package com.thekirschners.lists.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ItemsListDTO extends IdentifierBaseDTO<UserDTO> {
    @JsonProperty("value")
    private ItemsListValuesDTO value;

    @JsonProperty("owner")
    private String owner;

    @JsonProperty("invites")
    private List<String> invites;

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


    public String getOwner() {
        return owner;
    }

    public ItemsListDTO setOwner(String owner) {
        this.owner = owner;
        return this;
    }


    public List<String> getInvites() {
        if (invites == null)
            invites = new ArrayList<>();
        return invites;
    }

    public ItemsListDTO setInvites(List<String> invites) {
        this.invites = invites;
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

    public boolean hasInvite(String nickName) {
        return invites.contains(nickName);
    }


}
