package com.example.devkin.controllers;
import com.example.devkin.dtos.UserInfoDto;
import com.example.devkin.entities.User;
import com.example.devkin.repositories.UserRepository;
import com.example.devkin.services.AuthenticationService;
import com.example.devkin.services.ProjectService;
import com.example.devkin.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequestMapping("/users")
@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private ProjectService projectService;

    @GetMapping("/me")
    public ResponseEntity<UserInfoDto> authenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication.getPrincipal() instanceof UserDetails) {
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

    @GetMapping("/notInProject")
    public ResponseEntity<?> getUsersNotInProject(@RequestParam String projectSlug) {
        try {
            List<UserInfoDto> users = userService.getUsersNotInProject(projectService.getProjectBySlug(projectSlug));
            if (users.isEmpty()) {
                Map<String, String> response = new HashMap<>();
                response.put("message", "No results found");
                return ResponseEntity.ok(response);
            }
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving users: " + e.getMessage());
        }
    }
}
