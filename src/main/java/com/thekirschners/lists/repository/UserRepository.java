package com.thekirschners.lists.repository;

import com.thekirschners.lists.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByLoginName(String loginName);
    boolean existsByLoginName(String loginName);
}
