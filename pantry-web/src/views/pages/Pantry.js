import React, { useEffect, useState, useContext } from 'react';
import { useParams } from 'react-router';
import { getPantry, updatePantry, createPantry, createPantryItem, getPantryRebalance } from '../../services/apis/mypantry/fetch/requests/PantryRequests.js';
import Stack from 'react-bootstrap/Stack';
import VariantType from '../components/VariantType.js';
import PantryForm from '../components/PantryForm.js';
import { AlertContext } from '../../services/context/AppContext.js';
import ProductSearchBar from '../components/ProductSearchBar.js'
import PantryItemList from '../components/PantryItemList.js';
import Button from 'react-bootstrap/Button';

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
    const { alert, setAlert } = useContext(AlertContext);
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

    async function fetchPantryRebalance() {
        setIsLoading(true);
        setRefresh(true);
        try {
            await getPantryRebalance(id);
            setRefresh(false);
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
            await createPantryItem(pantry.id, body);
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

    function handleRebalance() {
        fetchPantryRebalance();
        showAlert(VariantType.SUCCESS, "Pantry rebalanced successfully! ");
    }

    function renderPantryList() {
        return (
            <Stack gap={2}>
                <div className="d-flex justify-content-end"><Button variant="primary" size="sm" onClick={handleRebalance} title='Analyse and provision items'>Balance Inventory</Button></div>
                <div><PantryItemList key={refresh} pantryId={pantry.id} /></div>
            </Stack>
        )
    }

    return (
        <Stack gap={3}>
            <div></div>
            <div>
                {mode === "edit" && isLoading ?
                    <h6>Loading...</h6> :
                    <PantryForm pantry={pantry} handleSave={handleSave} />}
            </div>
            <div>
                <ProductSearchBar handleSelectAction={handleAddItem} addButtonVisible={true} />
            </div>
            <div>
                {isLoading && !pantry ?
                    <h6>Loading...</h6> : renderPantryList()}
            </div>
        </Stack>
    );
}