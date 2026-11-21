package com.university.restaurant.dto.request;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * [M1] Inbound JSON payload for Login (US-AUTH-01).
 * Accepts `email` (or aliases `username` / `emailOrPhone`) plus `password`.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginRequest {

    @NotBlank(message = "Email or phone is required")
    @JsonAlias({"username", "emailOrPhone"})
    private String email;

    @NotBlank(message = "Password is required")
    private String password;
}

