package com.thekirschners.lists.repository;

import com.thekirschners.lists.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findBySubject(String subject);
    boolean existsBySubject(String subject);

    Optional<User> findByNickName(String nickName);
    boolean existsByNickName(String nickName);
}
