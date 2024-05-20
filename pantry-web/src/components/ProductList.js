import React, { useState, useEffect } from 'react';
import Table from 'react-bootstrap/Table';
import { getProductList, deleteProduct } from '../services/apis/mypantry/requests/PantryRequests.js';
import VariantType from './VariantType.js';
import useAlert from '../hooks/useAlert.js';
import Form from 'react-bootstrap/Form';
import Stack from 'react-bootstrap/Stack';
import Image from 'react-bootstrap/Image';
import food from '../assets/images/healthy-food.png';
import Button from 'react-bootstrap/Button';
import { BsPencil, BsTrash } from "react-icons/bs";
import { camelCase } from '../services/Utils.js';
import { FormCheck } from "react-bootstrap";
import OverlayTrigger from 'react-bootstrap/OverlayTrigger';
import Tooltip from 'react-bootstrap/Tooltip';

function ProductList({ disabled, onEdit, onRemove }) {

    const [isLoading, setIsLoading] = useState(true);
    const [refresh, setRefresh] = useState(true);
    const [productList, setProductList] = useState([]);
    const [filteredItems, setFilteredItems] = useState([]);
    const [searchText, setSearchText] = useState("");
    const { showAlert } = useAlert();
    const [showGroup, setShowGroup] = useState(true);
    const [expandProdDetail, setExpandProdDetail] = useState(false);

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

    function renderItems() {
        if (productList && productList.length > 0) return filteredItems.map((item) => renderItem(item))
    }

    function renderItem(item) {
        return (
            <tr key={item.id} >
                <td>
                    <div className="d-flex flex-row gap-1">
                        <div><Image src={food} width={20} height={20} rounded /></div>
                        <div><span className='text-wrap'>{camelCase(item.code)}</span></div>
                    </div>
                    <div id="productDetail" style={{ display: expandProdDetail ? 'block' : 'none' }}>
                        <p hidden={item.description === ''} className='ms-4 mb-0'>{item.description}  {item.size}</p>
                    </div>
                </td>
                <td hidden={!showGroup}><span className='text-wrap'>{item.accountGroup.name}</span></td>
                <td className="border-start-0">
                    <div className="d-flex flex-row justify-content-end align-items-start">
                        <Button onClick={() => onEdit(item)} variant="link" disabled={disabled}><BsPencil className='icon' /></Button>
                        <Button onClick={() => handleRemove(item.id)} variant="link" disabled={disabled}><BsTrash className='icon' /></Button>
                    </div>
                </td>
            </tr >
        )
    }

    return (
        <div>
            <Form.Control size="sm" type="text" id="search" className="form-control mb-1 input-custom" value={searchText} placeholder="Seacrh for items here" onChange={(e) => filter(e.target.value)} />
            <div className="scroll-product">
                <Table size='sm'>
                    <thead>
                        <tr key="0:0" >
                            <th >
                                <div className='d-flex justify-content-start align-items-center gap-2'>
                                <OverlayTrigger
                                        placement="bottom"
                                        overlay={
                                            <Tooltip className='custom-tooltip'>
                                                Show Product Detail
                                            </Tooltip>
                                        }
                                    >
                                    <FormCheck
                                        className='d-block form-switch'
                                        defaultChecked={expandProdDetail}
                                        onChange={() => setExpandProdDetail(!expandProdDetail)} />
                                        </OverlayTrigger>
                                    <h6 className='title'>Products</h6>
                                </div>

                            </th>
                            <th hidden={!showGroup} />
                            <th >
                                <div className='d-flex justify-content-end align-items-center gap-2 pe-2'>
                                    <OverlayTrigger
                                        placement="bottom"
                                        overlay={
                                            <Tooltip className="custom-tooltip">
                                                Show Account Group
                                            </Tooltip>
                                        }
                                    >
                                        <FormCheck
                                            className='d-block form-switch'
                                            defaultChecked={showGroup}
                                            onChange={() => setShowGroup(!showGroup)}
                                        />
                                    </OverlayTrigger>
                                </div>
                            </th>
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