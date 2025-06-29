package com.word.authservice.service;

import com.word.authservice.dto.*;
import com.word.authservice.exception.EmailAlreadyExistsException;
import com.word.authservice.exception.InvalidCredentialsException;
import com.word.authservice.exception.ResourceNotFoundException;
import com.word.authservice.model.AuthProvider;
import com.word.authservice.model.UserAccount;
import com.word.authservice.model.UserRole;
import com.word.authservice.repository.UserAccountRepository;
import com.word.authservice.security.TokenService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@Service
public class UserAccountServiceImpl implements UserAccountService {
    private final UserAccountRepository userAccountRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;
    private final RestTemplate restTemplate;

    @Value("${user.service.url}")
    private String userServiceUrl;

    public UserAccountServiceImpl(UserAccountRepository userAccountRepository,
                                  PasswordEncoder passwordEncoder,
                                  TokenService tokenService, RestTemplate restTemplate) {
        this.userAccountRepository = userAccountRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
        this.restTemplate = restTemplate;
    }

    @Override
    public RegistrationResponseDTO registerUser (RegistrationRequestDTO registrationRequestDTO) {
        if (userAccountRepository.existsByEmail(registrationRequestDTO.getEmail())) {
            throw new EmailAlreadyExistsException("Email already registered");
        }
        String encodedPassword = passwordEncoder.encode(registrationRequestDTO.getPassword());

        UserAccount userAccount = UserAccount.builder()
                .publicId(UUID.randomUUID().toString())
                .email(registrationRequestDTO.getEmail())
                .password(encodedPassword)
                .authProvider(AuthProvider.REGISTRATION)
                .role(UserRole.USER)
                .build();

        UserAccount savedUserAccount = userAccountRepository.save(userAccount);


        // 3. Call User Service to create profile (LOCAL URL)
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Auth-UserId", savedUserAccount.getPublicId());

        ResponseEntity<UserProfileResponseDTO> response = restTemplate.exchange(
                userServiceUrl + "/api/user/profile", // Update port as needed!
                HttpMethod.POST,
                new HttpEntity<>(headers),
                UserProfileResponseDTO.class
        );

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Failed to create user profile!");
        }
        return RegistrationResponseDTO.builder()
                .publicId(savedUserAccount.getPublicId())
                .email(savedUserAccount.getEmail())
                .role(savedUserAccount.getRole())
                .createdAt(savedUserAccount.getCreatedAt())
                .build();
    }

    @Override
    public LoginResponseDTO loginUser(LoginRequestDTO loginRequestDTO) {
        UserAccount user = userAccountRepository.findByEmail(loginRequestDTO.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));

        if (!passwordEncoder.matches(loginRequestDTO.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        String token = tokenService.generateToken(user);

        return LoginResponseDTO.builder()
                .publicId(user.getPublicId())
                .email(user.getEmail())
                .role(user.getRole())
                .token(token)
                .build();
    }

    @Override
    public LoginResponseDTO upgradeUser(String publicId, String upgradeCode) {
        final String correctCode = "VIP123";

        if (!correctCode.equals(upgradeCode)) {
            throw new InvalidCredentialsException("Invalid upgrade code");
        }

        UserAccount user = userAccountRepository.findByPublicId(publicId)
                .orElseThrow(() -> new ResourceNotFoundException("User id not found"));

        if (user.getRole() == UserRole.MEMBER) {
            throw new IllegalStateException("User is already a member");
        }

        user.setRole(UserRole.MEMBER);
        userAccountRepository.save(user);

        String newToken = tokenService.generateToken(user);

        return LoginResponseDTO.builder()
                .publicId(user.getPublicId())
                .email(user.getEmail())
                .role(user.getRole())
                .token(newToken)
                .build();
    }
}
