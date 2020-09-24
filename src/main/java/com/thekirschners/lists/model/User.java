package com.thekirschners.lists.model;

import com.thekirschners.lists.model.IdentifierBase;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Set;


@Entity
@Table(name = "user")
public class User extends IdentifierBase {

    @NotNull
    @Column(name = "login_name")
    String loginName;

    @NotNull
    @Column(name = "email")
    String email;

    @NotNull
    @Column(name = "phone")
    String phone;

    @Column(name = "language")
    @Length(min = 2, max = 3, message = "must be a 2 or 3 letter long ISO language code")
    String preferredLanguage;

    @NotNull
    @Column(name = "enabled")
    boolean enabled;


    public User() {
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

    public String getPhone() {
        return phone;
    }

    public User setPhone(String phone) {
        this.phone = phone;
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
}
