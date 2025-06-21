package com.example.gguro.service.UserService;

import com.example.gguro.domain.User;
import com.example.gguro.web.dto.UserRequestDTO;

public abstract class UserCommandService {
    public abstract User signUp(UserRequestDTO.UserSignUpDTO request);
}
