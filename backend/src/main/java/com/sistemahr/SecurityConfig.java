package com.sistemahr;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import javax.crypto.SecretKey;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.stereotype.Service;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.OncePerRequestFilter;

@ConfigurationProperties(prefix = "app")
record AppProperties(String jwtSecret, long jwtExpirationMinutes, String uploadDir, String corsAllowedOrigins, String initialAdminPassword, boolean cookieSecure, AttendanceProperties attendance) {
    record AttendanceProperties(LocalTime startTime, int lateToleranceMinutes) {}
}

@Getter
class CurrentUser {
    private final UserAccount account;

    CurrentUser(UserAccount account) {
        this.account = account;
    }

    Long id() {
        return account.getId();
    }

    String email() {
        return account.getEmail();
    }

    Role role() {
        return account.getRole();
    }

    Long employeeId() {
        return account.getEmployee() == null ? null : account.getEmployee().getId();
    }
}

@Service
@RequiredArgsConstructor
class SecurityUserDetailsService implements UserDetailsService {
    private final UserRepository users;

    @Override
    public UserDetails loadUserByUsername(String username) {
        UserAccount account = users.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no registrado"));
        if (account.getStatus() != AccountStatus.ACTIVO || (account.getEmployee() != null && account.getEmployee().getEmploymentStatus() == EmploymentStatus.INACTIVO)) {
            throw new UsernameNotFoundException("Usuario inactivo");
        }
        return new org.springframework.security.core.userdetails.User(
                account.getEmail(),
                account.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_" + account.getRole().name()))
        );
    }
}

@Service
@RequiredArgsConstructor
class JwtService {
    private final AppProperties props;

    String generate(UserAccount user) {
        Date now = new Date();
        Date expires = new Date(now.getTime() + Duration.ofMinutes(props.jwtExpirationMinutes()).toMillis());
        return Jwts.builder()
                .subject(user.getEmail())
                .claim("role", user.getRole().name())
                .issuedAt(now)
                .expiration(expires)
                .signWith(key())
                .compact();
    }

    String subject(String token) {
        Claims claims = Jwts.parser().verifyWith(key()).build().parseSignedClaims(token).getPayload();
        return claims.getSubject();
    }

    private SecretKey key() {
        String secret = props.jwtSecret();
        if (secret == null || secret.length() < 32) {
            throw new IllegalStateException("JWT_SECRET must contain at least 32 characters");
        }
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }
}

@RequiredArgsConstructor
class JwtAuthenticationFilter extends OncePerRequestFilter {
    static final String AUTH_COOKIE = "SISTEMAHR_SESSION";
    private final JwtService jwt;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String token = tokenFromRequest(request);
        if (token != null) {
            try {
                String email = jwt.subject(token);
                if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    UserDetails user = userDetailsService.loadUserByUsername(email);
                    var auth = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
            } catch (RuntimeException ignored) {
                SecurityContextHolder.clearContext();
            }
        }
        filterChain.doFilter(request, response);
    }

    private String tokenFromRequest(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }
        return Arrays.stream(cookies)
                .filter(cookie -> AUTH_COOKIE.equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);
    }
}

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
class SecurityConfig {
    private final JwtService jwt;
    private final SecurityUserDetailsService userDetailsService;
    private final AppProperties props;

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf
                        .csrfTokenRepository(csrfTokenRepository())
                        .csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler()))
                .cors(Customizer.withDefaults())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/auth/csrf").permitAll()
                        .requestMatchers("/actuator/health").permitAll()
                        .requestMatchers("/error").permitAll()
                        .requestMatchers("/api/**").authenticated()
                        .requestMatchers(HttpMethod.GET, "/**").permitAll()
                        .anyRequest().authenticated())
                .addFilterBefore(new JwtAuthenticationFilter(jwt, userDetailsService), UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    DaoAuthenticationProvider authenticationProvider(PasswordEncoder encoder) {
        var provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(encoder);
        return provider;
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(Arrays.stream(props.corsAllowedOrigins().split(","))
                .map(String::trim)
                .filter(origin -> !origin.isBlank())
                .toList());
        config.setAllowCredentials(true);
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type", "Accept", "Origin", "X-Requested-With"));
        config.setExposedHeaders(List.of("Content-Disposition"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    private CookieCsrfTokenRepository csrfTokenRepository() {
        CookieCsrfTokenRepository repository = new CookieCsrfTokenRepository();
        repository.setCookieCustomizer(cookie -> cookie
                .httpOnly(true)
                .secure(props.cookieSecure())
                .path("/"));
        return repository;
    }
}
