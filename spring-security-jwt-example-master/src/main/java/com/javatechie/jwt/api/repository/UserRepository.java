package com.javatechie.jwt.api.repository;

import com.javatechie.jwt.api.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Integer> {

    User findByUserName(String username);
}
