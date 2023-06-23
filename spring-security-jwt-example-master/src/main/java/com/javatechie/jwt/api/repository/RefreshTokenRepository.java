package com.javatechie.jwt.api.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.javatechie.jwt.api.entity.RefreshToken;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken,Integer> {
    Optional<RefreshToken> findByToken(String token);
}


