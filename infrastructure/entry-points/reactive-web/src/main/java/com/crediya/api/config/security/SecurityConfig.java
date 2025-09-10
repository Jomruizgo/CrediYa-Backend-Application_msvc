package com.crediya.api.config.security;

import com.crediya.api.util.ApiPaths;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationWebFilter jwtAuthenticationWebFilter;

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .cors(ServerHttpSecurity.CorsSpec::and)
                .authorizeExchange(exchanges -> 
                    exchanges
                        // Public endpoints
                        .pathMatchers("/actuator/**").permitAll()
                        .pathMatchers("/swagger-ui/**", "/v3/api-docs/**", "/webjars/**").permitAll()
                        // Loan Types - Public access (anyone can see available loan types)
                        .pathMatchers(HttpMethod.GET, ApiPaths.LOAN_TYPE_BASE + "/**").permitAll()
                        // Loan Applications - Only CLIENT role can create applications
                        .pathMatchers(HttpMethod.POST, ApiPaths.LOAN_APPLICATION_BASE).hasRole("CLIENT")
                        // Loan Applications - Only CLIENT role can view their applications  
                        .pathMatchers(HttpMethod.GET, ApiPaths.LOAN_APPLICATION_BY_ID).hasRole("CLIENT")
                        // Loan Applications Review - Only SELLER role can access review functionality
                        .pathMatchers(HttpMethod.GET, ApiPaths.LOAN_APPLICATIONS).hasRole("SELLER")
                        .anyExchange().authenticated()
                )
                .addFilterBefore(jwtAuthenticationWebFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .build();
    }
}