package com.example.devkin.services;
import com.example.devkin.dtos.UserInfoDto;
import com.example.devkin.entities.User;
import com.example.devkin.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public UserDetails loadUserByUsername(String userEmail){
        return userRepository.findByEmail(userEmail).get();
    }

    @Transactional(readOnly = true)
    public List<User> allUsers() {
        List<User> users = new ArrayList<>();

        userRepository.findAll().forEach(users::add);

        return users;
    }

    @Transactional(readOnly = true)
    public List<UserInfoDto> getUsersNotInProject(Integer projectId) {
        List<User> users = userRepository.findUsersNotInProject(projectId);

        // Map users to UserInfoDto
        return users.stream()
                .map(user -> new UserInfoDto(user.getName(), user.getEmail()))
                .collect(Collectors.toList());
    }

}