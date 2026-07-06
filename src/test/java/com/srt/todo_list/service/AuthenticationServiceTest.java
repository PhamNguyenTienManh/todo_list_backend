package com.srt.todo_list.service;

import com.nimbusds.jose.JOSEException;
import com.srt.todo_list.dto.request.AuthenticationRequest;
import com.srt.todo_list.dto.request.IntrospectRequest;
import com.srt.todo_list.dto.response.AuthenticationResponse;
import com.srt.todo_list.dto.response.IntrospectResponse;
import com.srt.todo_list.entity.User;
import com.srt.todo_list.exception.AppException;
import com.srt.todo_list.exception.ErrorCode;
import com.srt.todo_list.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.text.ParseException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthenticationServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AuthenticationService authenticationService;

    private AuthenticationRequest authenticationRequest;
    private AuthenticationRequest invalidPasswordRequest;
    private User user;

    @BeforeEach
    void initData() {
        authenticationService.SIGNER_KEY = "0123456789012345678901234567890123456789012345678901234567890123";
        authenticationService.VALID_DURATION = 3600;

        authenticationRequest = AuthenticationRequest.builder()
                .username("john")
                .password("password123")
                .build();
        invalidPasswordRequest = AuthenticationRequest.builder()
                .username("john")
                .password("wrong-password")
                .build();
        user = User.builder()
                .id("00f0f5d2")
                .username("john")
                .password(new BCryptPasswordEncoder(10).encode("password123"))
                .fullName("John Doe")
                .build();
    }

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void authenticate_success() {
        when(userRepository.findByUsername(authenticationRequest.getUsername()))
                .thenReturn(Optional.of(user));

        AuthenticationResponse response = authenticationService.authenticate(authenticationRequest);

        assertTrue(response.isAuthenticated());
        assertNotNull(response.getToken());
    }

    @Test
    void authenticate_whenUserNotFound_thenThrowUserNotFound() {
        when(userRepository.findByUsername(authenticationRequest.getUsername()))
                .thenReturn(Optional.empty());

        AppException exception = assertThrows(
                AppException.class,
                () -> authenticationService.authenticate(authenticationRequest)
        );

        assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void authenticate_whenPasswordInvalid_thenThrowUnauthenticated() {
        when(userRepository.findByUsername(invalidPasswordRequest.getUsername()))
                .thenReturn(Optional.of(user));

        AppException exception = assertThrows(
                AppException.class,
                () -> authenticationService.authenticate(invalidPasswordRequest)
        );

        assertEquals(ErrorCode.UNAUTHENTICATED, exception.getErrorCode());
    }

    @Test
    void getCurrentUser_success() {
        SecurityContextHolder.getContext()
                .setAuthentication(new UsernamePasswordAuthenticationToken("john", null));
        when(userRepository.findByUsername("john")).thenReturn(Optional.of(user));

        User currentUser = authenticationService.getCurrentUser();

        assertSame(user, currentUser);
    }

    @Test
    void introspect_whenTokenValid_thenReturnValidTrue() throws ParseException, JOSEException {
        String token = authenticationService.generateToken(user);
        IntrospectRequest request = IntrospectRequest.builder()
                .token(token)
                .build();

        IntrospectResponse response = authenticationService.introspect(request);

        assertTrue(response.isValid());
    }

    @Test
    void introspect_whenTokenInvalid_thenReturnValidFalse() throws ParseException, JOSEException {
        authenticationService.SIGNER_KEY = "abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijkl";
        String token = authenticationService.generateToken(user);
        IntrospectRequest request = IntrospectRequest.builder()
                .token(token)
                .build();
        authenticationService.SIGNER_KEY = "0123456789012345678901234567890123456789012345678901234567890123";

        IntrospectResponse response = authenticationService.introspect(request);

        assertFalse(response.isValid());
    }
}
