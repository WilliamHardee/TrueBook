import { NavLink } from "react-router";

interface BookCardProps {
    id: number;
    title: string;
    author: string;
    coverUrl: string;
}

export default function BookCard({id, title, author, coverUrl}: BookCardProps) {
    return (
        <NavLink to={`/book/${id}`}>
            <div className="flex flex-col justify-center cursor-pointer">
                <img className="w-full mx-auto" src={coverUrl} alt={`Cover of ${title}`}/>
                <div className="">
                    <div className="font-normal">{title}</div>
                    <div>By {author}</div>
                </div>
            </div>
        </NavLink>
    )
}