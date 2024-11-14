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
import { Supermarket } from './Supermarket';
import PantryItems from './PantryItems';
import NewPantryWizard from './NewPantryWizard';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import CustomAlert from '../components/CustomAlert';
import { ReactQueryDevtools } from '@tanstack/react-query-devtools'
import { Suspense } from 'react';
import { PurchaseProvider } from '../context/PurchaseProvider';
import GlobalLoading from '../components/GlobalLoading';
import NavigateSetter from '../components/NavigateSetter';
import TranslationSetter from '../components/TranslationSetter';

const queryClient = new QueryClient(
  {
    defaultOptions: {
      queries: {
        refetchOnMount: true,
        refetchOnWindowFocus: false,
        refetchOnReconnect: false,
      }
    }
  },
);

const Layout = () => {

  return (
    <>
      <Header />
      <CustomAlert />
      <Container className="content">
        <GlobalLoading />
        <NavigateSetter />
        <TranslationSetter />
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

  return (
    <QueryClientProvider client={queryClient}>
      <Suspense>
        <RouterProvider router={router} />
      </Suspense>
      <ReactQueryDevtools initialIsOpen={false} />
    </QueryClientProvider>
  )

}