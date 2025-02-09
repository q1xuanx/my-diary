package com.archive.post_service.repositories;


import com.archive.post_service.dto.ListPostDto;
import com.archive.post_service.entites.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Integer> {
    List<Post> getAllByUserCreatedId(int userId);
}
