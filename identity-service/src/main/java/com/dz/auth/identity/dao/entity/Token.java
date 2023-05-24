package com.dz.auth.identity.dao.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Token {

    @Id
    @GeneratedValue
    private Long id;

    @Column(unique = true)
    private String token;

    @Column(nullable = false)
    private Instant validFrom;

    @Column(nullable = false)
    private Instant validTill;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "refresh_token_id")
    private RefreshToken refreshToken;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}
