import { BrowserRouter } from "react-router-dom";
import CustomRoutes from "../routes/CustomRoutes.js";
import Header from './components/Header.js';
import React, { useState } from 'react';
import Container from 'react-bootstrap/Container';
import Alert from 'react-bootstrap/Alert';

import { PantryContext, SetPantryContext, AlertContext, SetAlertContext } from '../services/context/PantryContext.js';

export default function App() {

  const [pantry, setPantry] = useState({
    id: 0,
    name: "",
    type: "",
    isActive: false
  });

  const [alert, setAlert] = useState({
    show: false,
    message: "",
    type: ""
  });

  return (
    <PantryContext.Provider value={pantry}>
      <SetPantryContext.Provider value={setPantry}>
        <AlertContext.Provider value={alert}>
          <SetAlertContext.Provider value={setAlert}>
            <Container>
              <BrowserRouter>
                <Header />
                <Alert variant={alert.type} show={alert.show} onClose={() => setAlert((a) => a = { ...a, show: !alert.show })} dismissible >{alert.message}</Alert>
                <CustomRoutes />
              </BrowserRouter>
            </Container>
          </SetAlertContext.Provider>
        </AlertContext.Provider>
      </SetPantryContext.Provider>
    </PantryContext.Provider >
  )
}  