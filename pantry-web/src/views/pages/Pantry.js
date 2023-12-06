import React, { useEffect, useState, useContext } from 'react';
import { useParams, useNavigate } from 'react-router';
import { getPantry, updatePantry, createPantry, createPantryItem } from '../../services/apis/mypantry/fetch/requests/PantryRequests.js';
import Stack from 'react-bootstrap/Stack';
import VariantType from '../components/VariantType.js';
import PantryForm from '../components/PantryForm.js';
import { SetAlertContext } from '../../services/context/PantryContext.js';
import ProductSearchBar from '../components/ProductSearchBar.js'
import PantryItemList from '../components/PantryItemList.js';

export default function Pantry({ mode }) {

    let { id } = useParams();

    const [pantry, setPantry] = useState({});
    const [isLoading, setIsLoading] = useState(true);
    const setAlert = useContext(SetAlertContext);
    const navigate = useNavigate();

    useEffect(() => {
        setIsLoading(true);
        if (mode === 'edit') {
            fetchPantry();
        }
    }, [])

    async function fetchPantry() {
        try {
            const res = await getPantry(id);
            setPantry(res);
            setIsLoading(false);
        } catch (error) {
            showAlert(VariantType.DANGER, error.message);
        }
    }

    async function fetchSavePantry(body) {
        setIsLoading(true);
        try {
            const res = mode === 'new' ? await createPantry(body) : await updatePantry(id, body);
            if (!res) return;
            setPantry(res);
            setIsLoading(false);
        } catch (error) {
            showAlert(VariantType.DANGER, error.message);
        }
    }

    async function fetchSavePantryItem(body) {
        setIsLoading(true);
        try {
            const res = await createPantryItem(pantry.id, body);
            setIsLoading(false);
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

    function handleSave(body) {
        fetchSavePantry(body);
        const message = mode === 'edit' ? "updated!" : "created!";
        showAlert(VariantType.SUCCESS, "Pantry successfully " + message);
        return navigate("/", { replace: true });
    }

    function handleAddAction(product) {
        const body = {
            pantryId: pantry.id,
            productId: product.id
        }
        fetchSavePantryItem(body);
    }

    return (
        <Stack gap={3}>
            <div></div>
            <div>
                {isLoading ?
                    <h6>Loading...</h6> :
                    <PantryForm pantry={pantry} handleSave={handleSave} />}
            </div>
            <div><ProductSearchBar handleAction={handleAddAction} /></div>
            <div>
                {isLoading || !pantry ?
                    <h6>Loading...</h6> :
                    <PantryItemList pantryId={pantry.id} />}
            </div>
        </Stack>
    );
}