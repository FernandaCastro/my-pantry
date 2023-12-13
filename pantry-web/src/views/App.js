import { BrowserRouter } from "react-router-dom";
import CustomRoutes from "../routes/CustomRoutes.js";
import Header from './components/Header.js';
import React, { useState } from 'react';
import Container from 'react-bootstrap/Container';
import Alert from 'react-bootstrap/Alert';

import { PantryContext, AlertContext } from '../services/context/AppContext.js';

export default function App() {

  const [pantryCtx, setPantryCtx] = useState(() => {
    const data = localStorage.getItem("pantry-context");
    return JSON.parse(data) ||
    {
      id: 0,
      name: "",
      type: "",
      isActive: false
    }
  });

  const [alert, setAlert] = useState({
    show: false,
    message: "",
    type: ""
  });

  React.useEffect(() => {
    localStorage.setItem("pantry-context", JSON.stringify(pantryCtx));
  }, [pantryCtx]);

  return (
    <PantryContext.Provider value={{ pantryCtx, setPantryCtx }}>
      <AlertContext.Provider value={{ alert, setAlert }}>
        <Container>
          <BrowserRouter>
            <Header />
            <Alert variant={alert.type} show={alert.show} onClose={() => setAlert((a) => a = { ...a, show: !alert.show })} dismissible >{alert.message}</Alert>
            <CustomRoutes />
          </BrowserRouter>
        </Container>
      </AlertContext.Provider>
    </PantryContext.Provider >
  )
}  