package com.archive.post_service.controllers;


import com.archive.post_service.dto.AddNewPostDto;
import com.archive.post_service.services.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
    public ResponseEntity<Object> uploadPost(@RequestParam("title") String title, @RequestParam("content") String content, @RequestParam("image") MultipartFile file, @RequestParam("userCreatedId") int userCreatedId) {
        AddNewPostDto addNewPostDto = new AddNewPostDto(title, content, file, userCreatedId);
        int response = postService.addNewPost(addNewPostDto);
        if (response == 1){
            return ResponseEntity.ok().body("Upload Post Success");
        }else {
            return ResponseEntity.badRequest().body(new errorResponse(response).errorMessage());
        }
    }
}
