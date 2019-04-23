package com.example.demo.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.entiteis.Post;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    @Query(value = "select post from Post post where post.content like %:keyword%")
    List<Post> findByContent(@Param("keyword") String keyword);

}
