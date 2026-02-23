import { useState } from "react";
import type { Review } from "../../Types/BookTypes";
import ReviewCard from "./ReviewCard";

interface ReviewContainerProps {
    reviews: Review[]
}

export default function ReviewContainer({reviews}:ReviewContainerProps) {
    const [page, setPage] = useState<number>(0)
    const pages: Review[][] = generatePages()

    
    function generatePages(): Review[][] {
        const pageArray: Review[][] = []

        for (let i = 0; i < reviews.length; i += 10) {
            pageArray.push(reviews.slice(i, i + 10))
        }

        return pageArray
    }

    return (
        <div className="w-full px-24 py-2 min-h-[40vh] bg-slate-100 text-gray-700 font-light shadow-[0_-8px_20px_rgba(0,0,0,0.15)]">
            <div className="flex justify-between">
                <h1 className="text-3xl my-3">Reviews</h1>
                <div className="flex items-center">
                    {page !== 0 ? <img onClick={()=>setPage(page-1)} className="cursor-pointer w-10 h-10" src="/arrow.svg"/>: ""}
                    <span className="text-xl">Page: {page + 1} of {pages.length}</span>
                    {page !== pages.length - 1 ? <img onClick={()=>setPage(page+1)} className="cursor-pointer rotate-180 w-10 h-10" src="/arrow.svg"/>: "" }
                </div>
            </div>
            
            <div className="flex flex-col gap-6">
                {pages?.[page]?.map((review: Review) => (
                    <ReviewCard key={review.reviewId} review={review} />
                ))}
            </div>
        </div>
    )
}