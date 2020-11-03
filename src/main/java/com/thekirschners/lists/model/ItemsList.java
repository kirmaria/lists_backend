package com.thekirschners.lists.model;

import com.thekirschners.lists.dto.ItemsListDTO;
import com.thekirschners.lists.dto.ItemsListValuesDTO;
import com.thekirschners.lists.utils.ItemsListEntityListener;
import com.thekirschners.lists.utils.StringListConverter;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "list")
@EntityListeners(ItemsListEntityListener.class)
@FilterDef(name = ItemsList.FILTER_NAME_USER, parameters = {
        @ParamDef(name = ItemsList.FILTER_PARAM_NAME_OWNER, type = "string"),
        @ParamDef(name = ItemsList.FILTER_PARAM_NAME_INVITES, type = "string")
})
@Filter(name = ItemsList.FILTER_NAME_USER, condition = ItemsList.USER_FILTER_CLAUSE)

public class ItemsList extends IdentifierBase {
    public static final String FILTER_NAME_USER = "USER_FILTER";
    public static final String FILTER_PARAM_NAME_OWNER = "OWNER_PARAM";
    public static final String FILTER_PARAM_NAME_INVITES= "INVITES_PARAM";
    public static final String USER_FILTER_CLAUSE = "(owner = :" + FILTER_PARAM_NAME_OWNER + " or invites like :" + FILTER_PARAM_NAME_INVITES + ")";

    @NotNull
    @Column(name = "name", nullable = false, length = 64, unique=true)
    String name;

    @Column(name = "description", length = 256)
    String description;

    @NotNull
    @Column(name = "type")
    @Enumerated(value = EnumType.STRING)
    ListType type;


    @NotNull
    @Column(name = "creation_timestamp")
    long creationTimestamp;


    @NotNull
    @Column(name = "update_timestamp")
    long updateTimestamp;

    @OneToMany(targetEntity = Item.class, mappedBy = "list", fetch = FetchType.EAGER)
    List<Item> items;


    @NotNull
    @Column(name = "owner", updatable = false, nullable = false, length = 128)
    String owner;

    @Convert(converter = StringListConverter.class)
    @Column(name = "invites")
    List<String> invites;


    public ItemsList() {
        this.name = "";
        this.description = "";
        this.type = ListType.CHECKING_LIST;
        this.creationTimestamp = Instant.now().toEpochMilli();
        this.updateTimestamp = Instant.now().toEpochMilli();
        this.items = new ArrayList<>();
        this.invites = new ArrayList<>();
    }

    public ItemsList(@NotNull String name, String description, @NotNull ListType type) {
        this.name = name;
        this.description = description;
        this.type = type;
        this.creationTimestamp = Instant.now().toEpochMilli();
        this.updateTimestamp = Instant.now().toEpochMilli();
    }

    public String getName() {
        return this.name;
    }

    public ItemsList setName(String name) {
        this.name = name;
        return this;
    }

    public String getDescription() {
        return this.description;
    }

    public ItemsList setDescription(String description) {
        this.description = description;
        return this;
    }

    public ListType getType() {
        return this.type;
    }

    public ItemsList setType(ListType type) {
        this.type = type;
        return this;
    }


    public long getCreationTimestamp() {
        return this.creationTimestamp;
    }

    public ItemsList setCreationTimestamp(long creationTimestamp) {
        this.creationTimestamp = creationTimestamp;
        return this;
    }


    public long getUpdateTimestamp() {
        return this.updateTimestamp;
    }

    public ItemsList setUpdateTimestamp(long updateTimestamp) {
        this.updateTimestamp = updateTimestamp;
        return this;
    }

    public List<Item> getItems() {
        return this.items;
    }

    public ItemsList setItems(List<Item> items) {
        this.items = items;
        return this;
    }

    public String getOwner() {
        return this.owner;
    }

    public ItemsList setOwner(String owner) {
        this.owner = owner;
        return this;
    }

    public List<String> getInvites() {
        return this.invites;
    }

    public ItemsList setInvites(List<String> invites) {
        this.invites = invites;
        return this;
    }

    public ItemsList addInvite(String invite) {
        if (this.invites.contains(invite))
            this.invites.add(invite);
        return this;
    }

    public ItemsList removeInvite(List<String> invite) {
        if (this.invites.contains(invite)) {
            this.invites.remove(invite);
        }
        return this;
    }


    /* DTO */
    public ItemsList updateFromValuesDTO(ItemsListValuesDTO value) {
        this.setName(value.getName());
        this.setDescription(value.getDescription());
        this.setType(value.getType());
        this.setCreationTimestamp(Instant.now().toEpochMilli());
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

        dto.setInvites(invites);

        dto.getItems().clear();
        for (Item item : items) {
            dto.getItems().add(item.getDTO());
        }

        return dto;
    }

}
