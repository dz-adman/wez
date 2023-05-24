package com.dz.auth.identity.dao.repository;

import com.dz.auth.identity.dao.entity.LoginDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LoginDetailsRepository extends JpaRepository<LoginDetails, Long> {
    Optional<LoginDetails> findByLoginId(String loginId);
}
