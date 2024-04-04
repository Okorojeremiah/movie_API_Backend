package org.africa.movieflix.auth.repositories;

import org.africa.movieflix.auth.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByEmail(String username);
}