package com.devteria.identityservice.configuration;

import java.util.HashSet;

import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.devteria.identityservice.entity.User;
import com.devteria.identityservice.enums.Role;
import com.devteria.identityservice.repository.UserRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ApplicationInitConfig {
    PasswordEncoder passwordEncoder;

    @Bean
    @ConditionalOnProperty(
            prefix = "spring",
            value = "datasource.driverClassName",
            havingValue = "com.mysql.cj.jdbc.Driver")
    ApplicationRunner applicationRunner(UserRepository userRepository) {
        String adminUsername = "admin";
        String adminPassword = passwordEncoder.encode("admin");
        HashSet<String> roles = new HashSet<>();
        roles.add(Role.ADMIN.name());

        return args -> {
            if (userRepository.findByUsername(adminUsername).isEmpty()) {
                User user = User.builder()
                        .username(adminUsername)
                        .password(adminPassword)
                        .build();
                userRepository.save(user);
                log.warn("admin user has been created with default password: admin");
            }
        };
    }
}
