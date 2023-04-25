package com.example.demo.service;

import com.example.demo.controller.AuthController;
import com.example.demo.exception.BlogException;
import com.example.demo.model.Post;
import com.example.demo.model.PostStatus;
import com.example.demo.model.User;
import com.example.demo.repository.PostRepository;
import com.example.demo.request.PostRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class PostService {
    @Autowired
    private PostRepository postRepository;

    @Autowired
    private AuthController authController;

    public Post createPost(PostRequest postRequest) {
//        User currentUser = authController.getCurrentUser().orElseThrow(() ->
//                new BlogException("No User Found"));
        Post blogPost = new Post();
        blogPost.setTitle(postRequest.getTitle());
        blogPost.setContent(postRequest.getContent());
//        blogPost.setUsername(currentUser.getUsername());
        blogPost.setStatus(PostStatus.PENDING);
        return postRepository.save(blogPost);
    }
}