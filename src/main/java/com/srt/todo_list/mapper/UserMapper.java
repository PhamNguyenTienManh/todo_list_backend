package com.srt.todo_list.mapper;

import com.srt.todo_list.dto.request.CreateUserRequest;
import com.srt.todo_list.dto.response.UserResponse;
import com.srt.todo_list.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toUser(CreateUserRequest createUserRequest);
    UserResponse toUserResponse(User user);

}
