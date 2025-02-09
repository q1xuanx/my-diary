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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;

    //Upload config
    private final Map configUpload = ObjectUtils.asMap(
            "use_filename", true,
            "unique_filename", true,
            "overwrite", false,
            "transformation", new Transformation<>().width(400).height(400).crop("pad").fetchFormat("avif")
    );
    public int addNewPost (AddNewPostDto addNew) throws ExecutionException, InterruptedException, TimeoutException {
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
        Map result = uploadImage(addNew.getImage());
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
    public boolean updateNewPost(int idPost, AddNewPostDto update) throws IOException, ExecutionException, InterruptedException, TimeoutException {
        Optional<Post> postExist = postRepository.findById(idPost);
        if (postExist.isPresent()){
            Post post = postExist.get();
            if (update.getImage() != null) {
                String publicId = extractUrlToGetPublicId(post.getUrlImage());
                deleteImage(publicId);
                Map uploadNewImage = uploadImage(update.getImage());
                post.setUrlImage(uploadNewImage.get("url").toString());
            }
            post.setContent(update.getContent());
            post.setTitle(update.getTitle());
            post.setUserCreatedId(update.getUserCreatedId());
            postRepository.save(post);
            return true;
        }
        return false;
    }
    public boolean deletePost(int postId) throws IOException {
        Optional<Post> postExist = postRepository.findById(postId);
        if (postExist.isPresent()){
            Post post = postExist.get();
            String publicId = extractUrlToGetPublicId(post.getUrlImage());
            deleteImage(publicId);
            postRepository.delete(post);
            return true;
        }
        return false;
    }
    public void deleteImage(String publicId) throws IOException {
        Dotenv env = Dotenv.load();
        Cloudinary cloudinary = new Cloudinary(env.get("CLOUDINARY_URL"));
        cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
    }
    public Map uploadImage(MultipartFile file) throws ExecutionException, InterruptedException, TimeoutException {
        Dotenv env = Dotenv.load();
        Cloudinary cloudinary = new Cloudinary(env.get("CLOUDINARY_URL"));
        CompletableFuture<Map> uploaded = CompletableFuture.supplyAsync(() -> {
            try {
                return cloudinary.uploader().upload(file.getBytes(), configUpload);
            }catch (Exception e) {
                throw new IllegalStateException("Failed to upload image", e);
            }
        });
        return uploaded.get(10, TimeUnit.SECONDS);
    }
    public String extractUrlToGetPublicId(String urlImage){
        String[] getPublicId = urlImage.split("//")[1].split("/");
        return getPublicId[getPublicId.length - 1].split(".avif")[0];
    }
}
