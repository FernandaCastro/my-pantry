import React, { useState, useEffect, useContext } from 'react';
import { PantryContext } from '../services/context/AppContext.js';
import { getPantryList, deletePantry } from '../services/apis/mypantry/requests/PantryRequests.js';
import Stack from 'react-bootstrap/Stack';
import Table from 'react-bootstrap/Table';
import Button from 'react-bootstrap/Button';
import VariantType from '../components/VariantType.js';
import useAlert from '../hooks/useAlert.js';
import { BsPencil, BsTrash } from "react-icons/bs";
import Form from 'react-bootstrap/Form';

export default function Pantries() {

    const [pantries, setPantries] = useState([]);
    const [refresh, setRefresh] = useState(true);

    const [isLoading, setIsLoading] = useState(true);
    const { showAlert } = useAlert();

    useEffect(() => {
        if (refresh) fetchPantries();
    }, [refresh])

    async function fetchPantries() {
        setRefresh(true);
        setIsLoading(true);
        try {
            const res = await getPantryList();
            setPantries(res);
            setRefresh(false);
            setIsLoading(false);
        } catch (error) {
            showAlert(VariantType.DANGER, error.message);
        }
    }

    async function fetchDeletePantry(id) {
        try {
            await deletePantry(id);
            setRefresh(true);
        } catch (error) {
            showAlert(VariantType.DANGER, error.message);
        }
    }

    function renderItem(item) {
        return (
            <tr key={item.id} className="align-middle">
                <td>
                    <span disabled={!item.isActive}>{item.name}</span>
                </td>
                <td>
                    <span className='d-none d-md-block'>{item.accountGroup.name}</span>
                </td>
                <td>
                    <Stack direction="horizontal" gap={1} className="d-flex justify-content-end">
                        <div><Button href={"/pantries/" + item.id + "/edit"} variant="link"><BsPencil className='icon' /></Button></div>
                        <div><Button onClick={() => handleRemove(item.id)} variant="link"><BsTrash className='icon' /></Button></div>
                    </Stack>
                </td>
            </tr>
        )
    }

    function renderItems() {
        if (isLoading)
            return (<span>Loading...</span>)

        return (
            <Table className='bordered'>
                <tbody>
                    {pantries.map((item) => (renderItem(item)))}
                </tbody>
            </Table>
        )
    }

    function handleRemove(id) {
        fetchDeletePantry(id);
        showAlert(VariantType.SUCCESS, "Pantry removed successfully!");
    }

    return (
        <Stack gap={3}>
            <div></div>
            <div className="d-flex justify-content-between align-items-center">
                <h6 className='title'>Pantry List</h6>
                <Button bsPrefix="btn-custom" size="sm" href={"/pantries/new"} className="pe-2 ps-2">New Pantry</Button>
            </div>
            <div>
                {renderItems()}
            </div>
        </Stack>
    )
}

