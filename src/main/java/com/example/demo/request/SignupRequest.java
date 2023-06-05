package com.example.demo.request;

import com.example.demo.model.ERole;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Data
@Getter
@Setter
public class SignupRequest {

    private String username;
    private String email;
    private List<ERole> roles;
    private String password;
    private String phoneNo;

    public List<ERole> getRoles() {
        if (roles == null) {
            return new ArrayList<>();
        }
        return roles;
    }

    public void setRoles(List<ERole> roles) {
        this.roles = roles;
    }

}
