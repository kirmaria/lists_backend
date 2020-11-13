package com.thekirschners.lists.repository;

import com.thekirschners.lists.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findBySubject(String subject);
    boolean existsBySubject(String subject);

    List<User> findAllBySubjectIn(List<String> subjects);

    @Query("select user.subject from User user where user.nickName like %:nicknamePattern%")
    List<String> findAllSubjectsByNicknameLike(@Param("nicknamePattern")String nicknamePattern);

    @Query("select user.nickName from User user where user.subject in :subjects")
    List<String> findAllNicknamesBySubjectIn(@Param("subjects")List<String> subjects);

    Optional<User> findByNickName(String nickName);
    boolean existsByNickName(String nickName);
}
