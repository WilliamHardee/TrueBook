import { useEffect, useState } from "react";
import { useParams } from "react-router"
import type { Book } from "../../Types/BookTypes";
import ReviewContainer from "./ReviewContainer";


export default function BookPage() {

    const [book, setBook] = useState<Book|null>(null)
    const params = useParams<{ bookId: string }>()

    useEffect(() => {
        const fetchBook = async () => {
            try {
                const response = await fetch(`http://localhost:8080/book/${params.bookId}`);
                if (!response.ok) throw new Error("Failed to fetch books");
                const jsonResults = await response.json();
        
                const book: Book = jsonResults
                console.log(book)
                setBook(book);

            } catch (err: unknown) {
                if (err instanceof Error) {
                    console.error("Unable to fetch book", err.message);
                } else {
                    console.error("Unable to fetch book", err);
                }
            }
        };

        fetchBook();


    }, []);

    

    return (
        <div className="flex flex-col">
           <div className="flex flex-wrap justify-around gap-16 px-24 py-28 w-full items-center">
                <div className="flex-1 min-w-[300px]">
                    <div className="mb-10">
                        <h1 className="text-4xl font-medium">{book?.title}</h1>
                        <h2 className="text-2xl font-light">{book?.author}</h2>
                        <div className="flex gap-1 my-1">
                            {Array.from({ length: 5 }, (_, index) => (
                                <img
                                    key={index}
                                    src={
                                        index < (book?.rating || 0)
                                            ? "/star-yellow.svg"
                                            : "/star-empty.svg"
                                    }
                                    alt="star"
                                    className="w-5 h-5"
                                />
                            ))}
                        </div>
                    </div>
                    <p className="text-base leading-relaxed">{book?.summary}</p>
                    <div className="flex gap-3 my-4">
                        <div className="flex items-center gap-1 text-xl rounded-md border-black border-2 py-1 px-3 cursor-pointer transition-colors duration-150 hover:bg-gray-200">
                            <img className="w-8 h-8" src="/book.svg"/>
                            Read
                        </div>
                        <div className="flex items-center gap-1 text-xl rounded-md border-black border-2 py-1 px-3 cursor-pointer transition-colors duration-150 hover:bg-gray-200">
                            <img className="w-8 h-8" src="/star-empty.svg"/>
                            Review
                        </div>
                    </div>
                </div>
                <div className="flex-1 min-w-[300px] flex justify-center items-start">
                    <img className="w-1/2 max-w-xs object-contain" src={book?.coverUrl} alt={book?.title} />
                </div>
                
            </div>
            <ReviewContainer reviews={book?.reviews ? book.reviews : []}/>
        </div>
    )
}