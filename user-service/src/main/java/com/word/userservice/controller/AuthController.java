package com.word.userservice.controller;

import com.word.userservice.dto.LoginRequestDTO;
import com.word.userservice.dto.LoginResponseDTO;
import com.word.userservice.dto.RegistrationRequestDTO;
import com.word.userservice.dto.RegistrationResponseDTO;
import com.word.userservice.service.UserAccountService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class AuthController {
    private final UserAccountService userAccountService;

    public AuthController(UserAccountService userAccountService) {
        this.userAccountService = userAccountService;
    }
    @GetMapping("")
    public String welcome() {return "Welcome to MVP auth service api!";}

    @PostMapping("/register")
    public ResponseEntity<RegistrationResponseDTO> registerUser(
            @Valid @RequestBody RegistrationRequestDTO registrationRequestDTO) {
        System.out.println(registrationRequestDTO);
        RegistrationResponseDTO response = userAccountService.registerUser(registrationRequestDTO);
        return ResponseEntity.status(201).body(response);
    }
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> loginUser(
            @Valid @RequestBody LoginRequestDTO loginRequestDTO) {
        LoginResponseDTO response = userAccountService.loginUser(loginRequestDTO);
        return ResponseEntity.ok(response);
    }
}
