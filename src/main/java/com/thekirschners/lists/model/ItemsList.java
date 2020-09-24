package com.thekirschners.lists.model;

import com.thekirschners.lists.dto.ItemDTO;
import com.thekirschners.lists.dto.ItemsListDTO;
import com.thekirschners.lists.dto.ItemsListValuesDTO;
import jdk.jfr.Timestamp;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "list")
public class ItemsList extends IdentifierBase {
    @NotNull
    @Column(name = "name", nullable = false, length = 64)
    String name;

    @Column(name = "description", length = 256)
    String description;

    @NotNull
    @Column(name = "type")
    @Enumerated(value = EnumType.STRING)
    ListType type;

    @NotNull
    @Column(name = "creator_id", updatable = false, nullable = false, length = 64)
    String creatorId;

    @NotNull
    @Column(name = "creation_timestamp")
    long creationTimestamp;

    @NotNull
    @Column(name = "last_updater_id", length = 64)
    String lastUpdaterId;

    @NotNull
    @Column(name = "update_timestamp")
    long updateTimestamp;

    @OneToMany(targetEntity = Item.class, mappedBy = "list", fetch = FetchType.EAGER)
    List<Item> items;

    public ItemsList() {
        items = new ArrayList<>();
    }

    public ItemsList(@NotNull String name, String description, @NotNull ListType type, @NotNull String creatorId) {
        this();
        this.name = name;
        this.description = description;
        this.type = type;
        this.creatorId = creatorId;
        this.creationTimestamp = Instant.now().toEpochMilli();
        this.lastUpdaterId = creatorId;
        this.updateTimestamp = Instant.now().toEpochMilli();
    }

    public String getName() {
        return name;
    }

    public ItemsList setName(String name) {
        this.name = name;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public ItemsList setDescription(String description) {
        this.description = description;
        return this;
    }

    public ListType getType() {
        return type;
    }

    public ItemsList setType(ListType type) {
        this.type = type;
        return this;
    }

    public String getCreatorId() {
        return creatorId;
    }

    public ItemsList setCreatorId(String creatorId) {
        this.creatorId = creatorId;
        return this;
    }

    public long getCreationTimestamp() {
        return creationTimestamp;
    }

    public ItemsList setCreationTimestamp(long creationTimestamp) {
        this.creationTimestamp = creationTimestamp;
        return this;
    }

    public String getLastUpdaterId() {
        return lastUpdaterId;
    }

    public ItemsList setLastUpdaterId(String lastUpdaterId) {
        this.lastUpdaterId = lastUpdaterId;
        return this;
    }

    public long getUpdateTimestamp() {
        return updateTimestamp;
    }

    public ItemsList setUpdateTimestamp(long updateTimestamp) {
        this.updateTimestamp = updateTimestamp;
        return this;
    }

    public List<Item> getItems() {
        return items;
    }

    public ItemsList setItems(List<Item> newItems) {
        items = newItems;
        return this;
    }

    /* DTO */
    public ItemsList updateFromValuesDTO(ItemsListValuesDTO value) {
        this.setName(value.getName());
        this.setDescription(value.getDescription());
        this.setType(value.getType());
        this.setCreatorId("TITI");
        this.setCreationTimestamp(Instant.now().toEpochMilli());
        this.setLastUpdaterId("TITI");
        this.setUpdateTimestamp(Instant.now().toEpochMilli());
        return this;
    }


    public ItemsListValuesDTO getValuesDTO() {
        ItemsListValuesDTO value = new ItemsListValuesDTO();
        value.setName(name);
        value.setDescription(description);
        value.setType(type);
        return value;
    }

    public ItemsListDTO getDTO() {
        ItemsListDTO dto = new ItemsListDTO();

        dto.setId(id);
        dto.setValue(getValuesDTO());

        dto.getItems().clear();
        for (Item item : items) {
            dto.getItems().add(item.getDTO());
        }

        return dto;
    }

}
