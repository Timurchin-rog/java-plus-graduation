package ru.practicum.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.api.dto.user.UserDto;
import ru.practicum.api.feign.UserOperations;
import ru.practicum.user.service.UserService;

import java.util.List;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class ApiUserController implements UserOperations {

    private final UserService userService;

    @Override
    public List<UserDto> getUsers(@RequestBody List<Long> ids) {
        return userService.getUsers(ids);
    }

    @Override
    public void isExistsUser(@PathVariable(name = "user-id") long userId) {
        userService.isExistUser(userId);
    }

    @Override
    public UserDto getUserById(@PathVariable(name = "user-id") long userId) {
        return userService.getUserById(userId);
    }
}
