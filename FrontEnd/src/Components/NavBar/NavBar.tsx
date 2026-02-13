export default function NavBar() {
  return (
    <div className="flex items-center justify-between px-24 fixed top-0 left-0 w-full h-16 shadow-sm z-50 bg-white">
        <div className="text-2xl font-medium">
            <span className="text-blue-600">True</span>Book
        </div>
        <div className="bg-blue-500 hover:bg-blue-600 text-white rounded-xl px-5 py-1.5 cursor-pointer transition-colors duration-150">
            Log In
        </div>
    </div>
  )
}