package ru.practicum.api.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ru.practicum.api.dto.user.UserDto;

import java.util.List;

@FeignClient("user-service")
public interface UserOperations {

    String apiUsersPath = "/api/users";

    @PostMapping(apiUsersPath)
    List<UserDto> getUsers(@RequestBody List<Long> ids);

    @GetMapping(apiUsersPath + "/{user-id}/exists")
    void isExistsUser(@PathVariable(name = "user-id") long userId);

    @GetMapping(apiUsersPath + "/{user-id}")
    UserDto getUserById(@PathVariable(name = "user-id") long userId);
}
