package com.example.gguro.domain;

import com.example.gguro.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Uuid extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true) // uuid 안겹치도록 유니크 설정
    private String uuid;

    public static Uuid create() {
        return new Uuid(null, UUID.randomUUID().toString());
    }

}
