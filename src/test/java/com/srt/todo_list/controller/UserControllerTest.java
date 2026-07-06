package com.srt.todo_list.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.srt.todo_list.dto.request.CreateUserRequest;
import com.srt.todo_list.dto.response.UserResponse;
import com.srt.todo_list.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
public class UserControllerTest {

    @MockitoBean
    private UserService userService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private CreateUserRequest createUserRequest;
    private CreateUserRequest invalidUsernameRequest;
    private UserResponse userResponse;

    @BeforeEach
    void initData() {
        createUserRequest = CreateUserRequest.builder()
                .username("john")
                .password("password123")
                .fullName("John Doe")
                .build();
        invalidUsernameRequest = CreateUserRequest.builder()
                .username("jo")
                .password("password123")
                .fullName("John Doe")
                .build();
        userResponse = UserResponse.builder()
                .id("00f0f5d2")
                .username("john")
                .fullName("John Doe")
                .build();
    }

    @Test
    void createUser_success() throws Exception {
        when(userService.createUser(any(CreateUserRequest.class))).thenReturn(userResponse);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createUserRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1000))
                .andExpect(jsonPath("$.result.id").value("00f0f5d2"))
                .andExpect(jsonPath("$.result.username").value("john"))
                .andExpect(jsonPath("$.result.fullName").value("John Doe"));

        verify(userService).createUser(any(CreateUserRequest.class));
    }

    @Test
    void createUser_whenUsernameInvalid_thenReturnBadRequest() throws Exception {
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidUsernameRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(1004))
                .andExpect(jsonPath("$.message").value("Username must be at least 3 characters"));

        verify(userService, never()).createUser(any());
    }
}
