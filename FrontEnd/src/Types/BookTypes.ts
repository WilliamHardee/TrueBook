
type BookCardType = {
    id: number;
    title: string;
    author: string;
    coverUrl: string;

}

type Book = {
    id: number;
    title: string;
    author: string;
    summary: string;
    coverUrl: string;
    genres: Genre[];
    reviews: Review[];
    rating: number;
    reviewCount: number;
}

type Genre = {
    genreId: number;
    genreName: string;
}

type Review = {
    imageUrl: string;
    rating: number;
    review: string;
    reviewId: number;
    reviewerName: string;
    source: string;
}

export {BookCardType, Book, Genre, Review}