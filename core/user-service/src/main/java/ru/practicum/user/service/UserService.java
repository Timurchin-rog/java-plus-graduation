package ru.practicum.user.service;

import ru.practicum.api.dto.user.NewUserRequest;
import ru.practicum.api.dto.user.UserDto;
import ru.practicum.user.param.AdminUserParam;

import java.util.List;

public interface UserService {
    List<UserDto> getPageUsers(AdminUserParam param);

    List<UserDto> getUsers(List<Long> ids);

    void isExistUser(long userId);

    UserDto getUserById(long userId);

    UserDto createUser(NewUserRequest user);

    void removeUser(long userId);
}
