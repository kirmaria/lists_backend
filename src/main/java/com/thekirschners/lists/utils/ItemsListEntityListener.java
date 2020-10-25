package com.thekirschners.lists.utils;

import com.thekirschners.lists.model.ItemsList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.persistence.PrePersist;

public class ItemsListEntityListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(ItemsListEntityListener.class);

    @PrePersist
    public void setOwner(ItemsList itemsList) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof UserPrincipal) {
                UserPrincipal userPrincipal = (UserPrincipal) principal;
                itemsList.setOwner(userPrincipal.getSubject());
            } else {
                LOGGER.warn("setOwner / wrong kind of principal {}", principal.getClass().getName());
            }
        } else {
            LOGGER.warn("setOwner / unable to set owner on {}, there's no authentication", itemsList);
        }
    }
}
