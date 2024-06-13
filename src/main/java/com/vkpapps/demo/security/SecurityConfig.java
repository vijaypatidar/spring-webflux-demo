package com.vkpapps.demo.security;

import com.vkpapps.demo.security.jwt.JwtTokenAuthenticationFilter;
import com.vkpapps.demo.security.jwt.JwtTokenProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;
import org.springframework.web.reactive.config.CorsRegistry;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import org.springframework.web.reactive.config.WebFluxConfigurerComposite;
import reactor.core.publisher.Mono;

@Configuration
@EnableReactiveMethodSecurity
@EnableWebFlux
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder getPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    SecurityWebFilterChain springWebFilterChain(
            ServerHttpSecurity http,
            JwtTokenProvider tokenProvider,
            ReactiveAuthenticationManager reactiveAuthenticationManager) {
        final var authWhiteList = new String[]{
                // -- Swagger UI v2
                "/v2/api-docs",
                "/swagger-resources",
                "/swagger-resources/**",
                "/configuration/ui",
                "/configuration/security",
                "/swagger-ui.html",
                "/health",
                "/webjars/**",
                "/actuator/**",
                // -- Swagger UI v3 (OpenAPI)
                "/v3/api-docs/**",
                "/swagger-ui/**",
                // other public endpoints of your API may be appended to this array
                "/auth/**"
        };
        return http
                .exceptionHandling()
                .authenticationEntryPoint((swe, e) ->
                        Mono.fromRunnable(() -> swe.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED))
                ).accessDeniedHandler((swe, e) ->
                        Mono.fromRunnable(() -> swe.getResponse().setStatusCode(HttpStatus.FORBIDDEN))
                ).and()
                .csrf().disable()
                .formLogin().disable()
                .authenticationManager(reactiveAuthenticationManager)
                .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
                .authorizeExchange()
                .pathMatchers(HttpMethod.OPTIONS).permitAll()
                .pathMatchers(authWhiteList).permitAll()
                .anyExchange().authenticated()
                .and()
                .addFilterAt(new JwtTokenAuthenticationFilter(tokenProvider),
                        SecurityWebFiltersOrder.HTTP_BASIC)
                .build();

    }


    @Bean
    public ReactiveAuthenticationManager reactiveAuthenticationManager(
            ReactiveUserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder) {
        var authenticationManager =
                new UserDetailsRepositoryReactiveAuthenticationManager(userDetailsService);
        authenticationManager.setPasswordEncoder(passwordEncoder);
        return authenticationManager;
    }

    @Bean
    public WebFluxConfigurer corsConfigurer() {
        return new WebFluxConfigurerComposite() {

            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**").allowedOrigins("*")
                        .allowedMethods("*");
            }
        };
    }

}
