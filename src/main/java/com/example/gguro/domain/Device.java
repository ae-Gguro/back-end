package com.example.gguro.domain;

import com.example.gguro.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Device extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 255)
    private String token;

    @Column(name = "device_type", nullable = false, length = 20)
    @Builder.Default
    private String deviceType = "iOS";

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private boolean isActive = true;

    public void updateActive(boolean isActive) {
        this.isActive = isActive;
    }

}