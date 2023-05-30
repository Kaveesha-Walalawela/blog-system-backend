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



    /* used for sign in */
//    public UserResponse(String token, String id, String username, String email, String phoneNo, List<String> roles) {
//        this.token = token;
//        this.id = id;
//        this.username = username;
//        this.email = email;
//        this.roles = roles;
//        this.phoneNo = phoneNo;
//    }

    /* used for sign up */
//    public UserResponse(String id, String username, String email, String phoneNo) {
//        this.id = id;
//        this.username = username;
//        this.email = email;
//        this.phoneNo = phoneNo;
//    }

}