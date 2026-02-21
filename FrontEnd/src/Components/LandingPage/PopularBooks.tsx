import { useEffect, useState } from "react"
import type { BookCardType } from "../../Types/BookTypes";
import BookCard from "./BookCard";

export default function PopularBooks() {
    const[popularBooks, setPopularBooks] = useState<BookCardType[]>([])

   useEffect(() => {
        const fetchPopularBooks = async () => {
            try {
                const response = await fetch("http://localhost:8080/book/getPopular");
                if (!response.ok) throw new Error("Failed to fetch books");

                const jsonResults = await response.json();

                const books: BookCardType[] = jsonResults

                setPopularBooks(books);

            } catch (err: unknown) {
                if (err instanceof Error) {
                    console.error("Unable to fetch popular books", err.message);
                } else {
                    console.error("Unable to fetch popular books", err);
                }
            }
        };

        fetchPopularBooks();
    }, []);

    return( 
        <div className="w-full px-24 py-2 min-h-[40vh] bg-slate-100 text-gray-700 font-light shadow-[0_-8px_20px_rgba(0,0,0,0.15)]">
            <div className="text-3xl">
                Popular Books
            </div>
            <div className="my-6 gap-16 grid grid-cols-5 grid-rows-2">
                {
                    popularBooks.map((book) => <BookCard key={book.id} id={book.id} title={book.title} author={book.author} coverUrl={book.coverUrl}/>)
                }

            </div>
           
        </div>
    )
}