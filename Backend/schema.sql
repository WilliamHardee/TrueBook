CREATE EXTENSION IF NOT EXISTS vector;

CREATE TABLE book_chunks (
    id SERIAL PRIMARY KEY,
    book_id VARCHAR(50) NOT NULL,    
    chapter_number INTEGER NOT NULL, 
    content TEXT NOT NULL,           
    embedding vector(768),           
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE books (
    book_id VARCHAR(50) PRIMARY KEY,
    title TEXT NOT NULL,
    author VARCHAR(100) NOT NULL,
    total_chapters INTEGER NOT NULL,
    summary TEXT,                    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);