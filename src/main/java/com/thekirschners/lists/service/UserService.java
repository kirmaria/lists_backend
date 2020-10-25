package com.thekirschners.lists.service;

import com.thekirschners.lists.model.User;
import com.thekirschners.lists.dto.UserDTO;
import com.thekirschners.lists.dto.UserValuesDTO;
import com.thekirschners.lists.repository.UserRepository;
import com.thekirschners.lists.utils.UserPrincipal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@Transactional
public class UserService {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);

    @Autowired
    UserRepository userRepository;


    public void createUserIfNotExist() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (principal instanceof UserPrincipal) {
                UserPrincipal userPrincipal = (UserPrincipal) principal;

                UserValuesDTO userValuesDTO = new UserValuesDTO()
                        .setSubject(userPrincipal.getSubject())
                        .setNickName(userPrincipal.getNickName())
                        .setEmail(userPrincipal.getEmail());

                Optional<User> optionalUser = userRepository.findBySubject(userPrincipal.getSubject());
                optionalUser.ifPresentOrElse((user) -> {
                    if (LOGGER.isDebugEnabled())
                        LOGGER.debug("createUserIfNotExist / user found : {}", user.getId());
                }, () -> {
                    User savedUser = userRepository.save(new User().updateFromValuesDTO(userValuesDTO));
                    if (LOGGER.isDebugEnabled())
                        LOGGER.debug("createUserIfNotExist / new user created : {}", savedUser.getId());

                });

            } else {
                LOGGER.warn("createUserIfNotExist / wrong kind of principal: {}", principal.getClass().getName());
            }
        } else {
            LOGGER.warn("createUserIfNotExist / there's no authentication");
        }
    }


    public UserDTO getUser(String subject) {
        return userRepository.findBySubject(subject)
                .map(user -> {
                    return user.getDTO();
                })
                .orElseThrow(() -> new NoSuchElementException("ERROR getUser: user <" + subject + "> does not exist !"));
    }
}
