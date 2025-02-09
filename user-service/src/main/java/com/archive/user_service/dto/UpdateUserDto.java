package com.archive.user_service.dto;


import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateUserDto {
    private int idUser;
    private String password;
    private String fullName;
}
