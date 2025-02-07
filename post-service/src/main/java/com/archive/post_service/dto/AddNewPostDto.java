package com.archive.post_service.dto;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AddNewPostDto {
    private String title;
    private String content;
    private MultipartFile image;
    private int userCreatedId;
}
