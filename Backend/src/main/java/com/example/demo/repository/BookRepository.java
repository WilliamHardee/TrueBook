package com.example.demo.repository;

import com.example.demo.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface BookRepository extends JpaRepository<Book, Integer> {

    @Query(value = "SELECT * FROM Book ORDER BY review_count DESC LIMIT :limit", nativeQuery = true)
    List<Book> findNMostPopularBooks(@Param("limit") Integer limit);

    @Query(value = "SELECT * FROM Book WHERE title LIKE %:bookTitle% LIMIT 50", nativeQuery = true)
    List<Book> searchBooksByTitle(@Param("bookTitle") String bookTitle);
}
