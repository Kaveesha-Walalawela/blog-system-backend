package com.example.demo.repository;


import java.util.List;
import com.example.demo.model.Post;

public interface SearchRepository {
    List<Post> findByText(String text);
}
