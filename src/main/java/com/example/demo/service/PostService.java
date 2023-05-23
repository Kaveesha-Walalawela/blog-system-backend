package com.example.demo.service;

import com.example.demo.controller.AuthController;
import com.example.demo.model.Post;
import com.example.demo.model.PostStatus;
import com.example.demo.model.User;
import com.example.demo.repository.PostRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.request.PostRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PostService {
    @Autowired
    private PostRepository postRepository;

    @Autowired
    private AuthController authController;

    @Autowired
    private UserRepository userRepository;



    public Post createPost(PostRequest postRequest, String userId) {

        User currentUser = userRepository.findByUsername(postRequest.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Invalid user ID"));

        Post blogPost = new Post();
        blogPost.setTitle(postRequest.getTitle());
        blogPost.setContent(postRequest.getContent());
        blogPost.setUsername(currentUser.getUsername());
        blogPost.setStatus(PostStatus.PENDING);
        return postRepository.save(blogPost);
    }

    public Post createDraftPost(PostRequest postRequest, String userId) {
        User currentUser = userRepository.findByUsername(postRequest.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Invalid user ID"));

        Post blogPost = new Post();
        blogPost.setTitle(postRequest.getTitle());
        blogPost.setContent(postRequest.getContent());
        blogPost.setUsername(currentUser.getUsername());
        blogPost.setStatus(PostStatus.DRAFT); // Set the status as "DRAFT"
        return postRepository.save(blogPost);
    }

    public Post sharePost(String id) {
        Optional<Post> postData = postRepository.findById(id);

        if (postData.isPresent()) {
            Post updatedPostData = postData.get();
            updatedPostData.setStatus(PostStatus.PENDING);
            return postRepository.save(updatedPostData);
        }

        throw new IllegalArgumentException("Invalid post ID");
    }


}