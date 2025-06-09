package com.devteria.identityservice.controllers;

import com.devteria.identityservice.dto.request.UserCreationRequest;
import com.devteria.identityservice.dto.response.UserResponse;
import com.devteria.identityservice.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("/test.properties")
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    private UserCreationRequest request;
    private UserResponse userResponse;
    private LocalDate dob;

    @BeforeEach
    void initData() {
        dob = LocalDate.of(1995, 02, 03);

        request = UserCreationRequest.builder()
                .username("john")
                .firstName("John")
                .lastName("Doe")
                .password("12345678")
                .dob(dob)
                .build();

        userResponse = UserResponse.builder()
                .id("0402e031-6133-4b65-b617-6472dc1edfb0")
                .username("john")
                .firstName("John")
                .lastName("Doe")
                .dob(dob)
                .build();
    }

    @Test
    void createUser_validRequest() throws Exception {
        // given
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        String content = objectMapper.writeValueAsString(request);

        when(userService.createUser(any())).thenReturn(userResponse);

        // when
        mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/users")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content)
        )       // then
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("code")
                        .value(1000))
                .andExpect(MockMvcResultMatchers
                        .jsonPath("result.id")
                        .value("0402e031-6133-4b65-b617-6472dc1edfb0"));
    }

    @Test
    void createUser_usernameInvalid_fail() throws Exception {
        // given
        request.setUsername("jo");

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        String content = objectMapper.writeValueAsString(request);

        // when(userService.createUser(any())).thenReturn(userResponse);

        // when
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post("/users")
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content(content)
                )       // then
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("code")
                        .value(1003))
                .andExpect(MockMvcResultMatchers
                        .jsonPath("message")
                        .value("Username must be at least 3 characters"));
    }

}
