package com.archive.user_service.services;


import com.archive.user_service.dto.CreatUserDto;
import com.archive.user_service.dto.LoginDto;
import com.archive.user_service.dto.PostDto;
import com.archive.user_service.dto.UpdateUserDto;
import com.archive.user_service.entities.DiaryUser;
import com.archive.user_service.repositories.DiaryUserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DiaryUserService {
    private final DiaryUserRepository diaryUserRepository;
    private final WebClient.Builder webClient;
    private final String url = "http://localhost:8082";
    private final Logger log = LoggerFactory.getLogger(DiaryUserService.class);

    public int createUser(CreatUserDto creatUserDto){
        if (creatUserDto.getUserName().isEmpty()){
            return -1;
        }
        if (creatUserDto.getPassword().isEmpty()){
            return -2;
        }
        if (creatUserDto.getFullName().isEmpty()){
            return -3;
        }
        boolean isExistUsername = diaryUserRepository.existsByUsername(creatUserDto.getUserName());
        if (isExistUsername){
            return -4;
        }
        DiaryUser diaryUser = DiaryUser.builder()
                .username(creatUserDto.getUserName())
                .password(creatUserDto.getPassword())
                .fullName(creatUserDto.getFullName())
                .build();
        diaryUserRepository.save(diaryUser);
        return 1;
    }
    public int loginUser(LoginDto loginDto){
        DiaryUser diaryUser = diaryUserRepository.findByUsernameAndPassword(loginDto.getUserName(), loginDto.getPassword());
        if (diaryUser == null){
            return -1;
        }
        return diaryUser.getIdUser();
    }
    public int updateUser(UpdateUserDto updateUser){
        Optional<DiaryUser> findDiaryUser = diaryUserRepository.findById(updateUser.getIdUser());
        if (findDiaryUser.isPresent()){
            DiaryUser diaryUser = findDiaryUser.get();
            if (updateUser.getFullName().isEmpty()){
                return -3;
            }
            if (updateUser.getPassword().isEmpty()){
                return -2;
            }
            diaryUser.setFullName(updateUser.getFullName());
            diaryUser.setPassword(updateUser.getPassword());
            diaryUserRepository.save(diaryUser);
            return 1;
        }
        return -1;
    }
    public Mono<Optional<DiaryUser>> checkValidUser(int idUser){
        return Mono.fromCallable(() -> diaryUserRepository.findById(idUser))
                .subscribeOn(Schedulers.boundedElastic());
    }
    public Mono<String> createPost(PostDto post) {
        return checkValidUser(post.getUserCreatedId())
                .flatMap(optionalUser -> {
                    if (optionalUser.isEmpty()) {
                        return Mono.just("Not found user");
                    }
                    MultipartBodyBuilder builder = new MultipartBodyBuilder();
                    builder.part("title", post.getTitle());
                    builder.part("content", post.getContent());
                    builder.part("image", post.getImage().getResource());
                    builder.part("userCreatedId", post.getUserCreatedId());
                    return webClient.baseUrl(url).build()
                            .post()
                            .uri("/post-service/upload-post")
                            .contentType(MediaType.MULTIPART_FORM_DATA)
                            .body(BodyInserters.fromMultipartData(builder.build()))
                            .retrieve()
                            .bodyToMono(String.class)
                            .timeout(Duration.ofSeconds(15))
                            .onErrorResume(error -> {
                                log.info(error.getMessage());
                                return Mono.error(error);
                            });
                });
    }

    public Mono<String> updatePost(int idPost, PostDto post) {
        return checkValidUser(idPost)
                .flatMap(optional -> {
                    if (optional.isEmpty()){
                        return Mono.just("Not found user");
                    }
                    MultipartBodyBuilder builder = new MultipartBodyBuilder();
                    builder.part("title", post.getTitle());
                    builder.part("content", post.getContent());
                    builder.part("image", post.getImage().getResource());
                    builder.part("userCreatedId", post.getUserCreatedId());
                    return webClient.baseUrl(url).build()
                            .put()
                            .uri("/post-service/update-post/{idPost}", idPost)
                            .body(BodyInserters.fromMultipartData(builder.build()))
                            .retrieve()
                            .bodyToMono(String.class)
                            .timeout(Duration.ofSeconds(15))
                            .onErrorResume(error -> {
                                log.info(error.getMessage());
                                return Mono.error(error);
                            });
                });
    }
    public Mono<String> deletePost (int idPost, int idUser){
        return checkValidUser(idUser)
                .flatMap(optional -> {
                    if (optional.isEmpty()){
                        return Mono.just("Not found user");
                    }
                    return webClient.baseUrl(url).build()
                            .delete()
                            .uri("/post-service/delete-post/{idPost}/{idUser}", idPost, idUser)
                            .retrieve()
                            .bodyToMono(String.class);
                });
    }
}
