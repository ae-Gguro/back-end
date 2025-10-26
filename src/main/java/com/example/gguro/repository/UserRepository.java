package com.example.gguro.repository;

import com.example.gguro.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByUsername(String username);
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    Optional<User> findByOauthId(String oauthId);

    @Query("select distinct u from User u " +
            "left join fetch u.profileList p " +
            "left join fetch p.notificationSetting ns")
    List<User> findAllWithProfilesAndSettings();
}