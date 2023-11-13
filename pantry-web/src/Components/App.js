import {
  BrowserRouter as Router,
  Routes,
  Route
} from "react-router-dom";
import { useState } from 'react';
import Home from './Home.js';
import Consume from './Consume.js';
import Purchase from './Purchase.js';
import { PantryContext, SetPantryContext } from './PantryContext.js';


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
        <div>
          <Router>
            <Routes>
              <Route path="/" element={<Home />} />
              <Route path="/consume" element={<Consume />} />
              <Route path="/purchase" element={<Purchase />} />
            </Routes>
          </Router>
        </div>
      </SetPantryContext.Provider>
    </PantryContext.Provider>
  )
}  