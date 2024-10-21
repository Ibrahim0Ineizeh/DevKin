package com.example.devkin.controllers;
import com.example.devkin.dtos.UserInfoDto;
import com.example.devkin.entities.User;
import com.example.devkin.repositories.UserRepository;
import com.example.devkin.services.AuthenticationService;
import com.example.devkin.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("/users")
@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthenticationService authenticationService;

    @GetMapping("/me")
    public ResponseEntity<UserInfoDto> authenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication.getPrincipal() instanceof UserDetails) {
            // This is a regular user authenticated with email/password
            User user = (User) authentication.getPrincipal();
            User currentUser = userRepository.findByEmail(user.getUsername())
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));
            UserInfoDto userInfoDto = new UserInfoDto();
            userInfoDto.setEmail(currentUser.getEmail());
            userInfoDto.setName(currentUser.getName());
            return ResponseEntity.ok(userInfoDto);
        } else if (authentication.getPrincipal() instanceof OAuth2User) {
            // This is an OAuth2 user (GitHub in this case)
            OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
            User currentUser = authenticationService.authenticateOAuth2User(oAuth2User);
            UserInfoDto userInfoDto = new UserInfoDto();
            userInfoDto.setEmail(currentUser.getEmail());
            userInfoDto.setName(currentUser.getName());
            return ResponseEntity.ok(userInfoDto);
        } else {
            throw new UsernameNotFoundException("Authenticated principal is not recognized.");
        }
    }
}
