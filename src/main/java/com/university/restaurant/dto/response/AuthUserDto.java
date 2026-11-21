package com.university.restaurant.dto.response;

import com.university.restaurant.common.enums.RoleType;
import com.university.restaurant.common.enums.UserStatus;
import com.university.restaurant.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * [M1] Outbound JSON payload returned upon authentication (/login, /register,
 * /me).
 * Includes role-based target redirect path per US-AUTH-01 Scenario 2.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthUserDto {

    private Long id;
    private String fullName;
    private String email;
    private String phone;
    private RoleType role;
    private UserStatus status;
    private String redirectUrl;

    public static AuthUserDto fromEntity(User user) {
        return AuthUserDto.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .role(user.getRole())
                .status(user.getStatus())
                .redirectUrl(resolveRedirectUrl(user.getRole()))
                .build();
    }

    public static String resolveRedirectUrl(RoleType role) {
        if (role == null) {
            return "/menu";
        }
        return switch (role) {
            case ROLE_ADMIN -> "/admin/dashboard";
            case ROLE_STAFF -> "/staff/dashboard";
            case ROLE_CUSTOMER -> "/menu";
        };
    }
}
