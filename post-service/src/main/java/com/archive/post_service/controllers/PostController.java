package com.archive.post_service.controllers;


import com.archive.post_service.dto.AddNewPostDto;
import com.archive.post_service.services.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/post-service")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;
    public record errorResponse(int code) {
        public String errorMessage() {
            return switch (code) {
                case -1 -> "Missing title post";
                case -2 -> "Missing image file";
                case -3 -> "Missing content of post";
                case -4 -> "Missing id user created";
                default -> "Invalid request";
            };
        }
    }
    @PostMapping("/upload-post")
    public ResponseEntity<Object> uploadPost(@ModelAttribute AddNewPostDto addNewPostDto) {
        int response = postService.addNewPost(addNewPostDto);
        if (response == 1){
            return ResponseEntity.ok().body("Upload Post Success");
        }else {
            return ResponseEntity.badRequest().body(new errorResponse(response).errorMessage());
        }
    }
    @PutMapping("/update-post/{idPost}")
    public ResponseEntity<Object> updatePost(@PathVariable int idPost, @ModelAttribute AddNewPostDto updatePostDto) throws IOException {
        boolean updated = postService.updateNewPost(idPost, updatePostDto);
        if (updated) {
            return ResponseEntity.ok().body("Update Post Success");
        }
        return ResponseEntity.badRequest().body(new errorResponse(-1).errorMessage());
    }
    @DeleteMapping("/delete-post/{idPost}")
    public ResponseEntity<Object> deletePost(@PathVariable int idPost) throws IOException {
        boolean deleted = postService.deletePost(idPost);
        if (deleted) {
            return ResponseEntity.ok().body("Delete Post Success");
        }
        return ResponseEntity.badRequest().body("Not found post to delete");
    }
}
