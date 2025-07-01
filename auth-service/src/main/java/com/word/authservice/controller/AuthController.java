package com.word.authservice.controller;

import com.word.authservice.dto.*;
import com.word.authservice.service.UserAccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Auth", description = "Auth API")
public class AuthController {
    private final UserAccountService userAccountService;

    public AuthController(UserAccountService userAccountService) {
        this.userAccountService = userAccountService;
    }

    @GetMapping("")
    @Operation(summary = "Welcome to MVP auth service api!")
    public String welcome() {return "Welcome to MVP auth service api!";}

    @PostMapping("/register")
    @Operation(summary = "Register a new user")
    public ResponseEntity<RegistrationResponseDTO> registerUser(
            @Valid @RequestBody RegistrationRequestDTO registrationRequestDTO) {
        System.out.println(registrationRequestDTO);
        RegistrationResponseDTO response = userAccountService.registerUser(registrationRequestDTO);
        return ResponseEntity.status(201).body(response);
    }
    @PostMapping("/login")
    @Operation(summary = "Login a user")
    public ResponseEntity<LoginResponseDTO> loginUser(
            @Valid @RequestBody LoginRequestDTO loginRequestDTO) {
        System.out.println("hit auth service login endpoint");
        LoginResponseDTO response = userAccountService.loginUser(loginRequestDTO);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/get-member")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get membership")
    public ResponseEntity<LoginResponseDTO> getMembership(
            @RequestBody GetMembershipRequestDTO request,
            @AuthenticationPrincipal Jwt jwt
    ) {
        LoginResponseDTO response = userAccountService
                .upgradeUser(jwt.getSubject(), request.getUpgradeCode());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/profile")
    @Operation(summary = "Verify login")
    public String test() {return "you are logged in";}

    @GetMapping("/user")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    @Operation(summary = "Verify login as a user")
    public String userAuthorized() {return "you are logged in as a user";}


    @GetMapping("/member")
    @PreAuthorize("hasAuthority('ROLE_MEMBER')")
    @Operation(summary = "Verify login as a member")
    public String memberAuthorized() {return "you are logged in as a MEMBER";}
}
