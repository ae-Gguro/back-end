package com.example.gguro.repository;

import com.example.gguro.domain.Device;
import com.example.gguro.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeviceRepository extends JpaRepository<Device, Long> {
    Optional<Device> findByUserAndToken(User user, String token);

    List<Device> findAllByUser(User member);
}
