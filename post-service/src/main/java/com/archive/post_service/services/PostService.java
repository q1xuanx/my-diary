package com.archive.post_service.services;


import com.archive.post_service.dto.AddNewPostDto;
import com.archive.post_service.entites.Post;
import com.archive.post_service.repositories.PostRepository;
import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import com.cloudinary.utils.ObjectUtils;
import io.github.cdimascio.dotenv.Dotenv;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;

    public int addNewPost (AddNewPostDto addNew) {
        if (addNew.getTitle().isEmpty()) {
            return -1;
        }
        if (addNew.getImage() == null) {
            return -2;
        }
        if (addNew.getContent().isEmpty()) {
            return -3;
        }
        if (addNew.getUserCreatedId() == 0){
            return -4;
        }
        Dotenv env = Dotenv.load();
        Cloudinary cloudinary = new Cloudinary(env.get("CLOUDINARY_URL"));
        Map configUpload = ObjectUtils.asMap(
                "use_filename", true,
                "unique_filename", true,
                "overwrite", false,
                "transformation", new Transformation<>().width(400).height(400).crop("pad").fetchFormat("avif")
        );
        CompletableFuture<Map> uploaded = CompletableFuture.supplyAsync(() -> {
            try {
                Map response = cloudinary.uploader().upload(addNew.getImage().getBytes(), configUpload);
                return response;
            }catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        Map result = uploaded.join();
        LocalDateTime now = LocalDateTime.now(Clock.systemDefaultZone());
        Post post = Post.builder()
                .title(addNew.getTitle())
                .content(addNew.getContent())
                .urlImage(result.get("url").toString())
                .userCreatedId(addNew.getUserCreatedId())
                .createDate(now)
                .build();
        postRepository.save(post);
        return 1;
    }
}
