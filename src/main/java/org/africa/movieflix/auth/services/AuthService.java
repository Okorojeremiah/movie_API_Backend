package org.africa.movieflix.auth.services;

import lombok.RequiredArgsConstructor;
import org.africa.movieflix.auth.entities.User;
import org.africa.movieflix.auth.entities.UserRole;
import org.africa.movieflix.auth.repositories.UserRepository;
import org.africa.movieflix.auth.utils.AuthResponse;
import org.africa.movieflix.auth.utils.LoginRequest;
import org.africa.movieflix.auth.utils.RegisterRequest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {


    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final AuthenticationManager authenticationManager;

    public AuthResponse register(RegisterRequest registerRequest){
        var user = User.builder()
                .name(registerRequest.getName())
                .email(registerRequest.getEmail())
                .userName(registerRequest.getUsername())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .role(UserRole.ADMIN)
                .build();

        User savedUser = userRepository.save(user);
        var accessToken = jwtService.generateToken(savedUser);
        var refreshToken = refreshTokenService.createRefreshToken(savedUser.getEmail());

        return AuthResponse.builder()
                .accessTokens(accessToken)
                .refreshToken(refreshToken.getRefreshToken())
                .build();
    }

    public AuthResponse login(LoginRequest loginRequest) {

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()
                    )
            );
        }catch (BadCredentialsException ex) {
            throw new BadCredentialsException("Wrong username or password");
        }

        var user = userRepository.findByEmail(loginRequest.getEmail()).orElseThrow(
                () -> new UsernameNotFoundException("User not found")
        );
        var accessToken = jwtService.generateToken(user);
        var refreshToken = refreshTokenService.createRefreshToken(loginRequest.getEmail());

        return AuthResponse.builder()
                .accessTokens(accessToken)
                .refreshToken(refreshToken.getRefreshToken())
                .build();
    }
}
