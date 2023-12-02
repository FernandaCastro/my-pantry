import React from 'react';
import { useRoutes } from "react-router-dom";

import NotFound from "../views/pages/NotFound";

import Home from '../views/pages/Home';
import Consume from '../views/pages/Consume';
import Purchase from '../views/pages/Purchase';
import Pantry from '../views/pages/Pantry';

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
            path: "/pantries/:id",
            element: <Pantry />
        },
        {
            path: "/pantries/:id/consume",
            element: <Consume />
        },
        {
            path: "/purchase",
            element: <Purchase />
        },
    ]);

    return routes;
}

export default CustomRoutes;