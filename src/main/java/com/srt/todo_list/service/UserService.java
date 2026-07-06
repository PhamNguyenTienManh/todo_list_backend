package com.srt.todo_list.service;

import com.srt.todo_list.dto.request.CreateUserRequest;
import com.srt.todo_list.dto.response.UserResponse;
import com.srt.todo_list.entity.User;
import com.srt.todo_list.exception.AppException;
import com.srt.todo_list.exception.ErrorCode;
import com.srt.todo_list.mapper.UserMapper;
import com.srt.todo_list.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService {
    UserRepository userRepository;
    PasswordEncoder passwordEncoder;
    UserMapper userMapper;

    public UserResponse createUser (CreateUserRequest createUserRequest){
        User user = userMapper.toUser(createUserRequest);
        user.setPassword(passwordEncoder.encode(createUserRequest.getPassword()));
        try{
            user = userRepository.save(user);
        }catch (Exception e){
            throw new AppException(ErrorCode.USER_EXISTED);
        }

        return userMapper.toUserResponse(user);
    }
}
