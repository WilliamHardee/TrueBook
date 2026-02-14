package com.example.demo.repository;

import com.example.demo.model.UserLibrary;
import com.example.demo.model.UserLibraryId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserLibraryRepository extends JpaRepository<UserLibrary, UserLibraryId> {
}
