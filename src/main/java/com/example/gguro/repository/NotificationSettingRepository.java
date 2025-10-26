package com.example.gguro.repository;

import com.example.gguro.domain.NotificationSetting;
import com.example.gguro.domain.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NotificationSettingRepository extends JpaRepository<NotificationSetting, Long> {

    Optional<NotificationSetting> findByProfile(Profile profile);
}
