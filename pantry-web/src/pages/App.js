import '../assets/styles/App.scss';
import { useNavigate } from "react-router-dom";
import CustomRoutes from "../routes/CustomRoutes.js";
import Header from '../components/Header.js';
import { useState, useEffect, useRef } from 'react';
import Container from 'react-bootstrap/Container';
import Alert from 'react-bootstrap/Alert';
import Fade from 'react-bootstrap/Fade';
import History from '../util/History.js';
import TranslationSetter from '../util/TranslationSetter.js'
import NavigateSetter from "../util/NavigateSetter.js";
import { Suspense } from 'react';
import { AlertContext, ProfileContext, PurchaseContext } from '../context/AppContext.js';
import { Overlay } from "react-bootstrap";
import Footer from "../components/Footer.js";
import { LoadingProvider } from '../context/LoadingProvider';
import { RippleLoading } from '../components/RippleLoading';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';

export default function App() {

  const queryClient = new QueryClient();

  History.navigate = useNavigate();

  const target = useRef(null);

  const [profileCtx, setProfileCtx] = useState(() => {
    const data = localStorage.getItem("profile-context");
    return !data || data === 'undefined' || Object.keys(data).length === 0 ? {} : JSON.parse(data);
  });

  const [purchaseCtx, setPurchaseCtx] = useState(() => {
    const data = localStorage.getItem("purchase-context");
    return !data || data === 'undefined' || Object.keys(data).length === 0 ? [] : JSON.parse(data);
  });

  const [alert, setAlert] = useState({
    show: false,
    message: "",
    type: ""
  });

  useEffect(() => {
    if (profileCtx?.theme) {
      document.body.className = profileCtx.theme;
    }
  }, []);

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

  useEffect(() => {
    localStorage.setItem("purchase-context", JSON.stringify(purchaseCtx));
  }, [purchaseCtx]);

  // style={{ height: '40px', verticalAlign: 'middle' }}

  return (
    <Suspense fallback={<RippleLoading />} >
      <ProfileContext.Provider value={{ profileCtx, setProfileCtx }}>
        <PurchaseContext.Provider value={{ purchaseCtx, setPurchaseCtx }}>
          <AlertContext.Provider value={{ alert, setAlert }}>
            <QueryClientProvider client={queryClient}>
              <Header />
              <div ref={target} className='alert-box'>
                <Overlay target={target.current} show={alert.show} placement="bottom" transition={Fade}>
                  <Alert variant={alert.type} show={alert.show} onClose={() => setAlert((a) => a = { ...a, show: !alert.show })} dismissible transition={Fade}>{alert.message}</Alert>
                </Overlay>
              </div>
              <LoadingProvider>
                <Container className="content">
                  <NavigateSetter />
                  <TranslationSetter />
                  <CustomRoutes />
                </Container>
              </LoadingProvider>
              <Footer />
            </QueryClientProvider>
          </AlertContext.Provider>
        </PurchaseContext.Provider>
      </ProfileContext.Provider>
    </Suspense>

  )
}