package com.example.demo.response;

import lombok.Data;

import java.util.List;

//@Component
//@AllArgsConstructor (5 constructors are already defined)
//@NoArgsConstructor
@Data
public class UserResponse {

    private String token;
    private String type = "Bearer";
    private String username;
    private String email;
    private List<String> roles;
    private String phoneNo;

    private String id;



    /* used for sign in */
    public UserResponse(String token, String id, String username, String email, String phoneNo, List<String> roles) {
        this.token = token;
        this.id = id;
        this.username = username;
        this.email = email;
        this.roles = roles;
        this.phoneNo = phoneNo;
    }

    /* used for sign up */
    public UserResponse(String id, String username, String email, String phoneNo) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.phoneNo = phoneNo;
    }


}




