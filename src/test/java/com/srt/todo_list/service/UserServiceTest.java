package com.srt.todo_list.service;

import com.srt.todo_list.dto.request.CreateUserRequest;
import com.srt.todo_list.dto.response.UserResponse;
import com.srt.todo_list.entity.User;
import com.srt.todo_list.exception.AppException;
import com.srt.todo_list.exception.ErrorCode;
import com.srt.todo_list.mapper.UserMapper;
import com.srt.todo_list.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    private CreateUserRequest request;
    private User user;
    private User savedUser;
    private UserResponse expectedResponse;

    @BeforeEach
    void initData() {
        request = CreateUserRequest.builder()
                .username("john")
                .password("password123")
                .fullName("John Doe")
                .build();
        user = User.builder()
                .username("john")
                .password("password123")
                .fullName("John Doe")
                .build();
        savedUser = User.builder()
                .id("00f0f5d2")
                .username("john")
                .password("encoded-password")
                .fullName("John Doe")
                .build();
        expectedResponse = UserResponse.builder()
                .id("00f0f5d2")
                .username("john")
                .fullName("John Doe")
                .build();
    }

    @Test
    void createUser_success() {
        when(userMapper.toUser(request)).thenReturn(user);
        when(passwordEncoder.encode(request.getPassword())).thenReturn("encoded-password");
        when(userRepository.save(user)).thenReturn(savedUser);
        when(userMapper.toUserResponse(savedUser)).thenReturn(expectedResponse);

        UserResponse actualResponse = userService.createUser(request);

        assertSame(expectedResponse, actualResponse);
        assertEquals("encoded-password", user.getPassword());
        verify(userRepository).save(user);
        verify(userMapper).toUserResponse(savedUser);
    }

    @Test
    void createUser_whenRepositoryThrowsException_thenThrowUserExisted() {
        when(userMapper.toUser(request)).thenReturn(user);
        when(passwordEncoder.encode(request.getPassword())).thenReturn("encoded-password");
        when(userRepository.save(user)).thenThrow(new RuntimeException("Duplicate username"));

        AppException exception = assertThrows(AppException.class,
                () -> userService.createUser(request));

        assertEquals(ErrorCode.USER_EXISTED, exception.getErrorCode());
        assertEquals("encoded-password", user.getPassword());
        verify(userMapper, never()).toUserResponse(any());
    }
}
