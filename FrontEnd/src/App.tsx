
import Title from './Components/LandingPage/Title'
import PopularBooks from './Components/LandingPage/PopularBooks'
import SearchBox from './Components/LandingPage/SearchBox'
import NavBar from './Components/NavBar/NavBar'

function App() {


  return (
    <div className='min-h-lvh bg-white'>
      <NavBar/>
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

export default App
