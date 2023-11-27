import React from 'react';
import { useRoutes } from "react-router-dom";

import NotFound from "../views/pages/NotFound/NotFound";

import Home from '../views/pages/Home/Home';
import Consume from '../views/pages/Consume/Consume';
import Purchase from '../views/pages/Purchase/Purchase';
import Pantry from '../views/pages/Home/Pantry';

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
            path: "/consume",
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