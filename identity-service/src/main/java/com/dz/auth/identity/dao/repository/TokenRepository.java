package com.dz.auth.identity.dao.repository;

import com.dz.auth.identity.dao.entity.RefreshToken;
import com.dz.auth.identity.dao.entity.Token;
import com.dz.auth.identity.dao.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {
    List<Token> findAllByUser(User user);
    List<Token> findAllByUserAndRefreshToken(User user, RefreshToken refreshToken);
    Optional<Token> findByToken(String token);
}
