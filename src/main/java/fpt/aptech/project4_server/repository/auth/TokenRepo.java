package fpt.aptech.project4_server.repository.auth;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

import fpt.aptech.project4_server.entities.auth.Token;

@Repository
public interface TokenRepo extends JpaRepository<Token, Integer> {
    Optional<Token> findByRefreshToken(String token);
}
