package com.archive.post_service.controllers;

import com.archive.post_service.dto.AddNewPostDto;
import com.archive.post_service.services.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;


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
                case -5 -> "Update post fail, error occur try again later";
                default -> "Invalid request";
            };
        }
    }
    @GetMapping("/get-list/{idUser}")
    public ResponseEntity<Object> getListPost (@PathVariable int idUser) {
        return ResponseEntity.ok().body(postService.getAllPostById(idUser));
    }
    @PostMapping("/upload-post")
    public ResponseEntity<Object> uploadPost(@ModelAttribute AddNewPostDto addNewPostDto) throws ExecutionException, InterruptedException, TimeoutException {
        int response = postService.addNewPost(addNewPostDto);
        if (response == 1){
            return ResponseEntity.status(HttpStatus.CREATED).body("Upload Post Success");
        }else {
            return ResponseEntity.badRequest().body(new errorResponse(response).errorMessage());
        }
    }
    @PutMapping("/update-post/{idPost}")
    public ResponseEntity<Object> updatePost(@PathVariable int idPost, @ModelAttribute AddNewPostDto updatePostDto) throws IOException, ExecutionException, InterruptedException, TimeoutException {
        boolean updated = postService.updateNewPost(idPost, updatePostDto);
        if (updated) {
            return ResponseEntity.ok().body("Update Post Success");
        }
        return ResponseEntity.badRequest().body(new errorResponse(-5).errorMessage());
    }
    @DeleteMapping("/delete-post/{idPost}/{idUserDelete}")
    public ResponseEntity<Object> deletePost(@PathVariable int idPost, @PathVariable int idUserDelete) throws IOException {
        boolean deleted = postService.deletePost(idPost, idUserDelete);
        if (deleted) {
            return ResponseEntity.ok().body("Delete Post Success");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not found post to delete | you can't delete this post");
    }
}
