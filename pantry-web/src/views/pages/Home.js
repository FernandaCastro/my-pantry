import React, { useState, useEffect, useContext } from 'react';
import { PantryContext, SetPantryContext } from '../../services/context/PantryContext.js';
import { getPantryList, deletePantry } from '../../services/apis/mypantry/fetch/requests/PantryRequests.js';
import Stack from 'react-bootstrap/Stack';
import ListGroup from 'react-bootstrap/ListGroup';
import Button from 'react-bootstrap/Button';
import { SetAlertContext } from '../../services/context/PantryContext.js';
import VariantType from '../components/VariantType.js';


export default function Home() {

    const [pantries, setPantries] = useState([])
    const [isLoading, setIsLoading] = useState(true)

    const pantry = useContext(PantryContext);
    const setPantry = useContext(SetPantryContext);

    const setAlert = useContext(SetAlertContext);

    useEffect(() => {
        fetchPantries()
        setIsLoading(true)
    }, [])

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
            <ListGroup.Item variant="primary" key={item.id}
                className="d-flex justify-content-between align-items-center"
                action onClick={(e) => handlePantryClick(item)}>
                <span className={item.isActive ? "" : "text-black-50"}>{item.name}</span>
                <Stack direction="horizontal" gap={2} className="d-flex justify-content-end">
                    <div><Button href={"/pantries/" + item.id} variant="link">Edit</Button></div>
                    <div><Button onClick={() => handleRemove(item.id)} variant="link">Remove</Button></div>
                </Stack>
            </ListGroup.Item>
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
                        Selected: {pantry.name}
                        <Button variant="primary" size="sm" href={"/pantries/0"} >New Pantry</Button>
                    </ListGroup.Item>
                </ListGroup>
            </div>
            <div>
                <ListGroup>
                    {renderItems()}
                </ListGroup>
            </div>
        </Stack>
    )
}