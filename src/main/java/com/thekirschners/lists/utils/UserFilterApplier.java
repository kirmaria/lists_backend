package com.thekirschners.lists.utils;

import com.thekirschners.lists.model.ItemsList;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.hibernate.Session;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;


@Aspect
@Component
@Profile("!test")
public class UserFilterApplier {
    @PersistenceContext
    private EntityManager entityManager;

    @Before("execution(public * com.thekirschners.lists.service.ItemsListService.*(..))")
    public void aroundUserServices() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && !"anonymousUser".equals(authentication.getPrincipal())) {
            org.hibernate.Filter filter = entityManager.unwrap(Session.class).enableFilter(ItemsList.FILTER_NAME_USER);
            Object principal = authentication.getPrincipal();
            filter.setParameter(ItemsList.FILTER_PARAM_NAME_USER, ((UserPrincipal) principal).getSubject());
            filter.validate();
        }
    }
}
