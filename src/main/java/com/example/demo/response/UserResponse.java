package com.example.demo.response;

import com.example.demo.model.ERole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserResponse {

    private String id;
//   private String token;
    private String username;
    private String email;
    private List<ERole> roles;
    private String phoneNo;

}