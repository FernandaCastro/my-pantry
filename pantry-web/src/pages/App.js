import { useNavigate } from "react-router-dom";
import CustomRoutes from "../routes/CustomRoutes.js";
import Header from '../components/Header.js';
import { useState, useEffect, useRef } from 'react';
import Container from 'react-bootstrap/Container';
import Alert from 'react-bootstrap/Alert';
import Fade from 'react-bootstrap/Fade';
import '../assets/styles/App.scss';
import History from '../routes/History.js';
import TranslationSetter from '../services/TranslationSetter.js'
import NavigateSetter from "../routes/NavigateSetter.js";
import { Suspense } from 'react';
import { AlertContext, ProfileContext } from '../services/context/AppContext.js';
import { Overlay } from "react-bootstrap";

export default function App() {

  History.navigate = useNavigate();

  const target = useRef(null);

  const [profileCtx, setProfileCtx] = useState(() => {
    const data = localStorage.getItem("profile-context");
    return !data || data === 'undefined' || Object.keys(data).length === 0 ? {} : JSON.parse(data);
  });

  const [alert, setAlert] = useState({
    show: false,
    message: "",
    type: ""
  });

  useEffect(() => {
    if (alert.show) {
      setTimeout(() => {
        setAlert(
          (a) => a = { ...a, show: false }
        );
      }, 10000);
    }
  }, [alert.show])

  useEffect(() => {
    localStorage.setItem("profile-context", JSON.stringify(profileCtx));
  }, [profileCtx]);

  // style={{ height: '40px', verticalAlign: 'middle' }}

  return (
    <Suspense fallback="...is loading">
      <ProfileContext.Provider value={{ profileCtx, setProfileCtx }}>
        <AlertContext.Provider value={{ alert, setAlert }}>
          <Header />
          <div ref={target}>
            <Overlay target={target.current} show={alert.show} placement="bottom" transition={Fade}>
              <Alert variant={alert.type} show={alert.show} onClose={() => setAlert((a) => a = { ...a, show: !alert.show })} dismissible transition={Fade}>{alert.message}</Alert>
            </Overlay>
          </div>
          <Container >
            <NavigateSetter />
            <TranslationSetter />
            <CustomRoutes />
          </Container>
        </AlertContext.Provider>
      </ProfileContext.Provider>
    </Suspense>
  )
}