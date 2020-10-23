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
    String subject;

    @NotNull
    @Column(name = "nick_name")
    String nickName;

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

    public User(@NotNull String subject, @NotNull String nickName, @NotNull String email, @Length(min = 2, max = 3, message = "must be a 2 or 3 letter long ISO language code") String preferredLanguage, @NotNull boolean enabled) {
        this.subject = subject;
        this.nickName = nickName;
        this.email = email;
        this.preferredLanguage = preferredLanguage;
        this.enabled = enabled;
    }

    public String getSubject() {
        return subject;
    }

    public User setSubject(String subject) {
        this.subject = subject;
        return this;
    }

    public String getNickName() {
        return nickName;
    }

    public User setNickName(String nickName) {
        this.nickName = nickName;
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
        this.setSubject(value.getSubject());
        this.setNickName(value.getNickName());
        this.setEmail(value.getEmail());
        this.setPreferredLanguage(value.getPreferredLanguage());
        this.setEnabled(value.isEnabled());
        return this;
    }

    public User updateFromDTO(UserDTO dto) {
        this.setSubject(dto.getValue().getSubject());
        this.setNickName(dto.getValue().getNickName());
        this.setEmail(dto.getValue().getEmail());
        this.setPreferredLanguage(dto.getValue().getPreferredLanguage());
        this.setEnabled(dto.getValue().isEnabled());
        return this;
    }

    public UserValuesDTO getValuesDTO() {
        UserValuesDTO value = new UserValuesDTO();
        value.setSubject(this.subject);
        value.setNickName(this.nickName);
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
