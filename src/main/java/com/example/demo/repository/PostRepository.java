package com.example.demo.repository;

import com.example.demo.model.Post;
import com.example.demo.model.PostStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends MongoRepository<Post, String> {
    List<Post> findByUsername(String username);
    List<Post> findByStatus(PostStatus status);
}

//Post, which type of data I need to call. Mapping part
//String, basically the primary key