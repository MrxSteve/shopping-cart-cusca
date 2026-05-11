package com.prueba.auth_service.mapper;

import com.prueba.auth_service.dto.UserResponse;
import com.prueba.auth_service.entity.Role;
import com.prueba.auth_service.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.stream.Collectors;

@Mapper(componentModel = "spring", imports = {Role.class, Collectors.class})
public interface UserMapper {

    @Mapping(
        target = "roles",
        expression = "java(user.getRoles().stream().map(Role::getName).collect(Collectors.toSet()))"
    )
    UserResponse toUserResponse(User user);
}
