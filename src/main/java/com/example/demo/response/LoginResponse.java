package com.example.demo.response;

import com.example.demo.model.ERole;
import lombok.Data;

import java.util.List;

@Data
public class LoginResponse {
    String token;
    List<ERole> roles;
    private String id;

    private String username;
    private String email;
    private String phoneNo;


}
