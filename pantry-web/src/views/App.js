import { BrowserRouter } from "react-router-dom";
import CustomRoutes from "../routes/CustomRoutes.js";
import Header from './_components/Header/Header.js';
import { useState } from 'react';

import { PantryContext, SetPantryContext } from '../components/PantryContext.js';


export default function App() {

  const [pantry, setPantry] = useState({
    id: 0,
    name: "",
    type: "",
    isActive: false
  });

  return (
    <PantryContext.Provider value={pantry}>
      <SetPantryContext.Provider value={setPantry}>
        <div className="container">
          <BrowserRouter>
            <Header />
            <CustomRoutes />
          </BrowserRouter>
        </div>
      </SetPantryContext.Provider>
    </PantryContext.Provider>
  )
}  