package com.example.demo.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entiteis.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByAccessToken(String accessToken);

    Optional<User> findByAppToken(String appToken);

}
