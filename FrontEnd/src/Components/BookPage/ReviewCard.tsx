import type { Review } from "../../Types/BookTypes";

interface ReviewCardProps {
    review: Review
}

export default function ReviewCard({ review }: ReviewCardProps) {
    const rating = Math.max(0, Math.min(5, review.rating))

    const stars = Array.from({ length: 5 }, (_, index) => (
        <img
            key={index}
            src={
                index < rating
                    ? "/star-yellow.svg"
                    : "/star-empty.svg"
            }
            alt="star"
            className="w-4 h-4"
        />
    ))

    return (
        <div className="flex gap-3 flex-col shadow-sm py-4 px-4 rounded-xl">
            <div className="flex gap-2 items-center">
                <img
                    className="rounded-full w-10 h-10"
                    src={review.imageUrl}
                    alt="Reviewer"
                />
                <span>{review.reviewerName}</span>

                <div className="flex gap-1 ml-auto">
                    {stars}
                </div>
            </div>

            <div>
                {review.review}
            </div>
        </div>
    )
}