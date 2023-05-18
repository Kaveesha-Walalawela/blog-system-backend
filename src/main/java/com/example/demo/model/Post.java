package com.example.demo.model;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table
@Getter
@Setter
@Data
@Document(collection = "posts") // database collection
@NoArgsConstructor
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id; //Primary KEY
    @NotBlank
    @Column
    private String title;
    //    @Lob  //Specifies that a persistent property or field should be persisted as a large object to a database-supported large object type.
//    @Column
//    @NotEmpty
//    private String content;
    @Column
    private Instant createdOn;
    @Column
    private Instant updatedOn;
    @Column
    @NotBlank
    private String username;

    @Column
    private String userId;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private PostStatus status;

    @Lob
    @Column
    @NotEmpty
    private String content = String.valueOf(new ArrayList<>());
    //private String posts = new ArrayList<>();

    public Post(String id, String title, String content, Instant updatedOn, Instant createdOn,
                String username, PostStatus status, String userId) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.createdOn = createdOn;
        this.updatedOn = updatedOn;
        this.username = username;
        this.status = status;
        this.userId = userId;
    }
    public void setStatus(PostStatus status) {
        this.status = status;
    }

    public PostStatus getStatus() {
        return this.status;
    }
}
