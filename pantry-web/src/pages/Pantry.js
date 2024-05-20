import React, { useEffect, useState } from 'react';
import { useParams } from 'react-router';
import { getPantry, updatePantry, createPantry, createPantryItem, getPantryRebalance } from '../services/apis/mypantry/requests/PantryRequests.js';
import Stack from 'react-bootstrap/Stack';
import VariantType from '../components/VariantType.js';
import useAlert from '../hooks/useAlert.js';
import PantryForm from '../components/PantryForm.js';
import ProductSearchBar from '../components/ProductSearchBar.js'
import PantryItemList from '../components/PantryItemList.js';
import Button from 'react-bootstrap/Button';
import { BsChevronDown } from "react-icons/bs";
import Collapse from 'react-bootstrap/Collapse';
import { getAccountGroupList } from '../services/apis/mypantry/requests/AccountRequests.js';

export default function Pantry({ mode }) {

    let { id } = useParams();
    const [accountGroupOptions, setAccountGroupOptions] = useState([]);
    const [showPantry, setShowPantry] = useState(true);
    const [isEmpty, setIsEmpty] = useState(true);

    const [pantry, setPantry] = useState(
        {
            id: 0,
            name: "",
            type: "R",
            isActive: true,
            accountGroup: { id: 0 }
        });

    const [isLoading, setIsLoading] = useState(false);
    const [refresh, setRefresh] = useState(false);
    const { showAlert } = useAlert();

    useEffect(() => {
        if (id && mode === 'edit') {
            fetchPantry();
        }

        if (!accountGroupOptions || accountGroupOptions.length === 0) {
            fetchAccountGroups();
        }
    }, [])


    async function fetchAccountGroups() {
        setIsLoading(true);
        try {
            const res = await getAccountGroupList();

            var list = [];
            res.forEach(group => {
                list = [...list,
                {
                    value: group.id,
                    label: group.name
                }]
            });

            setAccountGroupOptions(list);
            setIsLoading(false);
        } catch (error) {
            showAlert(VariantType.DANGER, error.message);
        }
    }

    async function fetchPantry() {
        setIsLoading(true);
        try {
            const res = await getPantry(id);
            setPantry(res);

        } catch (error) {
            showAlert(VariantType.DANGER, error.message);
        } finally {
            setIsLoading(false);
        }
    }

    async function fetchPantryRebalance() {
        setIsLoading(true);
        setRefresh(true);
        try {
            await getPantryRebalance(id);

            showAlert(VariantType.SUCCESS, "Pantry rebalanced successfully! ");
        } catch (error) {
            showAlert(VariantType.DANGER, error.message);
        } finally {
            setRefresh(false);
            setIsLoading(false);
        }
    }

    async function fetchSavePantry(body) {
        setIsLoading(true);
        try {
            const res = mode === 'new' ? await createPantry(body) : await updatePantry(id, body);
            setPantry(res);

            const msg = mode === 'edit' ? "updated!" : "created!";
            showAlert(VariantType.SUCCESS, "Pantry successfully " + msg);

        } catch (error) {
            showAlert(VariantType.DANGER, error.message);
        } finally {
            setIsLoading(false);
        }
    }

    async function fetchSavePantryItem(body) {
        setIsLoading(true);
        setRefresh(true);
        try {
            await createPantryItem(pantry.id, body);
            showAlert(VariantType.SUCCESS, "Item added successfully.");
        } catch (error) {
            showAlert(VariantType.DANGER, error.message);
        } finally {
            setRefresh(false);
            setIsLoading(false);
        }
    }

    function handleAddItem(product) {
        const body = {
            pantry: pantry,
            product: product
        }
        fetchSavePantryItem(body);
    }

    function handleRebalance() {
        fetchPantryRebalance();
    }

    function renderPantryList() {
        return (
            <Stack gap={2}>
                <div className="d-flex justify-content-end"><Button bsPrefix='btn-custom' size="sm" onClick={handleRebalance} title='Analyse and provision items' disabled={isEmpty}>Balance Inventory</Button></div>
                <div><PantryItemList key={refresh} pantryId={pantry.id} setIsEmpty={setIsEmpty} /></div>
            </Stack>
        )
    }

    return (
        <Stack gap={3}>
            <div></div>
            <div>
                <div className='d-flex justify-content-start align-items-center gap-2' onClick={() => setShowPantry(!showPantry)}>
                    <h6 className="text-start fs-6 lh-lg title">Pantry Details </h6>
                    <BsChevronDown className='icon' />
                </div>
                <Collapse in={showPantry} >
                    <div>
                        {mode === "edit" && isLoading ?
                            <h6>Loading...</h6> :
                            <PantryForm key={pantry.id} pantry={pantry} handleSave={fetchSavePantry} accountGroupOptions={accountGroupOptions} />}
                    </div>
                </Collapse>
            </div>
            <div className="add-product" style={{ display: pantry && pantry.id > 0 ? 'block' : 'none' }}>
                <ProductSearchBar accountGroupId={pantry.accountGroup.id} accountGroupOptions={accountGroupOptions} handleSelectAction={handleAddItem} addButtonVisible={true} />
            </div>
            <div className="pantry-items" style={{ display: pantry && pantry.id > 0 ? 'block' : 'none' }}>
                {isLoading && !pantry ?
                    <h6>Loading...</h6> : renderPantryList()}
            </div>
        </Stack>
    );
}