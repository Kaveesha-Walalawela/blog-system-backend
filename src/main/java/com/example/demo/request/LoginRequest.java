package com.example.demo.request;

import lombok.Data;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotBlank;

//@Component
@Data
public class LoginRequest {
    @NotBlank
    private String username;
    @NotBlank
    private String password;


}