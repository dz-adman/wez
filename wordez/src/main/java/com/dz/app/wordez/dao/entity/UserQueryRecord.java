package com.dz.app.wordez.dao.entity;

import com.dz.app.wordez.dao.stub.Result;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class UserQueryRecord {
    @Id
    @Setter(AccessLevel.NONE)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String url;

    @Column(nullable = false)
    private boolean valid;

    @Enumerated(EnumType.STRING)
    private Result result;

    @Column(nullable = false)
    private long timeTakenInMs;

    private String instant;

    private long userId;
}
