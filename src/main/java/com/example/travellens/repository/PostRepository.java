package com.example.travellens.repository;

import com.example.travellens.entity.Post;
import com.example.travellens.entity.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    @EntityGraph(attributePaths = "user")
    List<Post> findAllByOrderByCreatedAtDesc();

    @EntityGraph(attributePaths = "user")
    List<Post> findByUserOrderByCreatedAtDesc(User user);

    @Query("SELECT p FROM Post p LEFT JOIN FETCH p.user WHERE p.id = :id")
    Optional<Post> findByIdWithUser(@Param("id") Long id);

    @Query("SELECT p FROM Post p LEFT JOIN FETCH p.user WHERE " +
           "(:keyword IS NULL OR :keyword = '' OR " +
           "LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(p.location) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(p.category) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "AND (:category IS NULL OR :category = '' OR :category = 'All Categories' OR p.category = :category) " +
           "ORDER BY p.createdAt DESC")
    List<Post> searchPosts(@Param("keyword") String keyword, @Param("category") String category);

}
