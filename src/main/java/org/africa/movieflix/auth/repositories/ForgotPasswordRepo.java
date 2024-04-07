package org.africa.movieflix.auth.repositories;

import org.africa.movieflix.auth.entities.ForgetPassword;
import org.africa.movieflix.auth.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ForgotPasswordRepo extends JpaRepository<ForgetPassword, Integer> {
    @Query("SELECT fp FROM ForgetPassword fp WHERE fp.otp = ?1 and fp.user = ?2")
    Optional<ForgetPassword> findByOtpAndUser(Integer otp, User user);
}
