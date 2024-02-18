import React from 'react';
import { useRoutes } from "react-router-dom";

import NotFound from "../pages/NotFound";
import Logout from '../pages/Logout';

import Home from '../pages/Home';
import Consume from '../pages/Consume';
import Purchase from '../pages/Purchase';
import Pantry from '../pages/Pantry';
import Product from '../pages/Product';
import GroupMembers from '../pages/GroupMembers';
import Pantries from '../pages/Pantries';

function CustomRoutes() {
    let routes = useRoutes([
        {
            path: "*",
            element: <NotFound />
        },
        {
            path: "/logout",
            element: <Logout />
        },
        {
            path: "/",
            element: <Home />
        },
        {
            path: "/home",
            element: <Home />
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
        {
            path: "/group-members",
            element: <GroupMembers />
        },
    ]);

    return routes;
}

export default CustomRoutes;