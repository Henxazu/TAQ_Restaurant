package com.university.restaurant.service;

import com.university.restaurant.common.enums.RoleType;
import com.university.restaurant.common.enums.UserStatus;
import com.university.restaurant.common.exception.BusinessException;
import com.university.restaurant.dto.request.ChangePasswordRequest;
import com.university.restaurant.dto.request.LoginRequest;
import com.university.restaurant.dto.request.RegisterRequest;
import com.university.restaurant.dto.request.UpdateProfileRequest;
import com.university.restaurant.dto.response.AuthUserDto;
import com.university.restaurant.dto.response.UserProfileDto;
import com.university.restaurant.entity.User;
import com.university.restaurant.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * [M1] TAQ_Restaurant AuthService: customer registration, session login/logout,
 * profile updates, and password changes.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final UserRepository userRepo;
    private final PasswordEncoder passEncoder;
    private final SecurityContextRepository contextRepo;

    /*
     * [M1]
     * Register customer
     * Validate password/confirm-password, check if email/phone already exists
     */
    @Transactional
    public AuthUserDto register(RegisterRequest req) {
        // Check password and confirm password
        if (req.getConfirmPassword() != null
                && !req.getConfirmPassword().isBlank()
                && !req.getPassword().equals(req.getConfirmPassword())) {
            throw BusinessException.badRequest("Password and confirm password do not match");
        }

        // Check exist email
        String mail = req.getEmail().trim().toLowerCase();
        String tel = req.getPhone().trim();

        if (userRepo.existsByEmail(mail) || userRepo.existsByPhone(tel)) {
            throw BusinessException.conflict("Email or phone number is already registered");
        }

        User entity = User.builder()
                .fullName(req.getFullName().trim())
                .email(mail)
                .phone(tel)
                .passwordHash(passEncoder.encode(req.getPassword()))
                .role(RoleType.ROLE_CUSTOMER)
                .status(UserStatus.ACTIVE)
                .build();

        return AuthUserDto.fromEntity(userRepo.save(entity));
    }

    /*
     * [M1]
     * Login customer
     * Check if user exists
     * Check if user is active
     * Check if password is correct
     */
    public AuthUserDto login(LoginRequest req, HttpServletRequest httpReq, HttpServletResponse httpRes) {
        String key = req.getEmail().trim();

        User entity = userRepo.findByEmailOrPhone(key.toLowerCase(), key)
                .orElseThrow(() -> BusinessException.unauthorized("Invalid email/phone or password"));

        if (entity.getStatus() == UserStatus.LOCKED) {
            throw BusinessException.forbidden("Your account has been deactivated. Please contact the restaurant.");
        }

        if (!passEncoder.matches(req.getPassword(), entity.getPasswordHash())) {
            throw BusinessException.unauthorized("Invalid email/phone or password");
        }

        var authority = new SimpleGrantedAuthority(entity.getRole().name());
        var token = new UsernamePasswordAuthenticationToken(entity.getEmail(), null, List.of(authority));

        SecurityContext ctx = SecurityContextHolder.createEmptyContext();
        ctx.setAuthentication(token);
        SecurityContextHolder.setContext(ctx);
        contextRepo.saveContext(ctx, httpReq, httpRes);

        return AuthUserDto.fromEntity(entity);
    }

    public void logout(HttpServletRequest httpReq) {
        HttpSession session = httpReq.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        SecurityContextHolder.clearContext();
    }

    public AuthUserDto getCurrentAuthUser() {
        return AuthUserDto.fromEntity(getAuthenticatedUserEntity());
    }

    public UserProfileDto getProfile() {
        return UserProfileDto.fromEntity(getAuthenticatedUserEntity());
    }

    @Transactional
    public UserProfileDto updateProfile(UpdateProfileRequest req) {
        User entity = getAuthenticatedUserEntity();
        String tel = req.getPhone().trim();

        // check if phone number is already registered by another user
        if (userRepo.existsByPhoneAndIdNot(tel, entity.getId())) {
            throw BusinessException.conflict("Phone number is already registered by another user");
        }

        entity.setFullName(req.getFullName().trim());
        entity.setPhone(tel);

        return UserProfileDto.fromEntity(userRepo.save(entity));
    }

    /*
     * Change password
     * Check if new password and confirm password match
     * Check if current password is correct
     */
    @Transactional
    public void changePassword(ChangePasswordRequest req) {
        if (req.getConfirmPassword() != null
                && !req.getConfirmPassword().isBlank()
                && !req.getNewPassword().equals(req.getConfirmPassword())) {
            throw BusinessException.badRequest("New password and confirm password do not match");
        }

        User entity = getAuthenticatedUserEntity();
        if (!passEncoder.matches(req.getCurrentPassword(), entity.getPasswordHash())) {
            throw BusinessException.badRequest("Current password is incorrect");
        }

        entity.setPasswordHash(passEncoder.encode(req.getNewPassword()));
        userRepo.save(entity);
    }

    /*
     * Get authenticated user entity
     * Check if user is authenticated
     * Check if user is active
     * Check if password is correct
     */
    public User getAuthenticatedUserEntity() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            throw BusinessException.unauthorized("Authentication required. Please log in.");
        }

        User entity = userRepo.findByEmail(auth.getName())
                .orElseThrow(() -> BusinessException.unauthorized("Authenticated user account no longer exists"));

        if (entity.getStatus() == UserStatus.LOCKED) {
            throw BusinessException.forbidden("Your account has been deactivated. Please contact the restaurant.");
        }

        return entity;
    }
}