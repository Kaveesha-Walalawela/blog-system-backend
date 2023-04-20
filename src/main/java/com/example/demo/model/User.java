package com.example.demo.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table
@Getter
@Setter
@Data
@Document(collection = "users")
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id; //Primary KEY
    @NotBlank
    @Column
    private String username;
    @NotBlank
    @Email
    @Column
    private String email;
    @NotBlank
    @Column
    private String password;
    @NotBlank
    @Column
    private String phoneNo;

//    @DBRef
    private List<ERole> roles = new ArrayList<>();
    public User(String username, String email, String password, List<ERole> roles, String phoneNo) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.roles = roles;
        this.phoneNo = phoneNo;
    }
}

