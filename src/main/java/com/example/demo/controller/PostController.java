package com.example.demo.controller;

import com.example.demo.model.Post;
import com.example.demo.model.PostStatus;
import com.example.demo.repository.PostRepository;
import com.example.demo.repository.SearchRepository;
import com.example.demo.request.PostRequest;
import com.example.demo.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController // to make this is a controller
@RequestMapping("/api/posts")
public class PostController {

    @Autowired
    SearchRepository searchRepository;

    @Autowired
    PostRepository postRepository;
    @Autowired
    private PostService postService;

    //create
    @PostMapping("/post")
    public ResponseEntity<Post> createPost(@RequestBody PostRequest postRequest) {
        Post createdPost = postService.createPost(postRequest);
        return new ResponseEntity<>(createdPost, HttpStatus.OK);
    }
    //getAll
    @GetMapping("")
    @CrossOrigin
    public List<Post> getAllPosts()
    {

        return postRepository.findAll();
    }

    // posts/java (search in frontend)
    @GetMapping("/posts/{text}")
    @CrossOrigin
    public List<Post> search(@PathVariable String text)
    {
        return searchRepository.findByText(text);
    }

    //getPostById
    @GetMapping("/getPostById/{id}")
    @CrossOrigin
    public ResponseEntity<Post> getPostsById(@PathVariable String id)
    {
        Optional<Post> postData = postRepository.findById(id);

        if(postData.isPresent()){
            return new ResponseEntity<>(postData.get(), HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    //updatePostById
    @PutMapping("/updatePostById/{id}")
    @CrossOrigin
    public ResponseEntity<Post> updatePostById(@PathVariable String id, @RequestBody Post newPostData)
    {
        Optional<Post> oldPostData = postRepository.findById(id);

        if(oldPostData.isPresent()){
            Post updatedPostData = oldPostData.get();
            updatedPostData.setTitle(newPostData.getTitle());
            updatedPostData.setContent(newPostData.getContent());

            Post createdPost = postRepository.save(updatedPostData);
            return new ResponseEntity<>(createdPost, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    //deletePostById
    @DeleteMapping("/deletePostById/{id}")
    @CrossOrigin
    public ResponseEntity<HttpStatus> deletePostById(@PathVariable String id)
    {
        postRepository.deleteById(id);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    //acceptPostById
    @PutMapping("/acceptPostById/{id}")
    @CrossOrigin
    public ResponseEntity<Post> acceptPostById(@PathVariable String id)
    {
        Optional<Post> postData = postRepository.findById(id);

        if(postData.isPresent()){
            Post updatedPostData = postData.get();
            updatedPostData.setStatus(PostStatus.ACCEPTED);

            Post createdPost = postRepository.save(updatedPostData);
            return new ResponseEntity<>(createdPost, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    //rejectPostById
    @PutMapping("/rejectPostById/{id}")
    @CrossOrigin
    public ResponseEntity<Post> rejectPostById(@PathVariable String id)
    {
        Optional<Post> postData = postRepository.findById(id);

        if(postData.isPresent()){
            Post updatedPostData = postData.get();
            updatedPostData.setStatus(PostStatus.REJECTED);

            Post createdPost = postRepository.save(updatedPostData);
            return new ResponseEntity<>(createdPost, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    //deletePostById
    @DeleteMapping("/adminDeletePostById/{id}")
    @CrossOrigin
    public ResponseEntity<HttpStatus> adminDeletePostById(@PathVariable String id)
    {
        Optional<Post> postData = postRepository.findById(id);

        if(postData.isPresent()){
            Post postToDelete = postData.get();

            // check if user has admin privileges
            // you can get the current user from the authentication object
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String loggedInUsername = auth.getName();

            if(loggedInUsername.equals(postToDelete.getUsername()) || auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))){
                postRepository.deleteById(id);
                return new ResponseEntity<>(HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
        }

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }



}
