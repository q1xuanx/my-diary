package com.archive.user_service.dto;


import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreatUserDto {
    private String userName;
    private String password;
    private String fullName;
}
