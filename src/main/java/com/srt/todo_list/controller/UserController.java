package com.srt.todo_list.controller;

import com.srt.todo_list.dto.request.CreateUserRequest;
import com.srt.todo_list.dto.response.APIResponse;
import com.srt.todo_list.dto.response.UserResponse;
import com.srt.todo_list.service.UserService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level= AccessLevel.PRIVATE, makeFinal=true)
public class UserController {
    UserService userService;

    @PostMapping
    public APIResponse<UserResponse> createUser (@RequestBody @Valid CreateUserRequest request) {
        APIResponse<UserResponse> apiResponse = new APIResponse<>();
        apiResponse.setResult(userService.createUser(request));
        return apiResponse;
    }

}
