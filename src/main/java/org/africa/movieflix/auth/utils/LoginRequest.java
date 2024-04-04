package org.africa.movieflix.auth.utils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.Authentication;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginRequest {

    private String email;
    private String password;

}
