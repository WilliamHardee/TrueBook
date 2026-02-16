# epub2vector.py
from ebooklib import epub
from dotenv import load_dotenv, find_dotenv
from bs4 import BeautifulSoup
from langchain_text_splitters import RecursiveCharacterTextSplitter
from google import genai
from google.genai import types
from psycopg2.extras import execute_values
from pathlib import Path
import ebooklib
import psycopg2
import sys
import os
import time
import requests


def get_db_connection():
    return psycopg2.connect(
        database=os.getenv("DB_NAME", "true_book_db"),
        user=os.getenv("DB_USER", "postgres"),
        password=os.getenv("DB_PASS", "postgres"),
        host=os.getenv("DB_HOST", "localhost"),
        port=os.getenv("DB_PORT", "5432")
    )


def delete_existing_book(cur, title: str):
    cur.execute("SELECT book_id FROM book WHERE title = %s", (title,))
    book2Delete = cur.fetchone()
    if book2Delete:
        cur.execute("DELETE FROM book WHERE title = %s", (title,))
        cur.execute("DELETE FROM book_chunk WHERE book_id = %s", (book2Delete[0],))


def create_book(title, author, total_chapters):
    payload = {
        'title': title, 
        'author': author, 
        'totalChapters': total_chapters 
    }
    
    response = requests.post('http://localhost:8080/book/create', json=payload)

    if response.status_code == 201:
        book_id = int(response.text)
        return book_id
    else:
        return None


def insert_chunks(cur, book_id: int, chapter_num: int, chunks: list, embeddings: list):
    data_to_insert = [
        (book_id, chapter_num, chunk, emb.values)
        for chunk, emb in zip(chunks, embeddings)
    ]
    execute_values(cur,
                   "INSERT INTO book_chunk (book_id, chapter_number, content, embedding) VALUES %s",
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

def main():
    load_dotenv(find_dotenv())

    connection = get_db_connection()
    cur = connection.cursor()

    directory = Path("epubs")
    for epubFile in directory.iterdir():

        book = epub.read_epub(str(epubFile))
        book_title, book_author, book_desc = extract_book_metadata(book)

        print(f"Ingesting: {book_title} by {book_author}")

        delete_existing_book(cur, book_title)
        connection.commit()

        chapters = extract_chapters(book)
        book_id = create_book(book_title, book_author, len(chapters))
        if(book_id) == None:
            print("Unable to add book with name: " + book_title)
            continue

        text_splitter = build_text_splitter()
        client = build_embedding_client()

        for chapter_num, chapter in enumerate(chapters, start=1):
            clean_text = clean_html(chapter.get_body_content())
            split_chunks = text_splitter.split_text(clean_text)
            embeddings = embed_chunks(client, split_chunks)

            insert_chunks(cur, book_id, chapter_num, split_chunks, embeddings)
            connection.commit()
            print(f"Chapter {chapter_num}: indexed {len(split_chunks)} chunks")
            time.sleep(0.25)  

    cur.close()
    connection.close()
    print("Ingestion Complete")

if __name__ == "__main__":

    main()
