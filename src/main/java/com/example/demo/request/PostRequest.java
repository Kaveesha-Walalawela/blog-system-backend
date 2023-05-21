package com.example.demo.request;

import com.example.demo.model.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PostRequest {
    private String id;
    private String content;
    private String title;
    private String username;

    private String status;

    public String getStatus() {
        return status;
    }


}
