package com.thekirschners.lists.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UserValuesDTO {
    @JsonProperty("login_name")
    String subject;

    @JsonProperty("nick_name")
    String nickName;

    @JsonProperty("email")
    String email;

    @JsonProperty("language")
    String preferredLanguage;

    @JsonProperty("enabled")
    boolean enabled;

    public UserValuesDTO() {
        this.subject = "";
        this.nickName = "";
        this.email = "";
        this.preferredLanguage = "EN";
        this.enabled = true;
    }

    public UserValuesDTO(String subject, String nickName, String email, String preferredLanguage, boolean enabled) {
        this.subject = subject;
        this.nickName = nickName;
        this.email = email;
        this.preferredLanguage = preferredLanguage;
        this.enabled = enabled;
    }

    public String getSubject() {
        return subject;
    }

    public UserValuesDTO setSubject(String subject) {
        this.subject = subject;
        return this;
    }

    public String getNickName() {
        return nickName;
    }

    public UserValuesDTO setNickName(String nickName) {
        this.nickName = nickName;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public UserValuesDTO setEmail(String email) {
        this.email = email;
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
