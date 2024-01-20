import React, { useState, useEffect, useContext } from 'react';
import Table from 'react-bootstrap/Table';
import { getProductList, deleteProduct } from '../services/apis/mypantry/fetch/requests/PantryRequests.js';
import { AlertContext } from '../services/context/AppContext.js';
import VariantType from './VariantType.js';
import Form from 'react-bootstrap/Form';
import Stack from 'react-bootstrap/Stack';
import Image from 'react-bootstrap/Image';
import food from '../assets/images/healthy-food.png';
import Button from 'react-bootstrap/Button';
import { BsPencil, BsTrash } from "react-icons/bs";
import { camelCase } from '../services/Utils.js';

function ProductList({ disabled, onEdit, onRemove }) {

    const [isLoading, setIsLoading] = useState(true);
    const [refresh, setRefresh] = useState(true);
    const [productList, setProductList] = useState([]);
    const [filteredItems, setFilteredItems] = useState([]);
    const [searchText, setSearchText] = useState("");
    const { setAlert } = useContext(AlertContext);

    useEffect(() => {
        if (refresh) fetchProductList();
    }, [refresh])

    useEffect(() => {
        filter(searchText);
    }, [productList])

    async function fetchProductList() {
        try {
            setIsLoading(true);
            const res = await getProductList();
            setProductList(res);
            setIsLoading(false);
            setRefresh(false);
        } catch (error) {
            showAlert(VariantType.DANGER, error.message);
        }
    }

    function filter(text) {
        if (text && text.length > 0)
            setFilteredItems(productList.filter(item => item.code.toUpperCase().includes(text.toUpperCase())));
        else
            setFilteredItems(productList);

        setSearchText(text);
    }

    function handleRemove(productId) {
        fetchDeleteProduct(productId);
        onRemove(productId);
    }

    async function fetchDeleteProduct(productId) {
        try {
            setRefresh(false);
            await deleteProduct(productId);
            showAlert(VariantType.SUCCESS, "Product removed successfully ");
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

    function renderItems() {
        if (productList && productList.length > 0) return filteredItems.map((item) => renderItem(item))
    }

    function renderItem(item) {
        return (
            <tr key={item.id} className="align-middle">
                <td>
                    <Stack direction="horizontal" gap={1}>
                        <div><Image src={food} width={20} height={20} rounded /></div>
                        <div><span>{camelCase(item.code)}</span></div>
                    </Stack>
                    <p hidden={item.description === ''} className='ms-4 mb-0'>{item.description}  {item.size}</p>
                </td>
                <td className="border-start-0">
                    <Stack direction="horizontal" gap={1} className="d-flex justify-content-end">
                        <div><Button onClick={() => onEdit(item)} variant="link" disabled={disabled}><BsPencil className='icon' /></Button></div>
                        <div><Button onClick={() => handleRemove(item.id)} variant="link" disabled={disabled}><BsTrash className='icon' /></Button></div>
                    </Stack>
                </td>
            </tr >
        )
    }

    return (
        <div>
            <Form.Control size="sm" type="text" id="search" className="form-control mb-1" value={searchText} placeholder="Seacrh for items here" onChange={(e) => filter(e.target.value)} />
            <div className="scroll-product">
                <Table>
                    <thead>
                        <tr key="0:0" className="align-middle">
                            <th><h6 className='title'>Products</h6></th>
                            <th />
                        </tr>
                    </thead>
                    <tbody>
                        {renderItems()}
                    </tbody>
                </Table>
            </div>
        </div>
    );
}

export default ProductList;