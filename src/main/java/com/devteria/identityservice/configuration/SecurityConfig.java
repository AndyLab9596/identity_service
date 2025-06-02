package com.devteria.identityservice.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final String[] PUBLIC_ENDPOINT = {
            "/users",
            "/auth/token", "/auth/introspect", "/auth/logout", "auth/refresh"
    };

    @Autowired
    private CustomJwtDecoder customJwtDecoder;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(
                        request ->
                                request.requestMatchers(HttpMethod.POST, PUBLIC_ENDPOINT).permitAll()
                                        // Authorize by END POINT
                                        //.requestMatchers(HttpMethod.GET, "/users").hasRole(Role.ADMIN.name())
                                .anyRequest().authenticated()
                );


        httpSecurity.oauth2ResourceServer(oauth2 -> {
            oauth2.jwt(jwtConfigurer ->
                    jwtConfigurer
                            // Register an Authentication Provider.
                            // Using jwtDecoder in Authentication Provider.
                            .decoder(customJwtDecoder)
                            // jwtAuthenticationConverter is to convert the SCOPE_ADMIN (this is OAuth2 by default) to ROLE_ADMIN
                            .jwtAuthenticationConverter(jwtAuthenticationConverter())

            )       // Dealing with 401 error code.
                    .authenticationEntryPoint(new JwtAuthenticationEntryPoint());
        });

        return httpSecurity.build();
    }

    @Bean
    JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        jwtGrantedAuthoritiesConverter.setAuthorityPrefix("");

        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);
        return jwtAuthenticationConverter;
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }
}
