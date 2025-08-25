package com.example.gguro.repository;

import com.example.gguro.domain.Profile;
import com.example.gguro.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, Long> {
    List<Profile> findAllByUserId(Long id);

    List<Profile> findByUser(User user);

    void deleteAllByUser(User user);
}