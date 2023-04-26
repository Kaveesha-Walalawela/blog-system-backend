package com.example.demo.controller;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import com.example.demo.model.ERole;
import com.example.demo.model.User;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.request.LoginRequest;
import com.example.demo.request.SignupRequest;
import com.example.demo.response.UserResponse;
import com.example.demo.service.UserDetailsServiceImpl;
import com.example.demo.utils.JwtUtils;
import com.example.demo.service.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletResponse;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("api/auth")
public class AuthController {
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) { //ResponseEntity is a build in class
        try {

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtUtils.generateJwtToken(authentication); // token generate
//            console.log(jwt);
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
//            List<String> roles = userDetails.getAuthorities().stream()
//                    .map(item -> item.getAuthority())
//                    .collect(Collectors.toList());

            return ResponseEntity.ok(new UserResponse(userDetails.getId(),
                    jwt,
                    userDetails.getUsername(),
                    userDetails.getEmail(),
//                    userDetails.getRole(),
                    null,
                    userDetails.getPhoneNo()));

        } catch (AuthenticationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username or password", e);
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestBody SignupRequest signUpRequest) {
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity
                    .badRequest()
                    .body("Error: Username is already taken!");
        }
        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body("Error: Email is already in use!");
        }

        // Create new user's account
        /*AuthService part*/
        List<ERole> roleArray = new ArrayList<>();
        for (ERole str : signUpRequest.getRoles()) {
            roleArray.add(str);
        }
        User user = new User();
        user.setUsername(signUpRequest.getUsername());
        user.setEmail(signUpRequest.getEmail());
        user.setRoles(roleArray);
        user.setPhoneNo(signUpRequest.getPhoneNo());
        user.setPassword(encoder.encode(signUpRequest.getPassword()));
//        UserDetailsImpl registerRequest;
        userRepository.save(user);

//        List<String> roleNames = user.getRoles().stream()
//                .map(role -> role.toString())
//                .collect(Collectors.toList());

        return ResponseEntity.ok(new UserResponse(user.getId(), null, user.getUsername(),
                user.getEmail(), user.getRoles(), user.getPhoneNo()));

    }

    /*Create getCurrentUser*/

//    public Optional<User> getCurrentUser() {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        System.out.println(authentication.getPrincipal());
//        if (authentication == null || !authentication.isAuthenticated()) {
//            return Optional.empty();
//        }
//        return Optional.of((User) authentication.getPrincipal());
//    }

}