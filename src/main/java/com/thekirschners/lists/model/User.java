package com.thekirschners.lists.model;


import com.thekirschners.lists.dto.UserDTO;
import com.thekirschners.lists.dto.UserValuesDTO;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.NotNull;


@Entity
@Table(name = "user")
public class User extends IdentifierBase {

    @NotNull
    @Column(name = "login_name")
    String loginName;

    @NotNull
    @Column(name = "email")
    String email;


    @Column(name = "language")
    @Length(min = 2, max = 3, message = "must be a 2 or 3 letter long ISO language code")
    String preferredLanguage;

    @NotNull
    @Column(name = "enabled")
    boolean enabled;


    public User() {
    }

    public User(@NotNull String loginName, @NotNull String email, @Length(min = 2, max = 3, message = "must be a 2 or 3 letter long ISO language code") String preferredLanguage, @NotNull boolean enabled) {
        this.loginName = loginName;
        this.email = email;
        this.preferredLanguage = preferredLanguage;
        this.enabled = enabled;
    }

    public String getLoginName() {
        return loginName;
    }

    public User setLoginName(String loginName) {
        this.loginName = loginName;
        return this;
    }


    public String getEmail() {
        return email;
    }

    public User setEmail(String email) {
        this.email = email;
        return this;
    }


    public String getPreferredLanguage() {
        return preferredLanguage;
    }

    public User setPreferredLanguage(String preferredLanguage) {
        this.preferredLanguage = preferredLanguage;
        return this;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public User setEnabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    /* DTO */
    public User updateFromValuesDTO(UserValuesDTO value) {
        this.setLoginName(value.getLoginName());
        this.setEmail(value.getEmail());
        this.setPreferredLanguage(value.getPreferredLanguage());
        this.setEnabled(value.isEnabled());
        return this;
    }

    public User updateFromDTO(UserDTO dto) {
        this.setLoginName(dto.getValue().getLoginName());
        this.setEmail(dto.getValue().getEmail());
        this.setPreferredLanguage(dto.getValue().getPreferredLanguage());
        this.setEnabled(dto.getValue().isEnabled());
        return this;
    }

    public UserValuesDTO getValuesDTO() {
        UserValuesDTO value = new UserValuesDTO();
        value.setLoginName(this.loginName);
        value.setEmail(this.email);
        value.setPreferredLanguage(this.preferredLanguage);
        value.setEnabled(this.enabled);
        return value;
    }

    public UserDTO getDTO() {
        UserDTO dto = new UserDTO();
        dto.setId(id);
        dto.setValue(getValuesDTO());
        return dto;
    }
}
