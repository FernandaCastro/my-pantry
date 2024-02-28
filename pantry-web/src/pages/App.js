import { BrowserRouter, useNavigate } from "react-router-dom";
import CustomRoutes from "../routes/CustomRoutes.js";
import Header from '../components/Header.js';
import React, { useState, useEffect } from 'react';
import Container from 'react-bootstrap/Container';
import Alert from 'react-bootstrap/Alert';
import Fade from 'react-bootstrap/Fade';
import '../assets/styles/App.scss';
import Collapse from 'react-bootstrap/Collapse';
import History from '../routes/History.js';
import NavigateSetter from "../routes/NavigateSetter.js";

import { PantryContext, AlertContext, ProfileContext } from '../services/context/AppContext.js';

export default function App() {

  History.navigate = useNavigate();

  const [profileCtx, setProfileCtx] = useState(() => {
    const data = localStorage.getItem("profile-context");
    return !data || data === 'undefined' || Object.keys(data).length === 0 ? {} : JSON.parse(data);
  });

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
    if (alert.show) {
      setTimeout(() => {
        setAlert(
          (a) => a = { ...a, show: false }
        );
      }, 5000);
    }
  }, [alert.show])

  React.useEffect(() => {
    localStorage.setItem("pantry-context", JSON.stringify(pantryCtx));
  }, [pantryCtx]);

  React.useEffect(() => {
    localStorage.setItem("profile-context", JSON.stringify(profileCtx));
  }, [profileCtx]);

  return (
    <ProfileContext.Provider value={{ profileCtx, setProfileCtx }}>
      <PantryContext.Provider value={{ pantryCtx, setPantryCtx }}>
        <AlertContext.Provider value={{ alert, setAlert }}>
          <Header />
          <Alert variant={alert.type} show={alert.show} onClose={() => setAlert((a) => a = { ...a, show: !alert.show })} dismissible transition={Fade}>{alert.message}</Alert>
          <Container>
            <NavigateSetter />
            <CustomRoutes />
          </Container>
        </AlertContext.Provider>
      </PantryContext.Provider >
    </ProfileContext.Provider>
  )
}