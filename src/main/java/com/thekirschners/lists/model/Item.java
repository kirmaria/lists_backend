package com.thekirschners.lists.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name="item")
public class Item extends IdentifierBase  {
    @NotNull
    @Column(name="label", length = 64, nullable = false)
    String label;


    @Column(name="description", length = 256)
    String description;

    @NotNull
    @Column(name="unit", length =16, nullable = false)
    @Enumerated(value = EnumType.STRING)
    UnitType unit;


    @Column(name="quantity", nullable = false)
    int qty;

    @Column(name="checked", nullable = false)
    boolean checked;

    @NotNull
    @ManyToOne(targetEntity = ItemsList.class, fetch = FetchType.EAGER)
    @JoinColumn(name = "list_id", nullable = false)
    ItemsList list;

    @Column(name="list_id", insertable = false, updatable = false)
    String listId;


    public Item() {
    }

    public Item(ItemsList list, @NotNull String label, String description, @NotNull UnitType unit, int qty, boolean checked ) {
        this.list = list;
        this.listId = list.getId();
        this.label = label;
        this.description = description;
        this.unit = unit;
        this.qty = qty;
        this.checked = checked;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public UnitType getUnit() {
        return unit;
    }

    public void setUnit(UnitType unit) {
        this.unit = unit;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public ItemsList getList() {
        return list;
    }

    public Item setList(ItemsList list) {
        this.list = list;
        this.listId = list.getId();
        return this;
    }

}
