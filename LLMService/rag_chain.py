from dotenv import load_dotenv, find_dotenv
from langchain.chat_models import init_chat_model
from langchain_google_genai import GoogleGenerativeAIEmbeddings
from langchain_postgres import PGEngine, PGVectorStore
import asyncio
import sys


load_dotenv(find_dotenv())

if sys.platform == "win32":
    asyncio.set_event_loop_policy(asyncio.WindowsSelectorEventLoopPolicy())

CONNECTION = "postgresql+psycopg://postgres:postgres@localhost:5432/true_book_db"

model = init_chat_model("google_genai:gemini-2.5-flash-lite", temperature=0)


embeddings = GoogleGenerativeAIEmbeddings(
    model="gemini-embedding-001",
    output_dimensionality=768,
)


engine = PGEngine.from_connection_string(CONNECTION)

vector_store = PGVectorStore.create_sync(
    engine=engine,
    table_name="book_chunks",
    embedding_service=embeddings,
    content_column="content",
    embedding_column="embedding",
    id_column="id",
)

def answer_question(query: str) -> str:

    retrieved_docs = vector_store.similarity_search(query, k=3)
    context = "\n\n".join(doc.page_content for doc in retrieved_docs)

    prompt = f"""
            You are answering questions about a book.

            Use ONLY the provided context.
            If the answer is not explicitly in the context, say you don't know.
            Do not add spoilers beyond what is mentioned.

            Context:
            {context}

            Question:
            {query}
    """

    response = model.invoke(prompt)
    return response.content

query = "Where did Mr. Earnshaw find Heathcliff, and what was he doing there?"
print(answer_question(query))