package com.example.devkin.controllers;
import com.example.devkin.entities.User;
import com.example.devkin.repositories.UserRepository;
import com.example.devkin.services.AuthenticationService;
import com.example.devkin.services.UserService;
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
    private final UserService userService;
    private final UserRepository userRepository;
    private final AuthenticationService authenticationService;

    public UserController(UserService userService,
                          UserRepository userRepository,
                          AuthenticationService authenticationService) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.authenticationService = authenticationService;
    }

    @GetMapping("/me")
    public ResponseEntity<User> authenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication.getPrincipal() instanceof UserDetails) {
            // This is a regular user authenticated with email/password
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            User currentUser = userRepository.findByEmail(userDetails.getUsername())
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));
            return ResponseEntity.ok(currentUser);
        } else if (authentication.getPrincipal() instanceof OAuth2User) {
            // This is an OAuth2 user (GitHub in this case)
            OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
            User currentUser = authenticationService.authenticateOAuth2User(oAuth2User);
            return ResponseEntity.ok(currentUser);
        } else {
            throw new UsernameNotFoundException("Authenticated principal is not recognized.");
        }
    }
}
