package com.udemySpringCourse.app.ws.service;

import com.udemySpringCourse.app.ws.shared.dto.UserDto;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService extends UserDetailsService {
    UserDto createUser(UserDto user);
    UserDto getUser(String email);
    UserDto updateUser(String id, UserDto user);
    UserDto getUserByUserId(String id);
    void deleteUser(String id);
    List<UserDto> getUsers(int page, int limit);
}
