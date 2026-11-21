package com.university.restaurant.controller;

import com.university.restaurant.common.response.ApiResponse;
import com.university.restaurant.dto.request.ChangePasswordRequest;
import com.university.restaurant.dto.request.LoginRequest;
import com.university.restaurant.dto.request.RegisterRequest;
import com.university.restaurant.dto.request.UpdateProfileRequest;
import com.university.restaurant.dto.response.AuthUserDto;
import com.university.restaurant.dto.response.UserProfileDto;
import com.university.restaurant.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * [M1] /api/v1/auth/** (register, login, logout, me, profile) - EPIC-01 / US-AUTH-01.
 * <ul>
 *   <li>Public: POST /register, POST /login, POST /logout</li>
 *   <li>Authenticated (any role): GET /me, GET|PUT /profile, PUT /password</li>
 * </ul>
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /** Customer self-registration -> 201 Created with ROLE_CUSTOMER account. */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthUserDto>> register(@Valid @RequestBody RegisterRequest request) {
        AuthUserDto user = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Registration successful. Please log in.", user));
    }

    /** Session login; response carries role-based redirectUrl for the Frontend. */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthUserDto>> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse) {
        AuthUserDto user = authService.login(request, httpRequest, httpResponse);
        return ResponseEntity.ok(ApiResponse.ok("Login successful", user));
    }

    /** Invalidates the HttpSession (idempotent; safe when already logged out). */
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(HttpServletRequest httpRequest) {
        authService.logout(httpRequest);
        return ResponseEntity.ok(ApiResponse.ok("Logout successful", null));
    }

    /** Current session user (used by Frontend to restore login state / guard routes). */
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<AuthUserDto>> me() {
        return ResponseEntity.ok(ApiResponse.ok(authService.getCurrentAuthUser()));
    }

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<UserProfileDto>> getProfile() {
        return ResponseEntity.ok(ApiResponse.ok(authService.getProfile()));
    }

    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<UserProfileDto>> updateProfile(@Valid @RequestBody UpdateProfileRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Profile updated successfully", authService.updateProfile(request)));
    }

    @PutMapping("/password")
    public ResponseEntity<ApiResponse<Void>> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        authService.changePassword(request);
        return ResponseEntity.ok(ApiResponse.ok("Password changed successfully", null));
    }
}