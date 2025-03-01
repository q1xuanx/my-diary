package com.archive.user_service.controllers;

import com.archive.user_service.dto.CreatUserDto;
import com.archive.user_service.dto.LoginDto;
import com.archive.user_service.dto.PostDto;
import com.archive.user_service.dto.UpdateUserDto;
import com.archive.user_service.entities.DiaryUser;
import com.archive.user_service.services.DiaryUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.io.IOException;

@Controller
@RequiredArgsConstructor
@RequestMapping("/user-service")
public class DiaryUserController {
    private final DiaryUserService diaryUserService;
    public record errorMessage(int code) {
        public String printError(){
            return switch(code) {
                case -1 -> "Username is empty || wrong username or password";
                case -2 -> "Password is empty";
                case -3 -> "Fullname is empty";
                case -4 -> "Username exist";
                default -> "Valid request";
            };
        }
    }
    @PostMapping("/create")
    public ResponseEntity<Object> createDiaryUser(@RequestBody CreatUserDto diaryUser) {
        int status = diaryUserService.createUser(diaryUser);
        if (status == 1){
            return ResponseEntity.status(HttpStatus.CREATED).body(diaryUser);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new errorMessage(status).printError());
    }
    @PostMapping("/login")
    public ResponseEntity<Object> loginToDiary(@RequestBody LoginDto loginDto) {
        DiaryUser valid = diaryUserService.loginUser(loginDto);
        if (valid.getIdUser() != -1){
            return ResponseEntity.status(HttpStatus.OK).body(valid);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(valid);
    }
    @PutMapping("/update")
    public ResponseEntity<Object> updateUser(@RequestBody UpdateUserDto updateUserDto){
        int status = diaryUserService.updateUser(updateUserDto);
        if (status == 1){
            return ResponseEntity.status(HttpStatus.OK).body(updateUserDto);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new errorMessage(status).printError());
    }
    @PostMapping("/upload-diary")
    public ResponseEntity<Object> uploadDiary(@ModelAttribute PostDto postDto)  {
        Mono<String> status = diaryUserService.createPost(postDto);
        String result = status.block();
        if (result != null && result.equals("Upload Post Success")){
            return ResponseEntity.status(HttpStatus.CREATED).body("Upload successful");
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(status);
    }
    @PutMapping("/update-diary/{idPost}")
    public ResponseEntity<Object> updateDiary(@PathVariable int idPost, @ModelAttribute PostDto update){
        Mono<String> status = diaryUserService.updatePost(idPost, update);
        String result = status.block();
        if (result != null && result.equals("Update Post Success")){
            return ResponseEntity.status(HttpStatus.OK).body(update);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(status);
    }
    @DeleteMapping("/delete-post/{idPost}/{idUser}")
    public ResponseEntity<Object> deletePost(@PathVariable int idPost, @PathVariable int idUser){
        Mono<String> status = diaryUserService.deletePost(idPost, idUser);
        String result = status.block();
        if (result != null && result.equals("Delete Post Success")){
            return ResponseEntity.status(HttpStatus.OK).body("Delete successful");
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(status);
    }
}
