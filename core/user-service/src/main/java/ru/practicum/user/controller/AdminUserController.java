package ru.practicum.user.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.api.dto.user.NewUserRequest;
import ru.practicum.api.dto.user.UserDto;
import ru.practicum.user.param.AdminUserParam;
import ru.practicum.user.service.UserService;

import java.util.List;

@RestController
@RequestMapping(path = "/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final UserService userService;
    private final String userPath = "/{user-id}";

    @GetMapping
    public List<UserDto> getPageUsers(@RequestParam(required = false) List<Long> ids,
                                      @RequestParam(defaultValue = "0") int from,
                                      @RequestParam(defaultValue = "10") int size) {
        AdminUserParam param = AdminUserParam.builder()
                .ids(ids)
                .from(from)
                .size(size)
                .build();
        return userService.getPageUsers(param);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto createUser(@Valid @RequestBody NewUserRequest user) {
        return userService.createUser(user);
    }

    @DeleteMapping(userPath)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeUser(@PathVariable(name = "user-id") long userId) {
        userService.removeUser(userId);
    }
}