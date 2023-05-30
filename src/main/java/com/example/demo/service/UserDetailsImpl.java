package com.example.demo.service;

import com.example.demo.model.ERole;
//import com.example.demo.model.Role;
import com.example.demo.model.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class UserDetailsImpl implements UserDetails {

//    private static final long serialVersionUID = 1L;

    private String id;

    private String username;

    private String email;

    private String phoneNo;
    @JsonIgnore
    private String password;

//    private Role name;

    private Collection<? extends GrantedAuthority> authorities;

    public UserDetailsImpl(String id, String username, String email, String password, String phoneNo,
                           Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.phoneNo = phoneNo;
        this.authorities = authorities;
    }

    public static UserDetailsImpl build(User user) {
        List<GrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.toString()))
                .collect(Collectors.toList());

        return new UserDetailsImpl(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getPassword(),
                user.getPhoneNo(),
                authorities);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    //    @Override
    public String getPhoneNo() {
        return phoneNo;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        UserDetailsImpl user = (UserDetailsImpl) o;
        return Objects.equals(id, user.id);
    }

//    public Role getRole() {
//
//        return name;
//
//    }
}



/*
This code is defining a custom implementation of the Spring Security UserDetails interface, which represents the
principal object (the user) that is authenticated and authorized in a Spring Security-enabled application.

The UserDetailsImpl class implements the UserDetails interface and contains the following fields:

id: The unique identifier of the user.
username: The username of the user.
email: The email address of the user.
phoneNo: The phone number of the user.
password: The password of the user (which is annotated with @JsonIgnore to prevent it from being serialized to JSON).
authorities: A collection of GrantedAuthority objects that represent the roles and permissions assigned to the user.
The class also has a constructor that takes these fields as arguments, and a static method named build that takes a
User object and creates a new UserDetailsImpl instance from it.

The class implements several methods required by the UserDetails interface, such as getAuthorities(), getPassword(),
getUsername(), and others. These methods provide information about the user's account and determine whether the account
is enabled, expired, or locked.

The class also overrides the equals() method to compare UserDetailsImpl objects by their ID field.

 */