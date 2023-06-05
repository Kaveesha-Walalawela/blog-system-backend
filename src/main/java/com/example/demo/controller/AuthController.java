package com.example.demo.controller;

import java.util.*;
import com.example.demo.model.ERole;
import com.example.demo.model.Post;
import com.example.demo.model.User;
import com.example.demo.repository.PostRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.request.LoginRequest;
import com.example.demo.request.ResetPasswordRequest;
import com.example.demo.request.SignupRequest;
import com.example.demo.response.LoginResponse;
import com.example.demo.response.UserResponse;
import com.example.demo.utils.JwtUtils;
import com.example.demo.service.UserDetailsImpl;
import com.example.demo.utils.PasswordUtils;
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

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("api/auth")
public class AuthController {
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    private PostRepository postRepository;

    @PostMapping("/signin")
    public LoginResponse authenticateUser(@RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtUtils.generateJwtToken(authentication);
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

            //get user from DB
            Optional<User> user = userRepository.findByUsername(loginRequest.getUsername());

            //set user roles to a list from user retrieved from DB
            List<ERole> role = user.get().getRoles();

            //create a new LoginResponse object with the generated token and role list
            LoginResponse loginResponse = new LoginResponse();
            loginResponse.setToken(jwt);
            loginResponse.setRoles(role);
            loginResponse.setId(user.get().getId());
            loginResponse.setUsername(user.get().getUsername());
            loginResponse.setEmail(user.get().getEmail());
            loginResponse.setPhoneNo(user.get().getPhoneNo());
            return loginResponse;

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
        user.setRoles(List.of(ERole.ROLE_USER));
        user.setPhoneNo(signUpRequest.getPhoneNo());
        user.setPassword(encoder.encode(signUpRequest.getPassword()));
        userRepository.save(user); // Save the new user to the database

        return ResponseEntity.ok(new UserResponse(user.getId(), user.getUsername(),
                user.getEmail(), user.getRoles(), user.getPhoneNo()));
    }


    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userRepository.findAll();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @GetMapping("/{userid}")
    public ResponseEntity<User> getUserById(@PathVariable("id") String id) {
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            return new ResponseEntity<>(user, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/updateProfileByUserId/{id}")
    public ResponseEntity<User> updateUserById(@PathVariable("id") String id, @RequestBody User updatedUser) {
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setUsername(updatedUser.getUsername());
            user.setEmail(updatedUser.getEmail());
            user.setRoles(updatedUser.getRoles());
            user.setPhoneNo(updatedUser.getPhoneNo());
            user.setWarnings(updatedUser.getWarnings());
            User savedUser = userRepository.save(user);
            return new ResponseEntity<>(savedUser, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/adminDeleteUserById/{id}")
    public ResponseEntity<HttpStatus> deleteUserById(@PathVariable("id") String id) {
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isPresent()) {
            userRepository.deleteById(id);
            String userName = userOptional.get().getUsername();
            List<Post> userPosts = postRepository.findByUsername(userName);
            postRepository.deleteAll(userPosts);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/adminWarningUser/{id}")
    public ResponseEntity<User> warningUserById(@PathVariable("id") String id) {
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isPresent()) {

            User user = userOptional.get();
            int currentWarnings = user.getWarnings();
            System.out.println("currentWarningsStr" + currentWarnings);
            //  int currentWarnings = Integer.parseInt(currentWarningsStr);
            // System.out.println("currentWarnings"+currentWarnings);
            user.setWarnings(currentWarnings + 1);
            User savedUser = userRepository.save(user);
            System.out.println("user" + user);
            return new ResponseEntity<>(savedUser, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/userWarnings")
    public ResponseEntity<List<Map<String, Object>>> getUserWarnings() {
        List<User> users = userRepository.findAll();
        List<Map<String, Object>> userWarnings = new ArrayList<>();

        for (User user : users) {
            Map<String, Object> userData = new HashMap<>();
            userData.put("username", user.getUsername());
            userData.put("warnings", user.getWarnings());
            userWarnings.add(userData);
        }

        return ResponseEntity.ok(userWarnings);
    }

    @GetMapping("/userWarningsCount/{username}")
    public ResponseEntity<Integer> getUserWarningsCount(@PathVariable("username") String username) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            int warningsCount = user.getWarnings();
            return ResponseEntity.ok(warningsCount);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }


    //for users who has more than 5 warnings

    @DeleteMapping("/adminDeleteUserByUsername/{username}")
    public ResponseEntity<HttpStatus> deleteUserByUsername(@PathVariable("username") String username) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isPresent()) {
            User user = userOptional.get();

            // Delete all the posts associated with the user
            List<Post> userPosts = postRepository.findByUsername(username);
            postRepository.deleteAll(userPosts);

            userRepository.delete(user); // Delete the user

            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }



    @CrossOrigin(origins = "http://localhost:4200")
    @PutMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordRequest resetPasswordRequest) {
        Optional<User> userOptional = userRepository.findByUsername(resetPasswordRequest.getUsername());
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            // Hash the new password
            String hashedNewPassword = PasswordUtils.hashPassword(resetPasswordRequest.getNewPassword());

            // Set the new hashed password for the user
            user.setPassword(hashedNewPassword);
            userRepository.save(user);

            return ResponseEntity.ok("Password reset successful");
        } else {
            return ResponseEntity.badRequest().body("User not found");
        }
    }

}