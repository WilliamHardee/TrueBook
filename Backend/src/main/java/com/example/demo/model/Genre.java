package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name="genre")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Genre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="genre_id")
    private Integer genreId;

    @Column(name = "genre_name", nullable = false, length = 50, unique = true)
    private String genreName;

    @ManyToMany(mappedBy = "genres")
    @JsonIgnore
    private Set<Book> books = new HashSet<>();
}
