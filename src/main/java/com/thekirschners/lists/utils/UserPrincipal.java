package com.thekirschners.lists.utils;

import java.security.Principal;

public class UserPrincipal implements Principal {

    public static final String ANONYMOUS_USER = "anonymousUser";

    private final String subject;
    private final String nickName;
    private final String email;

    public UserPrincipal(String subject, String nickName, String email) {
        this.subject = subject;
        this.nickName = nickName;
        this.email = email;
    }

    public String getSubject() {
        return subject;
    }

    public String getNickName() {
        return nickName;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public String getName() {
        return "UserPrincipal";
    }
}
