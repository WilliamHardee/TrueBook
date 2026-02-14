# epub2vector.py
from ebooklib import epub
from dotenv import load_dotenv, find_dotenv
from bs4 import BeautifulSoup
from langchain_text_splitters import RecursiveCharacterTextSplitter
from google import genai
from google.genai import types
from psycopg2.extras import execute_values
import ebooklib
import psycopg2
import sys
import os
import time

def get_db_connection():
    return psycopg2.connect(
        database=os.getenv("DB_NAME", "true_book_db"),
        user=os.getenv("DB_USER", "postgres"),
        password=os.getenv("DB_PASS", "postgres"),
        host=os.getenv("DB_HOST", "localhost"),
        port=os.getenv("DB_PORT", "5432")
    )


def delete_existing_book(cur, title: str):
    cur.execute("SELECT book_id FROM books WHERE title = %s", (title,))
    book2Delete = cur.fetchone()
    if book2Delete:
        cur.execute("DELETE FROM books WHERE title = %s", (title,))
        cur.execute("DELETE FROM book_chunks WHERE book_id = %s", (book2Delete[0],))


def insert_book_metadata(cur, title: str, author: str, summary: str, total_chapters: int):
    cur.execute(
        "INSERT INTO books (title, author, total_chapters, summary, visit_count) VALUES (%s, %s, %s, %s, 0)",
        (title, author, total_chapters, summary)
    )
    cur.execute("SELECT book_id FROM books WHERE title = %s", (title,))
    return cur.fetchone()[0]


def insert_chunks(cur, book_id: int, chapter_num: int, chunks: list, embeddings: list):
    data_to_insert = [
        (book_id, chapter_num, chunk, emb.values)
        for chunk, emb in zip(chunks, embeddings)
    ]
    execute_values(cur,
                   "INSERT INTO book_chunks (book_id, chapter_number, content, embedding) VALUES %s",
                   data_to_insert)


def extract_book_metadata(book):
    title = book.get_metadata("DC", "title")[0][0]
    author = book.get_metadata("DC", "creator")[0][0]
    desc = book.get_metadata("DC", "description")[0][0]
    print(book.get_metadata("DC", "identifier"))
    return title, author, desc


def extract_chapters(book):
    items = list(book.get_items_of_type(ebooklib.ITEM_DOCUMENT))
    chapters = [item for item in items if "chapter" in item.get_name()]
    return chapters


def clean_html(html_content: str):
    soup = BeautifulSoup(html_content, "html.parser")
    return soup.get_text()


def build_text_splitter():
    return RecursiveCharacterTextSplitter.from_tiktoken_encoder(
        encoding_name="cl100k_base",
        chunk_size=800,
        chunk_overlap=150,
        separators=["\n\n", "\n", ". ", " ", ""]
    )


def build_embedding_client():
    return genai.Client(api_key=os.getenv("GEMINI_API_KEY"))


def embed_chunks(client, chunks: list):
    embedded = client.models.embed_content(
        model="gemini-embedding-001",
        contents=chunks,
        config=types.EmbedContentConfig(output_dimensionality=768)
    )
    return embedded.embeddings

def main(epub_path: str):
    load_dotenv(find_dotenv())

    connection = get_db_connection()
    cur = connection.cursor()

    book = epub.read_epub(epub_path)
    book_title, book_author, book_desc = extract_book_metadata(book)

    print(f"Ingesting: {book_title} by {book_author}")

    delete_existing_book(cur, book_title)
    connection.commit()

    chapters = extract_chapters(book)
    book_id = insert_book_metadata(cur, book_title, book_author, book_desc, len(chapters))
    connection.commit()

    text_splitter = build_text_splitter()
    client = build_embedding_client()

    for chapter_num, chapter in enumerate(chapters, start=1):
        clean_text = clean_html(chapter.get_body_content())
        split_chunks = text_splitter.split_text(clean_text)
        embeddings = embed_chunks(client, split_chunks)

        insert_chunks(cur, book_id, chapter_num, split_chunks, embeddings)
        connection.commit()
        print(f"Chapter {chapter_num}: indexed {len(split_chunks)} chunks")
        time.sleep(20)  

    cur.close()
    connection.close()
    print("Ingestion Complete")

if __name__ == "__main__":
    if len(sys.argv) != 2:
        print("Usage: python epub2vector.py <path_to_epub>")
        sys.exit(1)

    main(sys.argv[1])
