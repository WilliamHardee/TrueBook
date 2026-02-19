import PopularBooks from "./PopularBooks"
import SearchBox from "./SearchBox"
import Title from "./Title"


function LandingPage() {

  return (
    <div className='min-h-lvh bg-white'>
      <div className='flex flex-col gap-5'>
        <div className='items-center px-24 h-[60vh] flex flex-wrap justify-evenly align-middle'>
          <Title/>
          <SearchBox/>
        </div>
        <PopularBooks/>
      </div>
     
      
    </div>
  )
}

export default LandingPage
