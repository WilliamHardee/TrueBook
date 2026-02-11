# This file takes in an EPUB file as its arguement and embeds
# it into the database.

from ebooklib import epub
from dotenv import load_dotenv, find_dotenv
from bs4 import BeautifulSoup
from langchain_text_splitters import RecursiveCharacterTextSplitter
from google import genai
from google.genai import types
from psycopg2.extras import execute_values
import ebooklib
import sys
import psycopg2

import os
import time

if len(sys.argv) != 2: 
    raise Exception("Usage: epub2Vector <path_to_epub>")

load_dotenv(find_dotenv())

connection = psycopg2.connect(database="true_book_db",user="postgres", password="postgres",host="localhost",port="5432")
cur = connection.cursor()

book = epub.read_epub(sys.argv[1])

bookTitle = book.get_metadata("DC", "title")[0][0]
bookAuthor = book.get_metadata("DC", "creator")[0][0]
bookDesc = book.get_metadata("DC", "description")[0][0]

items = list(book.get_items_of_type(ebooklib.ITEM_DOCUMENT))
chapters = [item for item in items if "chapter" in item.get_name()]

# Cleaning old books
cur.execute("SELECT book_id FROM books WHERE title = %s", (bookTitle,))
book2Delete = cur.fetchone()
if(book2Delete != None):
    cur.execute("DELETE FROM books WHERE title = %s", (bookTitle,))
    cur.execute("DELETE FROM book_chunks WHERE book_id = %s", (book2Delete[0],))
    connection.commit()

cur.execute("INSERT INTO books (title, author, total_chapters, summary) VALUES (%s, %s, %s, %s)", (bookTitle, bookAuthor, len(chapters), bookDesc))
connection.commit()

cur.execute("SELECT book_id FROM books WHERE title = %s", (bookTitle,))
bookId = cur.fetchone()[0]



textSplitter = RecursiveCharacterTextSplitter.from_tiktoken_encoder(
    encoding_name="cl100k_base", 
    chunk_size=800, 
    chunk_overlap=150, 
    separators=["\n\n", "\n", ". ", " ", ""] 
)
googleClient = genai.Client(
    api_key=os.getenv("GEMINI_API_KEY")

)

for chapterNum, chapter in enumerate(chapters):
    
    uncleanChapter = BeautifulSoup(chapter.get_body_content(), "html.parser")
    cleanChapter = uncleanChapter.get_text()
    splitChapter = textSplitter.split_text(cleanChapter)
 
    embeddedChunks = googleClient.models.embed_content(
        model="gemini-embedding-001",
        contents=splitChapter,
        config=types.EmbedContentConfig(output_dimensionality=768)
    )
        
    data_to_insert = [
        (bookId, chapterNum + 1, splitChapter[i], emb.values)
        for i, emb in enumerate(embeddedChunks.embeddings)
    ]

    
    execute_values(cur, "INSERT INTO book_chunks (book_id, chapter_number, content, embedding) VALUES %s", data_to_insert)
    connection.commit()
    
    print(f"Successfully indexed {len(data_to_insert)} chunks")
    
    time.sleep(20)

cur.close()
connection.close()
print("Ingestion Complete")
    
    







    




    