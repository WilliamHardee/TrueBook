import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import './index.css'

import { createBrowserRouter } from 'react-router'
import { RouterProvider } from 'react-router/dom'
import Layout from './Layout.tsx'
import LandingPage from './Components/LandingPage/LandingPage.tsx'
import BookPage from './Components/BookPage/BookPage.tsx'

const router = createBrowserRouter([{
  path: "/",
  Component: Layout,
  children: [
    {index:true, Component: LandingPage},
    {path:"/book/:bookId", Component: BookPage}
  ]
}])

createRoot(document.getElementById('root')!).render(
  <StrictMode>
    <RouterProvider router={router}/>
  </StrictMode>,
)
