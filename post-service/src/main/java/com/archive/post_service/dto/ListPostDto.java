package com.archive.post_service.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ListPostDto {
    private int idPost; 
    private String title;
    private String content;
    private String urlImage;
    private LocalDateTime createdAt;
}
