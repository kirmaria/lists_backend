package com.thekirschners.lists.service;

import com.thekirschners.lists.model.User;
import com.thekirschners.lists.dto.UserDTO;
import com.thekirschners.lists.dto.UserValuesDTO;
import com.thekirschners.lists.repository.UserRepository;
import com.thekirschners.lists.utils.UserPrincipal;
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
        UserPrincipal userPrincipal = UserPrincipal.class.cast(SecurityContextHolder.getContext().getAuthentication().getPrincipal());

        UserValuesDTO userValuesDTO = new UserValuesDTO()
                .setSubject(userPrincipal.getSubject())
                .setNickName(userPrincipal.getNickName())
                .setEmail(userPrincipal.getEmail());

        return userRepository.findBySubject(userPrincipal.getSubject())
                .orElseGet(() -> {return userRepository.save(new User().updateFromValuesDTO(userValuesDTO));}).getDTO();

    }
}
