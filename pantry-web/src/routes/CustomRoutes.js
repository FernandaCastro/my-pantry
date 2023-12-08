import React from 'react';
import { useRoutes } from "react-router-dom";

import NotFound from "../views/pages/NotFound";

import Home from '../views/pages/Home';
import Consume from '../views/pages/Consume';
import Purchase from '../views/pages/Purchase';
import Pantry from '../views/pages/Pantry';
import Product from '../views/pages/Product';


function CustomRoutes() {
    let routes = useRoutes([
        {
            path: "*",
            element: <NotFound />
        },
        {
            path: "/",
            element: <Home />
        },
        {
            path: "/pantries/:id/edit",
            element: <Pantry mode="edit" />
        },
        {
            path: "/pantries/new",
            element: <Pantry mode="new" />
        },
        {
            path: "/pantries/:id/consume",
            element: <Consume />
        },
        {
            path: "/purchase",
            element: <Purchase />
        },
        {
            path: "/product",
            element: <Product />
        },
    ]);

    return routes;
}

export default CustomRoutes;