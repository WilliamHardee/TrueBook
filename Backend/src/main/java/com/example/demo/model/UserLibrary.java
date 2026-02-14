package com.example.demo.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_library")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserLibrary {

    @EmbeddedId
    private UserLibraryId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("bookId")
    @JoinColumn(name = "book_id")
    private Book book;

    @Column(length = 20)
    private String status;
}