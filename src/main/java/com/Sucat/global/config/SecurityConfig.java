package com.Sucat.global.config;

import com.Sucat.domain.game.model.Game;
import com.Sucat.domain.game.model.GameCategory;
import com.Sucat.domain.game.repository.GameRepository;
import com.Sucat.domain.token.repository.BlacklistedTokenRepository;
import com.Sucat.domain.token.repository.TokenRepository;
import com.Sucat.domain.user.model.User;
import com.Sucat.domain.user.model.UserRole;
import com.Sucat.domain.user.repository.UserRepository;
import com.Sucat.global.security.filter.CustomLogoutFilter;
import com.Sucat.global.security.filter.JwtAuthenticationProcessingFilter;
import com.Sucat.global.security.filter.LoginFilter;
import com.Sucat.global.security.handler.LoginFailureHandler;
import com.Sucat.global.security.handler.LoginSuccessJWTProvideHandler;
import com.Sucat.global.security.service.UserDetailsServiceImpl;
import com.Sucat.global.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;

@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
    private final UserDetailsServiceImpl userDetailsService;
    private final ObjectMapper objectMapper;
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final BlacklistedTokenRepository blacklistedTokenRepository;
    private final JwtUtil jwtUtil;

    @Value("${admin.email}")
    private String adminEmail;

    @Value("${admin.password}")
    private String adminPassword;

    @Value("${security.origin1}")
    private String allowedOrigin1;
    @Value("${security.origin2}")
    private String allowedOrigin2;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .cors(corsConfigurer -> corsConfigurer.configurationSource(corsConfigurationSource())) // CORS 설정 추가
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .addFilterAt(loginFilter(), UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(jwtAuthenticationProcessingFilter(), LoginFilter.class)
                .addFilterBefore(new CustomLogoutFilter(jwtUtil, blacklistedTokenRepository,tokenRepository), LogoutFilter.class)

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/css/**", "/images/**", "/js/**", "/favicon.*", "/*/icon-*", "/", "/error/**", "/error", "/redis/**", "/stomp", "/stomp/**", "/healthcheck", "/upload").permitAll() // 정적 자원 설정
                        .requestMatchers("/api/v1/users/signup/**", "/api/v1/users/signup", "/login").permitAll()
                        .requestMatchers("/api/v1/users/password").permitAll()
                        .requestMatchers("/api/v1/reissue/accessToken").permitAll()
                        .requestMatchers("/api/v1/users/nickname/duplication").permitAll()
                        .requestMatchers("/api/v1/verification/**").permitAll()
                        .requestMatchers("/notification/**").permitAll()
                        .requestMatchers("/ws/**", "/sub/**", "/pub/**", "/chats/**").permitAll()
                        .requestMatchers("/api/v1/game/score").permitAll()
                        .requestMatchers("/admin/**").hasAuthority("ADMIN")
                        .anyRequest().authenticated());

        return http.build();
    }

    @Bean
    public static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 인증 관리자 관련 설정
    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() throws Exception {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();

        daoAuthenticationProvider.setUserDetailsService(userDetailsService);
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());

        return daoAuthenticationProvider;
    }

    @Bean
    public LoginSuccessJWTProvideHandler loginSuccessJWTProvideHandler() {
        return new LoginSuccessJWTProvideHandler(jwtUtil, userRepository, tokenRepository);
    }

    @Bean
    public LoginFailureHandler loginFailureHandler() {
        return new LoginFailureHandler();
    }

    @Bean
    public AuthenticationManager authenticationManager() throws Exception { //AuthenticationManager 등록
        DaoAuthenticationProvider provider = daoAuthenticationProvider(); //DaoAuthenticationProvider 사용
        return new ProviderManager(provider);
    }

    @Bean
    public LoginFilter loginFilter() throws Exception {
        LoginFilter loginFilter = new LoginFilter(objectMapper);
        loginFilter.setAuthenticationManager(authenticationManager());
        loginFilter.setAuthenticationSuccessHandler(loginSuccessJWTProvideHandler());
        loginFilter.setAuthenticationFailureHandler(loginFailureHandler());
        return loginFilter;
    }

    @Bean
    public JwtAuthenticationProcessingFilter jwtAuthenticationProcessingFilter() {

        return new JwtAuthenticationProcessingFilter(jwtUtil);
    }

    @Bean
    public CommandLineRunner initAdmin(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (!userRepository.existsByEmail(adminEmail)) {
                User admin = User.builder()
                        .email(adminEmail)
                        .password(passwordEncoder.encode(adminPassword))
                        .role(UserRole.ADMIN)
                        .build();
                userRepository.save(admin);
            }
        };
    }

    @Bean
    public CommandLineRunner initGames(GameRepository gameRepository) {
        return args -> {
            if (gameRepository.count() == 0) {
                gameRepository.save(new Game(GameCategory.Fruit));
                gameRepository.save(new Game(GameCategory.Churu));
                gameRepository.save(new Game(GameCategory.Monster));
                gameRepository.save(new Game(GameCategory.Ato));
                gameRepository.save(new Game(GameCategory.Bullet));
            }
        };
    }

    private CorsConfigurationSource corsConfigurationSource() {
        return request -> {
            CorsConfiguration config = new CorsConfiguration();
            config.setAllowedHeaders(Arrays.asList(allowedOrigin1, allowedOrigin2));
            config.setAllowedMethods(Collections.singletonList("*"));
            config.setAllowedOriginPatterns(Collections.singletonList("*"));
            config.setAllowCredentials(true);
            config.setMaxAge(3600L);
            config.setAllowedHeaders(Arrays.asList("Authorization", "Authorization-refresh", "Cache-Control", "Content-Type"));
            config.setExposedHeaders(Arrays.asList("Authorization", "Authorization-refresh", "Set-Cookie"));

            return config;
        };
    }
}
