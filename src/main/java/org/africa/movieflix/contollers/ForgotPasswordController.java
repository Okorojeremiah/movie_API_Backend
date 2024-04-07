package org.africa.movieflix.contollers;

import org.africa.movieflix.auth.entities.ForgetPassword;
import org.africa.movieflix.auth.entities.User;
import org.africa.movieflix.auth.repositories.ForgotPasswordRepo;
import org.africa.movieflix.auth.repositories.UserRepository;
import org.africa.movieflix.dto.ChangePassword;
import org.africa.movieflix.dto.MailBody;
import org.africa.movieflix.services.EmailService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Date;
import java.util.Objects;
import java.util.Random;

@RestController
@RequestMapping("/forgotPassword")
public class ForgotPasswordController {

    private final ForgotPasswordRepo forgotPasswordRepo;
    private static final int OTP_EXPIRATION_MINUTES = 5;

    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final UserRepository userRepository;

    public ForgotPasswordController(ForgotPasswordRepo forgotPasswordRepo, PasswordEncoder passwordEncoder, EmailService emailService, UserRepository userRepository) {
        this.forgotPasswordRepo = forgotPasswordRepo;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.userRepository = userRepository;
    }

    @PostMapping("/verifyEmail/{email}")
    public ResponseEntity<String> verifyEmail(@PathVariable String email){
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Please provide a valid email"));

        int otp = otpGenerator();
        MailBody mailBody = MailBody.builder()
                .to(email)
                .text("This is the OTP for your forgot password request : " + otp)
                .subject("OTP to rest your password")
                .build();

        ForgetPassword forgetPassword = ForgetPassword.builder()
                .otp(otp)
                .expirationTime(new Date(System.currentTimeMillis() + (OTP_EXPIRATION_MINUTES * 60 * 1000)))
                .user(user)
                .build();

        emailService.sendSimpleMessage(mailBody);
        forgotPasswordRepo.save(forgetPassword);

        return ResponseEntity.ok("Email sent for verification");
    }

    @PostMapping("/verifyOtp/{otp}/{email}")
    public ResponseEntity<String> verifyOtp(@PathVariable Integer otp, @PathVariable String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Please provide a valid email"));

        ForgetPassword forgetPassword = forgotPasswordRepo.findByOtpAndUser(otp, user)
                .orElseThrow(() -> new RuntimeException("Invalid OTP for email: " + email));

        if (forgetPassword.getExpirationTime().before(Date.from(Instant.now()))) {
            forgotPasswordRepo.deleteById(forgetPassword.getForgotPasswordId());
            return new ResponseEntity<>("OTP has expired!", HttpStatus.EXPECTATION_FAILED);
        }

        return ResponseEntity.ok("OTP verified");
    }

    @PostMapping("/changePassword/{email}")
    public ResponseEntity<String> changePasswordHandler(@RequestBody ChangePassword changePassword,
                                                        @PathVariable String email) {
        if (!Objects.equals(changePassword.password(), changePassword.repeatPassword())) {
            return new ResponseEntity<>("please enter the password again", HttpStatus.EXPECTATION_FAILED);
        }

        String encodedPassword = passwordEncoder.encode(changePassword.password());
        userRepository.updatePassword(email, encodedPassword);

        return ResponseEntity.ok("Password has been changed successfully");
    }

    @PostMapping("/resetPassword/{email}")
    public ResponseEntity<String> restPasswordHandler(@RequestBody ChangePassword changePassword,
                                                      @PathVariable String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found!"));

        if (!Objects.equals(changePassword.password(), changePassword.repeatPassword())) {
            return new ResponseEntity<>("please enter the password again", HttpStatus.EXPECTATION_FAILED);
        }

        String encodedPassword = passwordEncoder.encode(changePassword.password());
        userRepository.updatePassword(user.getEmail(), encodedPassword);

        return ResponseEntity.ok("Password has been changed successfully");
    }

    private Integer otpGenerator() {
        Random random = new Random();
        return random.nextInt(100_100, 999_999);
    }
}
