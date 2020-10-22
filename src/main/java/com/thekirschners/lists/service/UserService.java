package com.thekirschners.lists.service;

import com.thekirschners.lists.model.User;
import com.thekirschners.lists.dto.UserDTO;
import com.thekirschners.lists.dto.UserValuesDTO;
import com.thekirschners.lists.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
public class UserService {
    @Autowired
    UserRepository userRepository;


    public UserDTO createUserIfNotExist() {

        String loginName = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();

        return userRepository.findByLoginName(loginName)
                .orElseGet(() -> {return userRepository.save(new User().updateFromValuesDTO(new UserValuesDTO().setLoginName(loginName)));}).getDTO();

    }
}
