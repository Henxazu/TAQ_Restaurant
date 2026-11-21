package com.university.restaurant.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * [M1] Inbound JSON payload for Customer self-registration (US-AUTH-01).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterRequest {

    public static final String EMAIL_REGEX = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
    public static final String PHONE_REGEX = "^0\\d{9}$";
    public static final String PASSWORD_REGEX = "^(?=.*[A-Za-z])(?=.*\\d)\\S{6,100}$";

    @NotBlank(message = "Full name is required")
    @Size(max = 100, message = "Full name must not exceed 100 characters")
    private String fullName;

    @NotBlank(message = "Email is required")
    @Email(regexp = EMAIL_REGEX, message = "Invalid email format (e.g., user@example.com)")
    @Size(max = 120, message = "Email must not exceed 120 characters")
    private String email;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = PHONE_REGEX, message = "Phone number must be exactly 10 digits starting with 0 (e.g., 0901234567)")
    private String phone;

    @NotBlank(message = "Password is required")
    @Size(min = 6, max = 100, message = "Password must be between 6 and 100 characters")
    @Pattern(
        regexp = PASSWORD_REGEX,
        message = "Password must be at least 6 characters, contain at least one letter and one number, and have no spaces"
    )
    private String password;

    @NotBlank(message = "Confirm password is required")
    private String confirmPassword;
}
