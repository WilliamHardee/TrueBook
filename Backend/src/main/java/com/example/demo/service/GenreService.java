package com.example.demo.service;

import com.example.demo.model.Genre;
import com.example.demo.repository.GenreRepository;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class GenreService {

    private GenreRepository genreRepository;

    public GenreService(GenreRepository genreRepository) {
        this.genreRepository = genreRepository;
    }

    public Set<Genre> resolveGenres(List<String> genreList) {
        Set<Genre> genres = new HashSet<>();

        for (String name : genreList) {
            Genre genre = genreRepository.findByGenreName(name)
                    .orElseGet(() -> genreRepository.save(Genre.builder()
                            .genreName(name)
                            .build()));
            genres.add(genre);
        }

        return genres;
    }


}
