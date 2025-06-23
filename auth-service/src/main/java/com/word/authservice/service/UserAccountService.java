package com.word.authservice.service;

import com.word.authservice.dto.LoginRequestDTO;
import com.word.authservice.dto.LoginResponseDTO;
import com.word.authservice.dto.RegistrationRequestDTO;
import com.word.authservice.dto.RegistrationResponseDTO;

public interface UserAccountService {
    RegistrationResponseDTO registerUser(RegistrationRequestDTO registrationRequestDTO);
    LoginResponseDTO loginUser(LoginRequestDTO loginRequestDTO);

    LoginResponseDTO upgradeUser(String publicId, String upgradeCode);
}
