package com.example.demo.controller;

import com.example.demo.model.Post;
import com.example.demo.model.PostStatus;
import com.example.demo.model.User;
import com.example.demo.repository.PostRepository;
import com.example.demo.repository.SearchRepository;
import com.example.demo.repository.UserRepository;
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
@RestController
@RequestMapping("/api/posts")
public class PostController {

    @Autowired
    SearchRepository searchRepository;

    @Autowired
    PostRepository postRepository;

    @Autowired
    private PostService postService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/post")
    public ResponseEntity<Post> createPost(@RequestBody PostRequest postRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String loggedInUsername = authentication.getName();
        System.out.println(authentication);

        String userId = loggedInUsername;


        Post createdPost = postService.createPost(postRequest, userId);
        return new ResponseEntity<>(createdPost, HttpStatus.OK);
    }
    @GetMapping("")
    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }

    @GetMapping("/posts/{text}")
    public List<Post> search(@PathVariable String text) {
        return searchRepository.findByText(text);
    }


    @GetMapping("/getPostByUsername/{username}")
    public ResponseEntity<List<Post>> getPostsByUsername(@PathVariable String username) {
        List<Post> posts = postRepository.findByUsername(username);

        if (!posts.isEmpty()) {
            return new ResponseEntity<>(posts, HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }



    @GetMapping("/getPostById/{id}")
    public ResponseEntity<Post> getPostsById(@PathVariable String id) {
        Optional<Post> postData = postRepository.findById(id);

        if (postData.isPresent()) {
            Post post = postData.get();
            return new ResponseEntity<>(post, HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PutMapping("/updatePostById/{id}")
    public ResponseEntity<Post> updatePostById(@PathVariable String id, @RequestBody Post newPostData) {
        Optional<Post> oldPostData = postRepository.findById(id);

        if (oldPostData.isPresent()) {
            Post updatedPostData = oldPostData.get();
            updatedPostData.setTitle(newPostData.getTitle());
            updatedPostData.setContent(newPostData.getContent());

            Post createdPost = postRepository.save(updatedPostData);
            return new ResponseEntity<>(createdPost, HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/deletePostById/{id}")
    public ResponseEntity<HttpStatus> deletePostById(@PathVariable String id) {
        postRepository.deleteById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/acceptPostById/{id}")
    public ResponseEntity<Post> acceptPostById(@PathVariable String id) {
        Optional<Post> postData = postRepository.findById(id);

        if (postData.isPresent()) {
            Post updatedPostData = postData.get();
            updatedPostData.setStatus(PostStatus.ACCEPTED);

            Post createdPost = postRepository.save(updatedPostData);
            return new ResponseEntity<>(createdPost, HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PutMapping("/rejectPostById/{id}")
    public ResponseEntity<Post> rejectPostById(@PathVariable String id) {
        Optional<Post> postData = postRepository.findById(id);

        if (postData.isPresent()) {
            Post updatedPostData = postData.get();
            updatedPostData.setStatus(PostStatus.REJECTED);

            Post createdPost = postRepository.save(updatedPostData);
            return new ResponseEntity<>(createdPost, HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/adminDeletePostById/{id}")
    public ResponseEntity<HttpStatus> adminDeletePostById(@PathVariable String id) {
        Optional<Post> postData = postRepository.findById(id);

        if (postData.isPresent()) {
            Post postToDelete = postData.get();

            // Check if the user has admin privileges
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String loggedInUsername = auth.getName();

            if (loggedInUsername.equals(postToDelete.getUsername()) ||
                    auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {

                postRepository.deleteById(id);
                return new ResponseEntity<>(HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
        }

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
