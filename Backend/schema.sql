CREATE EXTENSION IF NOT EXISTS vector;

CREATE TABLE book (
    book_id SERIAL PRIMARY KEY,
    title TEXT NOT NULL,
    author VARCHAR(100) NOT NULL,
    total_chapters INTEGER NOT NULL,
    summary TEXT,        
    cover_url TEXT,
    visit_count INTEGER DEFAULT 0, 
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);


CREATE TABLE book_chunk (
    id SERIAL PRIMARY KEY,
    book_id INTEGER NOT NULL REFERENCES book(book_id) ON DELETE CASCADE,    
    chapter_number INTEGER NOT NULL, 
    content TEXT NOT NULL,           
    embedding vector(768),           
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);


CREATE TABLE users ( 
    user_id SERIAL PRIMARY KEY,
    email VARCHAR(50) NOT NULL UNIQUE,
    username VARCHAR(50) NOT NULL,
    password VARCHAR(255),
    source VARCHAR(10) NOT NULL
);

CREATE TABLE review (
    review_id SERIAL PRIMARY KEY,
    book_id INTEGER NOT NULL REFERENCES book(book_id) ON DELETE CASCADE,
    reviewer_id INTEGER REFERENCES users(user_id),
    reviewer_name VARCHAR(50),
    score INTEGER NOT NULL CHECK (score >= 1 AND score <= 5), 
    review TEXT,
    source VARCHAR(10), 
    CONSTRAINT external_internal_review 
    CHECK (
        (source IS NOT NULL) OR (reviewer_id IS NOT NULL)
    )
);

CREATE TABLE user_library (
    user_id INTEGER REFERENCES users(user_id) ON DELETE CASCADE,
    book_id INTEGER REFERENCES book(book_id) ON DELETE CASCADE,
    status VARCHAR(20),
    PRIMARY KEY (user_id, book_id) 
);

CREATE TABLE genre (
    genre_id SERIAL PRIMARY KEY,
    genre_name VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE book_to_genre (
    genre_id INTEGER REFERENCES genre(genre_id) ON DELETE CASCADE,
    book_id INTEGER REFERENCES book(book_id) ON DELETE CASCADE,
    PRIMARY KEY(genre_id, book_id)
);