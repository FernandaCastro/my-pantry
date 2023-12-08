import React, { useState, useEffect, useContext } from 'react';
import { PantryContext, SetPantryContext } from '../../services/context/PantryContext.js';
import { getPantryList, deletePantry } from '../../services/apis/mypantry/fetch/requests/PantryRequests.js';
import Stack from 'react-bootstrap/Stack';
import ListGroup from 'react-bootstrap/ListGroup';
import Table from 'react-bootstrap/Table';
import Button from 'react-bootstrap/Button';
import { SetAlertContext } from '../../services/context/PantryContext.js';
import VariantType from '../components/VariantType.js';
import { BsPencil, BsTrash } from "react-icons/bs";

export default function Home() {

    const [pantries, setPantries] = useState([])
    const [isLoading, setIsLoading] = useState(true)

    const pantry = useContext(PantryContext);
    const setPantry = useContext(SetPantryContext);

    const setAlert = useContext(SetAlertContext);

    useEffect(() => {
        fetchPantries()
        setIsLoading(true)
    }, [isLoading])

    async function fetchPantries() {
        setIsLoading(true);
        try {
            const res = await getPantryList();
            setPantries(res)
            setIsLoading(false)
        } catch (error) {
            showAlert(VariantType.DANGER, error.message);
        }
    }

    async function fetchDeletePantry(id) {
        try {
            await deletePantry(id);
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
        item.isActive ? setPantry(item) : setPantry({});
    }

    function handleRemove(id) {
        fetchDeletePantry(id);
        showAlert(VariantType.SUCCESS, "Pantry removed successfully!");
        setPantry({})
        fetchPantries();
    }

    return (
        <Stack gap={3}>
            <div></div>
            <div>
                <ListGroup >
                    <ListGroup.Item variant="primary" className="d-flex justify-content-between align-items-center">
                        <span>{pantry.name}</span>
                        <Button variant="primary" size="sm" href={"/pantries/new"} >New Pantry</Button>
                    </ListGroup.Item>
                </ListGroup>
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