package org.africa.movieflix.contollers;

import org.africa.movieflix.auth.entities.RefreshToken;
import org.africa.movieflix.auth.entities.User;
import org.africa.movieflix.auth.services.AuthService;
import org.africa.movieflix.auth.services.JwtService;
import org.africa.movieflix.auth.services.RefreshTokenService;
import org.africa.movieflix.auth.utils.AuthResponse;
import org.africa.movieflix.auth.utils.LoginRequest;
import org.africa.movieflix.auth.utils.RefreshTokenRequest;
import org.africa.movieflix.auth.utils.RegisterRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth/")
public class AuthController {

    private final AuthService authService;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    public AuthController(AuthService authService, JwtService jwtService, RefreshTokenService refreshTokenService) {
        this.authService = authService;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest registerRequest){
        return ResponseEntity.ok(authService.register(registerRequest));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest loginRequest){
        return ResponseEntity.ok(authService.login(loginRequest));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(@RequestBody RefreshTokenRequest refreshTokenRequest){
        RefreshToken refreshToken = refreshTokenService.verifyRefreshToken(refreshTokenRequest.getRefreshToken());
        User user = refreshToken.getUser();

        String accessToken = jwtService.generateToken(user);
        return  ResponseEntity.ok(AuthResponse
                .builder()
                .accessTokens(accessToken)
                .refreshToken(refreshToken.getRefreshToken())
                .build());
    }
}
