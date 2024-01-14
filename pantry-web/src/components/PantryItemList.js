import { AlertContext } from '../services/context/AppContext.js';
import { getPantryItems, deletePantryItem, updatePantryItem } from '../services/apis/mypantry/requests/PantryRequests.js';
import React, { useEffect, useState, useContext } from 'react';
import VariantType from '../components/VariantType.js';
import Button from 'react-bootstrap/Button';
import { BsTrash } from "react-icons/bs";
import NumericField from './NumericField.js'
import Table from 'react-bootstrap/Table';
import Form from 'react-bootstrap/Form';
import Image from 'react-bootstrap/Image';
import food from '../assets/images/healthy-food.png'
import Stack from 'react-bootstrap/Stack';
import { camelCase } from '../services/Utils.js';

function PantryItemList({ pantryId }) {

    const [isLoading, setIsLoading] = useState(true);
    const [refresh, setRefresh] = useState(true);
    const [pantryItems, setPantryItems] = useState([]);
    const [filteredItems, setFilteredItems] = useState([]);
    const [searchText, setSearchText] = useState("");
    const { alert, setAlert } = useContext(AlertContext);

    useEffect(() => {
        if (pantryId && pantryId > 0 && refresh) {
            fetchPantryItems();
        }
    }, [pantryId, refresh])

    useEffect(() => {
        filter(searchText);
    }, [pantryItems])

    async function fetchPantryItems() {
        try {
            setIsLoading(true);
            const res = await getPantryItems(pantryId);
            setPantryItems(res);
            setRefresh(false);
            setIsLoading(false);
        } catch (error) {
            showAlert(VariantType.DANGER, error.message);
        }
    }

    async function fetchDeletePantryItem(pantryId, productId) {
        try {
            await deletePantryItem(pantryId, productId);
            setRefresh(true);
        } catch (error) {
            showAlert(VariantType.DANGER, error.message);
        }
    }

    async function fetchUpdatePantryItem(item) {
        try {
            await updatePantryItem(item.pantryId, item.productId, item);
            setRefresh(true);
        } catch (error) {
            showAlert(VariantType.DANGER, error.message);
        }
    }

    function handleRemove(item) {
        fetchDeletePantryItem(item.pantryId, item.productId)
        showAlert(VariantType.SUCCESS, "Item removed successfully!");
        return
    }

    function handleSave(item) {
        fetchUpdatePantryItem(item);
        //showAlert(VariantType.SUCCESS, "Item updated successfully!");
    }

    function showAlert(type, message) {
        setAlert({
            show: true,
            type: type,
            message: message
        })
    }

    function renderItems() {
        return filteredItems.map((item) => renderItem(item))
    }

    function renderItem(item) {
        return (
            <tr key={item.productId} className="border border-primary-subtle align-middle">
                <td>
                    <Stack direction="horizontal" gap={2}>
                        <div><Image src={food} width={20} height={20} rounded /></div>
                        <div><span>{camelCase(item.product.code)}</span></div>
                    </Stack>
                    <p className='d-none d-md-block ms-4 mb-0' hidden={item.product.description === ''}>
                        {item.product.description}  {item.product.size}
                    </p>
                </td>
                <td><NumericField object={item} attribute="idealQty" onValueChange={handleSave} /></td>
                <td><NumericField object={item} attribute="currentQty" onValueChange={handleSave} /></td>
                <td className='ms-0 pe-0'><span>{item.provisionedQty}</span></td>
                <td className='ms-0 ps-0 me-2 pe-2'>
                    <Button onClick={() => handleRemove(item)} variant="link" className='pt-0 pb-0 pe-0'><BsTrash /></Button>
                </td>
            </tr >
        )
    }

    function filter(text) {
        if (text && text.length > 0)
            setFilteredItems(pantryItems.filter(item => item.product.code.toUpperCase().includes(text.toUpperCase())));
        else
            setFilteredItems(pantryItems);

        setSearchText(text);
    }

    return (
        <div>
            <Form.Control size="sm" type="text" id="search" className="form-control mb-1" value={searchText} placeholder="Seacrh for items here" onChange={(e) => filter(e.target.value)} />
            <div className='scroll-pantryItems'>
                <Table>
                    <thead>
                        <tr key="0:0" className="align-middle">
                            <th><span className='title'>Code/Desc.</span></th>
                            <th><span className='title'>Ideal</span></th>
                            <th ><span className='title'>Current</span></th>
                            <th className='ms-0 ps-0'><span className="title">Prov.</span></th>
                            <th className='ms-0 ps-0 me-2 pe-2'><span /></th>
                        </tr>
                    </thead>
                    <tbody >
                        {renderItems()}
                    </tbody>
                </Table>
            </div>
        </div>
    );
}

export default PantryItemList;