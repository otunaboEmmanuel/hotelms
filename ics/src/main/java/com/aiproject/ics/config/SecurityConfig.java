package com.aiproject.ics.config;

import com.aiproject.ics.filter.JwtFilter;
import com.aiproject.ics.service.CustomOAuth2SuccessHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
@EnableMethodSecurity
@Configuration
public class SecurityConfig  {
    @Autowired
    private JwtFilter jwtAuthenticationFilter;

    @Autowired
    private CustomOAuth2SuccessHandler customOAuth2SuccessHandler;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(11); // Define PasswordEncoder bean for use in your app
    }
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.securityMatcher("/**")
                .csrf(csrf -> csrf.disable()) // Disable CSRF protection for stateless APIs
                .authorizeHttpRequests(auth -> auth
                                .requestMatchers("/hotel/login","/hotel/addUser",
                                        "/hotel/changePassword",
                                        "/oauth2/**",
                                        "/hotel/otp-verification",
                                        "/hotel/verify-email",
                                        "/ai/**",
                                        "/v3/api-docs/**",
                                        "/swagger-ui/**",
                                        "/swagger-ui.html").permitAll()
                                .anyRequest().authenticated()
//                        .requestMatchers("/login", "/auth/**","/api/**").permitAll() // Allow login and public paths
//                        .anyRequest().authenticated() // Secure all other endpoints
                ).formLogin(Customizer.withDefaults())
                .oauth2Login(oauth2 -> oauth2
                        .successHandler(customOAuth2SuccessHandler)
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class); // Add JWT filter

        return http.build();
    }
}
