# 📚 Spoiler-Free Book Assistant

An AI-powered reading companion that provides summaries and answers questions about the book you're currently reading—without ever spoiling future chapters.

## 🏗 System Architecture

This project uses a **Retrieval-Augmented Generation (RAG)** pipeline with a unique "Metadata Gate" to ensure the AI only sees what you've read. It bridges a modern web frontend with a robust backend and a specialized AI microservice.



* **Frontend:** React + Vite + TypeScript — *Responsive UI with chapter-aware context.*
* **Backend:** Java Spring Boot — *Handles business logic, user progress, and secure data access.*
* **AI Service:** Python + FastAPI — *Manages EPUB parsing, Gemini embeddings, and retrieval logic.*
* **Database:** PostgreSQL + `pgvector` — *Stores text chunks and high-dimensional vectors.*

---

## 🛠 Features

* **The Spoiler Gate:** A custom retrieval filter that restricts the AI's "memory" based on your current `chapter_number`. 
* **Semantic Search:** Uses Google’s `text-embedding-004` (truncated to 768 dimensions) for fast, accurate meaning-based search.
* **EPUB Processing:** Automated ingestion that respects book structure, ensuring chunks don't bleed across chapter boundaries.
* **Self-Hosted Vectors:** Leverages the HNSW index within Postgres for lightning-fast similarity lookups.

---
