package com.thekirschners.lists.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UserValuesDTO {
    @JsonProperty("login_name")
    String loginName;

    @JsonProperty("email")
    String email;

    @JsonProperty("phone")
    String phone;

    @JsonProperty("language")
    String preferredLanguage;

    @JsonProperty("enabled")
    boolean enabled;

    public UserValuesDTO() {
    }

    public UserValuesDTO(String loginName, String email, String phone, String email1, String phone1, String preferredLanguage, boolean enabled) {
        this.loginName = loginName;
        this.email = email;
        this.phone = phone;
        this.email = email1;
        this.phone = phone1;
        this.preferredLanguage = preferredLanguage;
        this.enabled = enabled;
    }

    public String getLoginName() {
        return loginName;
    }

    public UserValuesDTO setLoginName(String loginName) {
        this.loginName = loginName;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public UserValuesDTO setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getPhone() {
        return phone;
    }

    public UserValuesDTO setPhone(String phone) {
        this.phone = phone;
        return this;
    }

    public String getPreferredLanguage() {
        return preferredLanguage;
    }

    public UserValuesDTO setPreferredLanguage(String preferredLanguage) {
        this.preferredLanguage = preferredLanguage;
        return this;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public UserValuesDTO setEnabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }
}