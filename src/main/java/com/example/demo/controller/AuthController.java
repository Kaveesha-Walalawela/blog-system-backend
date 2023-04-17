package com.example.demo.controller;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.example.demo.model.ERole;
import com.example.demo.model.Role;
import com.example.demo.model.User;

import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.request.LoginRequest;
import com.example.demo.request.SignupRequest;
import com.example.demo.response.JwtResponse;
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
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

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
        try{

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtUtils.generateJwtToken(authentication);

            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            List<String> roles = userDetails.getAuthorities().stream()
                    .map(item -> item.getAuthority())
                    .collect(Collectors.toList());

            return ResponseEntity.ok(new JwtResponse(jwt,
                    userDetails.getId(),
                    userDetails.getUsername(),
                    userDetails.getEmail(),
                    userDetails.getPhoneNo(),
                    roles));

        }
        catch(AuthenticationException e){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username or password", e);
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestBody SignupRequest signUpRequest) {
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity
                    .badRequest()
                    .body("Error: Username is already taken!");
//            .body(new MessageResponse("Error: Username is already taken!"));
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body("Error: Email is already in use!");
//                .body(new MessageResponse("Error: Email is already in use!"));
        }

        // Create new user's account
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


        /* Overall, this code is responsible for mapping the list of role names provided in the SignupRequest object
        to a set of Role objects that can be assigned to the new User object. It does this by using the roleRepository
        to find the Role objects with the correct names, and adding them to the set of roles for the new User object.
         */

//        List<ERole> strRoles = signUpRequest.getRoles();
//        Set<Role> roles = new HashSet<>();
//
//        if (strRoles == null) {
////            //TODO: ENUM Validation
//        Role userRole = roleRepository.findByName(String.valueOf(ERole.ROLE_USER))
//                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
//            roles.add(userRole);
//        } else {
//            strRoles.forEach(role -> {
//                switch (role) {
//                    case ROLE_ADMIN:
//                        Role adminRole = roleRepository.findByName(String.valueOf(ERole.ROLE_ADMIN))
//                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
//                        roles.add(adminRole);
//                        break;
//
//                    default:
//                        Role userRole = roleRepository.findByName(String.valueOf(ERole.ROLE_USER))
//                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
//                        roles.add(userRole);
//                }
//            });
//        }


        userRepository.save(user);

        List<String> roleNames = user.getRoles().stream()
                .map(role -> role.toString())
                .collect(Collectors.toList());

        return ResponseEntity.ok(new JwtResponse(
                user.getId(),
                user.getPassword(),
                user.getUsername(),
                user.getEmail(),
                user.getPhoneNo(),
                roleNames));


        /*Use this for Post Service*/
//    public Optional<User> getCurrentUser() {
//        Object principal =  SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//        if (principal instanceof User) {
//            return userRepository.findByUsername(((User) principal).getUsername());
//        }
//        //Throw exception
//        .orElseThrow(() -> new UserNotFoundException("User not found in repository"));
//    } else {
//        throw new UserNotFoundException("User not authenticated");
//    }

    }

}
