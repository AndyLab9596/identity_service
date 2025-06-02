package com.devteria.identityservice.controller;

import com.devteria.identityservice.dto.request.*;
import com.devteria.identityservice.dto.response.AuthenticationResponse;
import com.devteria.identityservice.dto.response.IntrospectResponse;
import com.devteria.identityservice.service.AuthenticationService;
import com.nimbusds.jose.JOSEException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationController {
    AuthenticationService authenticationService;

    @PostMapping("/token")
    ApiResponse<AuthenticationResponse> login(
            @RequestBody AuthenticationRequest request
            ) {
        AuthenticationResponse authenticationResponse = authenticationService.authenticate(request);
        return ApiResponse.<AuthenticationResponse>builder().result(authenticationResponse).build();
    }

    @PostMapping("/introspect")
    ApiResponse<IntrospectResponse> introspect(@RequestBody IntrospectRequest request) throws ParseException, JOSEException {
        IntrospectResponse introspectResponse = authenticationService.introspect(request);

        return ApiResponse.<IntrospectResponse>builder().result(introspectResponse).build();
    }

    @PostMapping("/refresh")
    ApiResponse<AuthenticationResponse> refreshToken(
            @RequestBody RefreshRequest request
    ) throws ParseException, JOSEException {
        AuthenticationResponse authenticationResponse = authenticationService.refreshToken(request);
        return ApiResponse.<AuthenticationResponse>builder().result(authenticationResponse).build();
    }

    @PostMapping("/logout")
    ApiResponse<Void> logout(@RequestBody LogoutRequest request) throws ParseException, JOSEException {
        authenticationService.logout(request);
        return ApiResponse.<Void>builder().build();
    }
}
