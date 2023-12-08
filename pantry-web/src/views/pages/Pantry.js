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

    const [pantry, setPantry] = useState(
        {
            id: 0,
            name: "",
            type: "",
            isActive: true
        });

    const [isLoading, setIsLoading] = useState(false);
    const setAlert = useContext(SetAlertContext);
    const navigate = useNavigate();
    const [refresh, setRefresh] = useState(false);

    useEffect(() => {
        setIsLoading(true);
        if (id && mode === 'edit') {
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
        setRefresh(true);
        try {
            const res = await createPantryItem(pantry.id, body);
            setRefresh(false);
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
        //return navigate("/", { replace: true });
    }

    function handleAddItem(product) {
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
                {mode === "edit" && isLoading ?
                    <h6>Loading...</h6> :
                    <PantryForm pantry={pantry} handleSave={handleSave} />}
            </div>
            <div><ProductSearchBar handleSelectAction={handleAddItem} /></div>
            <div>
                {isLoading && !pantry ?
                    <h6>Loading...</h6> :
                    <PantryItemList key={refresh} pantryId={pantry.id} />}
            </div>
        </Stack>
    );
}