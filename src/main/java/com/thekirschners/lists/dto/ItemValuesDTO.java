package com.thekirschners.lists.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.thekirschners.lists.model.UnitType;

public class ItemValuesDTO {

    @JsonProperty("label")
    String label;

    @JsonProperty("description")
    String description;

    @JsonProperty("unit")
    UnitType unit;

    @JsonProperty("quantity")
    int qty;

    @JsonProperty("checked")
    boolean checked;

    public ItemValuesDTO() {
    }

    public ItemValuesDTO(String label, String description, UnitType unit, int qty, boolean checked) {
        this.label = label;
        this.description = description;
        this.unit = unit;
        this.qty = qty;
        this.checked = checked;
    }

    public String getLabel() {
        return label;
    }

    public ItemValuesDTO setLabel(String label) {
        this.label = label;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public ItemValuesDTO setDescription(String description) {
        this.description = description;
        return this;
    }

    public UnitType getUnit() {
        return unit;
    }

    public ItemValuesDTO setUnit(UnitType unit) {
        this.unit = unit;
        return this;
    }

    public int getQty() {
        return qty;
    }

    public ItemValuesDTO setQty(int qty) {
        this.qty = qty;
        return this;
    }

    public boolean isChecked() {
        return checked;
    }

    public ItemValuesDTO setChecked(boolean checked) {
        this.checked = checked;
        return this;
    }
}
