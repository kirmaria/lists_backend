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
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

@Service
@Transactional
public class UserService {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);

    @Autowired
    UserRepository userRepository;

    public void createUserIfNotExist() {

        doWithPrincipal(userPrincipal -> {
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
                return "";
        });
    }


    public UserDTO getUserBySubject(String subject) {
        return userRepository.findBySubject(subject)
                .map(user -> {
                    return user.getDTO();
                })
                .orElseThrow(() -> new NoSuchElementException("Unable to find user for subject <" + subject + "> "));
    }

    public UserDTO getUserByNickName(String nickName) {
        return userRepository.findByNickName(nickName)
                .map(user -> user.getDTO())
                .orElseThrow(() -> new NoSuchElementException("Unable to find user for nickname <" + nickName + "> "));
    }

    public boolean isNickNameTaken(String nickName) {
        return userRepository.existsByNickName(nickName);
    }


    public UserDTO updateUserNickName(String nickName){
        return doWithPrincipal(userPrincipal -> {
            String subject = userPrincipal.getSubject();
            return userRepository.findBySubject(subject)
                    .map(user-> userRepository.save(user.setNickName(nickName)).getDTO())
                    .orElseThrow(() -> new NoSuchElementException("Unable to find user for subject <" + subject + ">"));
        });
    }

    public UserDTO updateUserEmail(String email){
        return doWithPrincipal(userPrincipal -> {
            String subject = userPrincipal.getSubject();
            return userRepository.findBySubject(subject)
                    .map(user-> userRepository.save(user.setEmail(email)).getDTO())
                    .orElseThrow(() -> new NoSuchElementException("Unable to find user for subject <" + subject + ">"));
        });
    }


    public String getNicknameForSubject(String subject) {
        return this.userRepository.findBySubject(subject)
                .map(user -> user.getNickName())
                .orElseThrow(() -> new NoSuchElementException("Unable to find user for subject <" + subject + "> "));
    }

    public List<String> getNicknamesForSubjects(List<String> subjectsList) {
        return this.userRepository.findAllNicknamesBySubjectIn(subjectsList);
    }


    /* PRIVATE */
    private <R> R doWithPrincipal(Function<UserPrincipal, R> consumer) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (principal instanceof UserPrincipal) {
                UserPrincipal userPrincipal = (UserPrincipal) principal;
                return consumer.apply(userPrincipal);
            } else {
                LOGGER.warn("Wrong kind of principal: {}", principal.getClass().getName());
            }
        } else {
            LOGGER.warn("There's no authentication");
        }
        return null;
    }


}
