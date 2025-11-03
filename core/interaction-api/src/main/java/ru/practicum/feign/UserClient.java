package ru.practicum.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.practicum.dto.user.UserDto;
import ru.practicum.dto.user.UserShortDto;

@FeignClient(value = "user-service", path = "/admin/users")
public interface UserClient {

    @GetMapping("/{user-id}/exist")
    void isExistUser(@PathVariable(name = "user-id") long userId);

    @GetMapping("/{user-id}")
    UserDto getUserById(@PathVariable(name = "user-id") long userId);

    @GetMapping("/{user-id}/short")
    UserShortDto getUserShortById(@PathVariable(name = "user-id") long userId);

}
