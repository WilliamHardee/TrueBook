export default function SearchBox() {

  return (
    <div className="flex-1 flex flex-col gap-2">
      <label className="text-sm font-medium">
        Search For A Book
      </label>

      <div className="relative w-4/5">
        <input
          type="text"
          placeholder="Search..."
          aria-label="Search"
          className="w-full h-12 rounded-3xl bg-slate-50 pr-24 pl-4 shadow-[0_0_20px_rgba(0,0,0,0.15)] focus:outline-none focus:ring-2 focus:ring-blue-500"
        />

        <button
          className="absolute right-2 top-1/2 -translate-y-1/2 bg-blue-500 hover:bg-blue-600 text-white rounded-3xl px-5 py-1 cursor-pointer transition-colors duration-150"
        >
          Find
        </button>
      </div>
    </div>
  );
}
    
