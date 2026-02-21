package com.example.demo.service;


import com.example.demo.DTO.CreateBookDTO;
import com.example.demo.model.Book;
import com.example.demo.model.Review;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.exc.JsonNodeException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class HardcoverService {

    private RestClient hardcoverApiClient;


    public HardcoverService(@Value("${hardcover.api.key}") String hardcoverApiKey) {
        this.hardcoverApiClient = RestClient.builder()
                .requestFactory(new HttpComponentsClientHttpRequestFactory())
                .baseUrl("https://api.hardcover.app/v1/graphql")
                .defaultHeader("authorization", hardcoverApiKey)
                .build();
    }

    public Book createAndMapBook(CreateBookDTO createBookDTO, JsonNode hardcoverResponse) {
        Book book = parseBookDetails(hardcoverResponse, createBookDTO);
        List<Review> reviewList = parseBookReviews(hardcoverResponse);
        book.setReviews(reviewList);
        reviewList.forEach((review -> review.setBook(book)));

        return book;

    }

    private Book parseBookDetails(JsonNode hardcoverResponse, CreateBookDTO createBookDTO) throws JsonNodeException {

        Book book = Book.builder()
                .title(createBookDTO.title())
                .author(createBookDTO.author())
                .totalChapters(createBookDTO.totalChapters())
                .rating(hardcoverResponse.path("rating").asFloat())
                .reviewCount(hardcoverResponse.path("ratings_count").asInt())
                .coverUrl(hardcoverResponse.path("image").path("url").asString())
                .summary(hardcoverResponse.path("description").asString())
                .build();

        return book;
    }

    private List<Review> parseBookReviews(JsonNode hardcoverResponse) {
        JsonNode reviews = hardcoverResponse.path("user_books");
        List<Review> reviewsList = new ArrayList<>();

        for(JsonNode reviewNode : reviews) {
            Review review = Review.builder()
                    .review(reviewNode.path("review_raw").asString())
                    .rating(reviewNode.path("rating").asInt())
                    .reviewerName(reviewNode.path("user").path("username").asString())
                    .imageUrl(reviewNode.path("user").path("image").path("url").asString(""))
                    .source("HARDCOVER")
                    .build();

            reviewsList.add(review);
        }

        return  reviewsList;
    }

    public List<String> parseGenreNames(JsonNode hardcoverResponse) {
        JsonNode genres = hardcoverResponse.path("cached_tags").path("Genre");
        List<String> genreList = new ArrayList<>();

        for(JsonNode genre : genres) {
            genreList.add(genre.path("tag").asString());
        }

        return genreList;
    }


    public JsonNode getApiResponse(String bookTitle) {
        String query = """
        query GetEditionsFromTitle($bookName: String!) {
            books(
                where: {title: {_eq: $bookName}}
                order_by: {ratings_count: desc}
                limit: 1
            ) {
                id
                title
                description
                pages
                release_date
                ratings_count
                rating
                cached_tags
                image {
                    url
                }
                user_books(where: {has_review: {_eq: true}}) {
                    review_raw
                    rating
                    reviewed_at
                    user {
                        username
                        image {
                            url
                        }
                    }
                }
            }
        }
        """;

        Map<String, Object> variables = Map.of(
                "bookName", bookTitle
        );

        Map<String, Object> body = Map.of(
                "query", query,
                "variables", variables
        );

        JsonNode response = hardcoverApiClient.post()
                .body(body)
                .retrieve()
                .body(JsonNode.class);


        return response.path("data").path("books").get(0);
    }

}
