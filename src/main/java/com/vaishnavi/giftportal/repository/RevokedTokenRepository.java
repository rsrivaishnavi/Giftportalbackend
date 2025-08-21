package com.vaishnavi.giftportal.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.vaishnavi.giftportal.entity.RevokedToken;

@Repository
public interface RevokedTokenRepository extends CrudRepository<RevokedToken, String> {
    Optional<RevokedToken> findByToken(String token);
}