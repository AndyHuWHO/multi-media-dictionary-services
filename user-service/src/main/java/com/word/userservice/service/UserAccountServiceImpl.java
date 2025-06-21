package com.word.userservice.service;

import com.word.userservice.dto.LoginRequestDTO;
import com.word.userservice.dto.LoginResponseDTO;
import com.word.userservice.dto.RegistrationRequestDTO;
import com.word.userservice.dto.RegistrationResponseDTO;
import com.word.userservice.exception.EmailAlreadyExistsException;
import com.word.userservice.exception.InvalidCredentialsException;
import com.word.userservice.model.AuthProvider;
import com.word.userservice.model.UserAccount;
import com.word.userservice.model.UserRole;
import com.word.userservice.repository.UserAccountRepository;
import com.word.userservice.security.JwtUtil;
import com.word.userservice.security.TokenService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserAccountServiceImpl implements UserAccountService {
    private final UserAccountRepository userAccountRepository;
    private final PasswordEncoder passwordEncoder;
//    private final JwtUtil jwtUtil;
    private final TokenService tokenService;

    public UserAccountServiceImpl(UserAccountRepository userAccountRepository, PasswordEncoder passwordEncoder, TokenService tokenService) {
        this.userAccountRepository = userAccountRepository;
        this.passwordEncoder = passwordEncoder;
//        this.jwtUtil = jwtUtil;
        this.tokenService = tokenService;
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
}
