import '../assets/styles/App.scss';
import Header from '../components/Header.js';
import Container from 'react-bootstrap/Container';
import Footer from "../components/Footer.js";
import { createBrowserRouter, Outlet, redirect, RouterProvider } from 'react-router-dom'
import NotFound from "./NotFound";
import Login from './Login';
import Register from './Register';
import ResetPassword from './ResetPassword';
import Logout from './Logout';
import Home from './Home';
import Consume from './Consume';
import Purchase from './Purchase';
import Pantry from './Pantry';
import Product from './Product';
import GroupMembers from './GroupMembers';
import Pantries from './Pantries';
import Welcome from './Welcome';
import { Supermarket } from './Supermarket';
import PantryItems from './PantryItems';
import NewPantryWizard from './NewPantryWizard';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import CustomAlert from '../components/CustomAlert';
import { ReactQueryDevtools } from '@tanstack/react-query-devtools'
import { Suspense } from 'react';
import { PurchaseProvider } from '../context/PurchaseProvider';
import GlobalLoading from '../components/GlobalLoading';

const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      refetchOnWindowFocus: false,
      refetchOnReconnect: false,
    },
  },
});

export const Layout = () => {

  return (
    <>
      <Header />
      <CustomAlert />
      <Container className="content">
        <GlobalLoading />
        <Outlet />
      </Container>
      <Footer />
    </>
  )
}

const router = createBrowserRouter([
  {
    path: "/",
    element: <Layout />,
    children: [
      {
        index: true,
        loader: () => redirect("/home")
      },
      {
        path: "/home",
        element: <Home />
      },
      {
        path: "*",
        element: <NotFound />
      },
      {
        path: "/welcome",
        element: <Welcome />
      },
      {
        path: "/account/new",
        element: <Register mode="new" />
      },
      {
        path: "/account/edit",
        element: <Register mode="edit" />
      },
      {
        path: "/reset-password/:enteredEmail",
        element: <ResetPassword />
      },
      {
        path: "/reset-password",
        element: <ResetPassword />
      },
      {
        path: "/login",
        element: <Login />
      },
      {
        path: "/logout",
        element: <Logout />
      },
      {
        path: "/pantries",
        element: <Pantries />
      },
      {
        path: "/pantries/:id/edit",
        element: <Pantry mode="edit" />
      },
      {
        path: "/pantries/:id/items",
        element: <PantryItems />
      },
      {
        path: "/pantries/new",
        element: <Pantry mode="new" />
      },
      {
        path: "/pantries/consume",
        element: <Consume />
      },
      {
        path: "/purchase",
        element:
          (
            < PurchaseProvider >
              <Purchase />
            </PurchaseProvider >
          )
      },
      {
        path: "/product",
        element: <Product />
      },
      {
        path: "/group-members",
        element: <GroupMembers />
      },
      {
        path: "/supermarkets",
        element: <Supermarket />
      },
      {
        path: "/pantries/new-wizard",
        element: <NewPantryWizard />
      }]
  }
]);


export default function App() {

  //History.navigate = useNavigate();

  // const target = useRef(null);

  // const [profileCtx, setProfileCtx] = useState(() => {
  //   const data = localStorage.getItem("profile-context");
  //   return !data || data === 'undefined' || Object.keys(data).length === 0 ? {} : JSON.parse(data);
  // });

  // const [purchaseCtx, setPurchaseCtx] = useState(() => {
  //   const data = localStorage.getItem("purchase-context");
  //   return !data || data === 'undefined' || Object.keys(data).length === 0 ? [] : JSON.parse(data);
  // });

  // const [alert, setAlert] = useState({
  //   show: false,
  //   message: "",
  //   type: ""
  // });

  // useEffect(() => {
  //   if (profileCtx?.theme) {
  //     document.body.className = profileCtx.theme;
  //   }
  // }, []);

  // useEffect(() => {
  //   if (alert.show) {
  //     setTimeout(() => {
  //       setAlert(
  //         (a) => a = { ...a, show: false }
  //       );
  //     }, 10000);
  //   }
  // }, [alert.show])

  // useEffect(() => {
  //   localStorage.setItem("profile-context", JSON.stringify(profileCtx));
  // }, [profileCtx]);

  // useEffect(() => {
  //   localStorage.setItem("purchase-context", JSON.stringify(purchaseCtx));
  // }, [purchaseCtx]);


  return (
    <QueryClientProvider client={queryClient}>
      <Suspense>
        <RouterProvider router={router} />
      </Suspense>
      <ReactQueryDevtools initialIsOpen={false} />
    </QueryClientProvider>
  )


  // return (
  //   <Suspense fallback={<RippleLoading />} >
  //     <ProfileContext.Provider value={{ profileCtx, setProfileCtx }}>
  //       <PurchaseContext.Provider value={{ purchaseCtx, setPurchaseCtx }}>
  //         <AlertContext.Provider value={{ alert, setAlert }}>

  //             <Header />
  //             <div ref={target} className='alert-box'>
  //               <Overlay target={target.current} show={alert.show} placement="bottom" transition={Fade}>
  //                 <Alert variant={alert.type} show={alert.show} onClose={() => setAlert((a) => a = { ...a, show: !alert.show })} dismissible transition={Fade}>{alert.message}</Alert>
  //               </Overlay>
  //             </div>
  //             <LoadingProvider>
  //               <Container className="content">
  //                 <NavigateSetter />
  //                 <TranslationSetter />
  //                 <CustomRoutes />
  //               </Container>
  //             </LoadingProvider>
  //             <Footer />
  //         </AlertContext.Provider>
  //       </PurchaseContext.Provider>
  //     </ProfileContext.Provider>
  //   </Suspense>

  // )
}