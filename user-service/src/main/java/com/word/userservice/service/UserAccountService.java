package com.word.userservice.service;

import com.word.userservice.dto.LoginRequestDTO;
import com.word.userservice.dto.LoginResponseDTO;
import com.word.userservice.dto.RegistrationRequestDTO;
import com.word.userservice.dto.RegistrationResponseDTO;

public interface UserAccountService {
    RegistrationResponseDTO registerUser(RegistrationRequestDTO registrationRequestDTO);
    LoginResponseDTO loginUser(LoginRequestDTO loginRequestDTO);

}
