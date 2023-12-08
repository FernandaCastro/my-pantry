import Row from 'react-bootstrap/Row';
import Col from 'react-bootstrap/Col';
import { ListGroup } from 'react-bootstrap';
import { SetAlertContext } from '../../services/context/PantryContext.js';
import { getPantryItems, deletePantryItem, updatePantryItem } from '../../services/apis/mypantry/fetch/requests/PantryRequests.js';
import React, { useEffect, useState, useContext } from 'react';
import VariantType from '../components/VariantType.js';
import Button from 'react-bootstrap/Button';
import Stack from 'react-bootstrap/Stack';
import { BsCheck2All, BsTrash } from "react-icons/bs";
import NumericField from '../components/NumericField.js'
import Table from 'react-bootstrap/Table';

function PantryItemList({ pantryId }) {

    const [isLoading, setIsLoading] = useState(true);
    const [refresh, setRefresh] = useState(true);
    const [pantryItems, setPantryItems] = useState([]);
    const setAlert = useContext(SetAlertContext);

    useEffect(() => {
        if (pantryId && pantryId > 0 && refresh) {
            fetchPantryItems();
        }
    }, [pantryId, refresh])

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
        showAlert(VariantType.SUCCESS, "Item updated successfully!");
    }

    function showAlert(type, message) {
        setAlert({
            show: true,
            type: type,
            message: message
        })
    }

    function renderItems() {
        if (pantryItems && pantryItems.length > 0) return pantryItems.map((item) => renderItem(item))
    }

    function renderItem(item) {
        return (
            <tr key={item.pantryId + ":" + item.productId} className="border border-primary-subtle align-middle">
                <td colSpan="2">
                    <span>{item.product.code}</span>
                    <p className='d-none d-md-block'>
                        <br /> {item.product.description} - {item.product.size}
                    </p>
                </td>
                <td><NumericField object={item} attribute="idealQty" /></td>
                <td><NumericField object={item} attribute="currentQty" /></td>
                <td><span>{item.provisionedQty}</span></td>
                <td>
                    <Stack direction="horizontal" gap={0} className="d-flex justify-content-end align-items-start'">
                        <div><Button onClick={() => handleSave(item)} variant="link" className='pt-0 pb-0 ps-0'><BsCheck2All /></Button></div>
                        <div><Button onClick={() => handleRemove(item)} variant="link" className='pt-0 pb-0 pe-0'><BsTrash /></Button></div>
                    </Stack>
                </td>
            </tr >
        )
    }

    return (
        <Table variant="primary" hover>
            <tbody>
                <tr key="0:0" className="border border-primary-subtle align-middle">
                    <th colSpan="2" scope="col"><span>Code/Desc.</span></th>
                    <th scope="col"><span>Ideal</span></th>
                    <th scope="col"><span>Current</span></th>
                    <th scope="col"><span>Prov.</span></th>
                    <th scope="col"><span /></th>
                </tr>
                {renderItems()}
            </tbody>
        </Table>
    );
}

export default PantryItemList;