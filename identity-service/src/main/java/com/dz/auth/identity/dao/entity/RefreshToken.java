package com.dz.auth.identity.dao.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class RefreshToken {
    @Id
    @GeneratedValue
    private Long id;

    @Column(unique = true)
    private String token;

    @Column(nullable = false)
    private Instant validFrom;

    @Column(nullable = false)
    private Instant validTill;

    @OneToMany(mappedBy = "refreshToken")
    private List<Token> tokens;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    public User user;
}
