package com.archive.user_service.repositories;

import com.archive.user_service.entities.DiaryUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DiaryUserRepository extends JpaRepository<DiaryUser, Integer> {
    DiaryUser findByUsernameAndPassword(String username, String password);
    boolean existsByUsername(String username);
}
