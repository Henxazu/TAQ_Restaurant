package com.university.restaurant.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.university.restaurant.common.enums.UserStatus;
import com.university.restaurant.common.response.ApiResponse;
import com.university.restaurant.repository.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * [M1] Role-based URL access rules for /api/v1/**, Session SecurityContext persistence,
 * UserDetailsService backed by UserRepository, and BCrypt password encoder.
 * Unauthenticated -> 401 JSON, wrong role -> 403 JSON.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            SecurityContextRepository securityContextRepository,
            ObjectMapper objectMapper
    ) throws Exception {
        http
            .cors(Customizer.withDefaults())                 // uses CorsConfig#corsConfigurationSource
            .csrf(AbstractHttpConfigurer::disable)
            .securityContext(sc -> sc
                .securityContextRepository(securityContextRepository)
                .requireExplicitSave(true))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.POST, "/api/v1/auth/register", "/api/v1/auth/login", "/api/v1/auth/logout").permitAll()
                .requestMatchers("/api/v1/auth/**").authenticated()
                .requestMatchers(HttpMethod.GET, "/api/v1/menu/**").permitAll()
                .requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**").permitAll()
                .requestMatchers("/", "/error", "/css/**", "/js/**", "/images/**").permitAll()
                .requestMatchers("/api/v1/customer/**").hasRole("CUSTOMER")
                .requestMatchers("/api/v1/staff/**").hasAnyRole("STAFF", "ADMIN")
                .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated())
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint((request, response, authException) ->
                    writeJsonError(response, objectMapper, HttpStatus.UNAUTHORIZED, "Authentication required. Please log in."))
                .accessDeniedHandler((request, response, accessDeniedException) ->
                    writeJsonError(response, objectMapper, HttpStatus.FORBIDDEN, "Access denied. Insufficient permissions.")))
            .httpBasic(AbstractHttpConfigurer::disable)
            .formLogin(AbstractHttpConfigurer::disable);
        return http.build();
    }

    @Bean
    public SecurityContextRepository securityContextRepository() {
        return new HttpSessionSecurityContextRepository();
    }

    @Bean
    public UserDetailsService userDetailsService(UserRepository userRepository) {
        return identifier -> userRepository.findByEmailOrPhone(identifier, identifier)
                .map(user -> org.springframework.security.core.userdetails.User.builder()
                        .username(user.getEmail())
                        .password(user.getPasswordHash())
                        .authorities(List.of(new SimpleGrantedAuthority(user.getRole().name())))
                        .accountLocked(user.getStatus() == UserStatus.LOCKED)
                        .disabled(user.getStatus() == UserStatus.LOCKED)
                        .build())
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + identifier));
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    private void writeJsonError(
            HttpServletResponse response,
            ObjectMapper objectMapper,
            HttpStatus status,
            String message
    ) throws IOException {
        response.setStatus(status.value());
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(response.getWriter(), ApiResponse.error(message));
    }
}
