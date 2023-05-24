package com.dz.app.wordez.dao.repository;

import com.dz.app.wordez.dao.entity.UserQueryRecord;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserQueryRecordRepository extends R2dbcRepository<UserQueryRecord, Long> {
}
