package ru.practicum.user.service;

import ru.practicum.dto.user.NewUserRequest;
import ru.practicum.dto.user.UserDto;
import ru.practicum.dto.user.UserShortDto;
import ru.practicum.user.param.AdminUserParam;

import java.util.List;

public interface UserService {
    List<UserDto> getUsers(AdminUserParam param);

    void isExistUser(long userId);

    UserDto getUserById(long userId);

    UserShortDto getUserShortById(long userId);

    UserDto createUser(NewUserRequest user);

    void removeUser(long userId);
}
