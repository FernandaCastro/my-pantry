import React, { useState, useEffect, useContext } from 'react';
import { PantryContext } from '../../services/context/AppContext.js';
import { getPantryList, deletePantry } from '../../services/apis/mypantry/fetch/requests/PantryRequests.js';
import Stack from 'react-bootstrap/Stack';
import ListGroup from 'react-bootstrap/ListGroup';
import Table from 'react-bootstrap/Table';
import Button from 'react-bootstrap/Button';
import { AlertContext } from '../../services/context/AppContext.js';
import VariantType from '../components/VariantType.js';
import { BsPencil, BsTrash } from "react-icons/bs";

export default function Home() {

    const [pantries, setPantries] = useState([]);
    const [refresh, setRefresh] = useState(true);

    const { pantryCtx, setPantryCtx } = useContext(PantryContext);
    const { alert, setAlert } = useContext(AlertContext);

    useEffect(() => {
        if (refresh) fetchPantries();
    }, [refresh])

    async function fetchPantries() {
        setRefresh(true);
        try {
            const res = await getPantryList();
            setPantries(res);
            setRefresh(false);
        } catch (error) {
            showAlert(VariantType.DANGER, error.message);
        }
    }

    async function fetchDeletePantry(id) {
        setRefresh(false);
        try {
            await deletePantry(id);
            setRefresh(true);
        } catch (error) {
            showAlert(VariantType.DANGER, error.message);
        }
    }

    function showAlert(type, message) {
        setAlert({
            show: true,
            type: type,
            message: message
        })
    }

    function renderItem(item) {
        return (
            <tr key={item.id} className="border border-primary-subtle">
                <td className="border-end-0" onClick={(e) => handlePantryClick(item)}>
                    <span className={item.isActive ? "" : "text-black-50"}>{item.name}</span></td>
                <td className="border-start-0">
                    <Stack direction="horizontal" gap={1} className="d-flex justify-content-end">
                        <div><Button href={"/pantries/" + item.id + "/edit"} variant="link"><BsPencil /></Button></div>
                        <div><Button onClick={() => handleRemove(item.id)} variant="link"><BsTrash /></Button></div>
                    </Stack>
                </td>
            </tr>
        )
    }

    function renderItems() {
        return pantries.map((item) => (renderItem(item)));
    }

    function handlePantryClick(item) {
        item.isActive ? setPantryCtx(item) : setPantryCtx({});
    }

    function handleRemove(id) {
        fetchDeletePantry(id);
        showAlert(VariantType.SUCCESS, "Pantry removed successfully!");
        if (pantryCtx.id === id) setPantryCtx({})
    }

    return (
        <Stack gap={3}>
            <div></div>
            <div className="d-flex justify-content-end align-items-center">

                <Button variant="primary" size="sm" href={"/pantries/new"} >New Pantry</Button>

            </div>
            <div>
                <Table variant="primary" className='table table-sm align-middle' hover>
                    <tbody>
                        {renderItems()}
                    </tbody>
                </Table>
            </div>
        </Stack>
    )
}