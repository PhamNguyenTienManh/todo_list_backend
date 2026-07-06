package com.srt.todo_list.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.srt.todo_list.dto.request.AuthenticationRequest;
import com.srt.todo_list.dto.request.IntrospectRequest;
import com.srt.todo_list.dto.response.AuthenticationResponse;
import com.srt.todo_list.dto.response.IntrospectResponse;
import com.srt.todo_list.service.AuthenticationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthenticationController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AuthenticationControllerTest {

    @MockitoBean
    private AuthenticationService authenticationService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private AuthenticationRequest authenticationRequest;
    private AuthenticationResponse authenticationResponse;
    private IntrospectRequest introspectRequest;
    private IntrospectResponse introspectResponse;

    @BeforeEach
    void initData() {
        authenticationRequest = AuthenticationRequest.builder()
                .username("john")
                .password("password123")
                .build();
        authenticationResponse = AuthenticationResponse.builder()
                .token("jwt-token")
                .isAuthenticated(true)
                .build();
        introspectRequest = IntrospectRequest.builder()
                .token("jwt-token")
                .build();
        introspectResponse = IntrospectResponse.builder()
                .valid(true)
                .build();
    }

    @Test
    void authenticate_success() throws Exception {
        when(authenticationService.authenticate(any(AuthenticationRequest.class)))
                .thenReturn(authenticationResponse);

        mockMvc.perform(post("/auth/token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authenticationRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1000))
                .andExpect(jsonPath("$.result.token").value("jwt-token"))
                .andExpect(jsonPath("$.result.authenticated").value(true));

        verify(authenticationService).authenticate(any(AuthenticationRequest.class));
    }

    @Test
    void introspect_success() throws Exception {
        when(authenticationService.introspect(any(IntrospectRequest.class)))
                .thenReturn(introspectResponse);

        mockMvc.perform(post("/auth/introspect")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(introspectRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1000))
                .andExpect(jsonPath("$.result.valid").value(true));

        verify(authenticationService).introspect(any(IntrospectRequest.class));
    }
}
