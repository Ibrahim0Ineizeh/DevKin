package com.example.devkin.services;
import com.example.devkin.dtos.LoginUserDto;
import com.example.devkin.dtos.RegisterUserDto;
import com.example.devkin.entities.User;
import com.example.devkin.repositories.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    public AuthenticationService(
            UserRepository userRepository,
            AuthenticationManager authenticationManager,
            PasswordEncoder passwordEncoder
    ) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User signup(RegisterUserDto input) {
        User user = new User();
        user.setName(input.getName());
        user.setEmail(input.getEmail());
        user.setPassword(passwordEncoder.encode(input.getPassword()));

        return userRepository.save(user);
    }

    public User authenticate(LoginUserDto input) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        input.getEmail(),
                        input.getPassword()
                )
        );

        return userRepository.findByEmail(input.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public User authenticateOAuth2User(OAuth2User oAuth2User) {
        String githubEmail = oAuth2User.getAttribute("email");
        String githubUsername = oAuth2User.getAttribute("login");

        return userRepository.findByEmail(githubEmail)
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setEmail(githubEmail);
                    newUser.setName(githubUsername);
                    newUser.setPassword("");
                    return userRepository.save(newUser);
                });
    }
}
