package com.example.demo.controller;

//import java.io.IOException;
import java.util.*;
//import java.util.stream.Collectors;
import com.example.demo.model.ERole;
import com.example.demo.model.User;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.request.LoginRequest;
import com.example.demo.request.SignupRequest;
import com.example.demo.request.UserRequest;
import com.example.demo.response.UserResponse;
//import com.example.demo.service.UserDetailsServiceImpl;
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

//import javax.servlet.http.HttpServletResponse;

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
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtUtils.generateJwtToken(authentication);
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

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
        userRepository.save(user); // Save the new user to the database

        return ResponseEntity.ok(new UserResponse(user.getId(), null, user.getUsername(),
                user.getEmail(), user.getRoles(), user.getPhoneNo()));
    }

    @PutMapping("/update-profile/{userId}")
    public ResponseEntity<?> updateUserProfile(@PathVariable String userId, @RequestBody UserRequest userRequest) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (!(authentication.getPrincipal() instanceof UserDetailsImpl)) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized to update the profile");
            }

            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

            // Only allow the authenticated user to update their own profile
            if (!userId.equals(userDetails.getId())) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized to update the profile");
            }

            // Validate input data
            if (userRequest.getUsername() != null && !userRequest.getUsername().isEmpty()) {
                user.setUsername(userRequest.getUsername());
            }
            if (userRequest.getEmail() != null && !userRequest.getEmail().isEmpty()) {
                // Perform email validation if required
                user.setEmail(userRequest.getEmail());
            }
            if (userRequest.getPhoneNo() != null && !userRequest.getPhoneNo().isEmpty()) {
                user.setPhoneNo(userRequest.getPhoneNo());
            }

            User updatedUser = userRepository.save(user); // Save the updated user details

            // Create a new response object with the updated user details
            UserResponse response = new UserResponse(updatedUser.getId(), null, updatedUser.getUsername(),
                    updatedUser.getEmail(), updatedUser.getRoles(), updatedUser.getPhoneNo());

            return ResponseEntity.ok(response);
        } catch (ResponseStatusException e) {
            throw e; // Rethrow the existing response status exceptions
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error updating user details", e);
        }
    }




}
