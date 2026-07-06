package com.srt.todo_list.controller;

import com.nimbusds.jose.JOSEException;
import com.srt.todo_list.dto.request.AuthenticationRequest;
import com.srt.todo_list.dto.request.IntrospectRequest;
import com.srt.todo_list.dto.response.APIResponse;
import com.srt.todo_list.dto.response.AuthenticationResponse;
import com.srt.todo_list.dto.response.IntrospectResponse;
import com.srt.todo_list.service.AuthenticationService;
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
@FieldDefaults(level= AccessLevel.PRIVATE, makeFinal=true)
public class AuthenticationController {
    AuthenticationService authenticationService;

    @PostMapping("/token")
    APIResponse<AuthenticationResponse> isAuthenticated (@RequestBody AuthenticationRequest authenticationRequest) {
        var result = authenticationService.authenticate(authenticationRequest);
        return APIResponse.<AuthenticationResponse>builder()
                .result(result)
                .build();
    }

    @PostMapping("/introspect")
    APIResponse<IntrospectResponse> isIntrospected (@RequestBody IntrospectRequest introspectRequest)
            throws ParseException, JOSEException {
        var result = authenticationService.introspect(introspectRequest);
        return APIResponse.<IntrospectResponse>builder()
                .result(result)
                .build();
    }
}
