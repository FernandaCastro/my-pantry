import React, { useState, useEffect } from 'react';
import { getProductList, deleteProduct } from '../services/apis/mypantry/requests/PantryRequests.js';
import VariantType from './VariantType.js';
import useAlert from '../hooks/useAlert.js';
import Form from 'react-bootstrap/Form';
import Image from 'react-bootstrap/Image';
import food from '../assets/images/healthy-food.png';
import Button from 'react-bootstrap/Button';
import { BsPencil, BsTrash } from "react-icons/bs";
import { camelCase } from '../services/utils.js';
import { Card, Col, FormCheck, Row } from "react-bootstrap";
import Modal from 'react-bootstrap/Modal';
import { useTranslation } from 'react-i18next';

function ProductList({ hidden, disabled, onEdit, onRemove }) {

    const { t } = useTranslation(['product', 'common']);

    const [refresh, setRefresh] = useState(true);
    const [productList, setProductList] = useState([]);
    const [filteredItems, setFilteredItems] = useState([]);
    const [searchText, setSearchText] = useState("");
    const { showAlert } = useAlert();
    const [showGroup, setShowGroup] = useState(true);
    const [expandProdDetail, setExpandProdDetail] = useState(false);
    const [showModal, setShowModal] = useState(false);
    const [productToDelete, setProductToDelete] = useState();

    useEffect(() => {
        if (refresh) fetchProductList();
    }, [refresh])

    useEffect(() => {
        filter(searchText);
    }, [productList])

    async function fetchProductList() {
        try {
            const res = await getProductList();
            setProductList(res);
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


    function showConfirmDeletion(item) {
        setProductToDelete(item);
        setShowModal(!showModal);
    }

    function handleRemove() {
        if (productToDelete) {
            fetchDeleteProduct(productToDelete.id);
        }
        setShowModal(false);

        onRemove(productToDelete.id);
    }

    async function fetchDeleteProduct(productId) {
        try {
            setRefresh(false);
            await deleteProduct(productId);
            showAlert(VariantType.SUCCESS, t('delete-product-success'));
            setRefresh(true);
        } catch (error) {
            showAlert(VariantType.DANGER, error.message);
        }
    }

    function renderCards() {
        if (productList && productList.length > 0) return filteredItems.map((item) => renderCard(item))
    }

    function renderCard(item) {
        return (
            <Col key={item.id} className="d-flex flex-column g-2">
                <Card className="card1 flex-fill">
                    <Card.Body className="d-flex  flex-column h-100">

                        <div className="d-flex justify-content-between" >
                            <div className='d-flex gap-2'>
                                <Image src={food} width={20} height={20} rounded />
                                <Card.Title as="h6" className='mb-0'><span className='text-wrap'>{camelCase(item.code)}</span></Card.Title>
                            </div>
                            <div className="d-flex flex-row justify-content-end align-items-start">
                                <Button onClick={() => onEdit(item)} variant="link" disabled={disabled}><BsPencil className='icon' /></Button>
                                <Button onClick={() => showConfirmDeletion(item)} variant="link" disabled={disabled}><BsTrash className='icon' /></Button>
                            </div>
                        </div>

                        <div className="d-flex justify-content-between " >
                            <span className="mt-0 small" hidden={!expandProdDetail}>
                                {item.description} {item.size}
                            </span>
                        </div>

                        <div className="d-flex gap-3 mt-auto">
                            <span className='text-wrap small' hidden={!showGroup}>{item.accountGroup.name}</span>
                        </div>

                    </Card.Body>
                </Card>
            </Col>
        )
    }

    return (
        <div hidden={hidden}>

            <div className='d-flex justify-content-evenly pt-2 pb-3'>
                <FormCheck label={t('tooltip-switch-product-detail', { ns: 'common' })}
                    className='d-block form-switch'
                    defaultChecked={expandProdDetail}
                    onChange={() => setExpandProdDetail(!expandProdDetail)} />
                <FormCheck label={t('tooltip-switch-account-group')}
                    className='d-block form-switch'
                    defaultChecked={showGroup}
                    onChange={() => setShowGroup(!showGroup)}
                />
            </div>
            <Form.Control type="text" id="search" className="form-control mb-1 search-input" value={searchText} placeholder={t('placeholder-search-product')} onChange={(e) => filter(e.target.value)} />

            <Row xs={1} md={2} lg={3} xl={4} className='m-0'>
                {renderCards()}
            </Row>

            <Modal className='custom-alert' size='sm' show={showModal} onHide={() => setShowModal(false)} >
                <Modal.Body className='custom-alert-body pb-0'>
                    <span className='title text-center'>
                        {t('delete-product-alert', { product: camelCase(productToDelete?.code) })}
                    </span>
                </Modal.Body>
                <Modal.Footer className='custom-alert-footer p-2'>
                    <Button bsPrefix='btn-custom' size='sm' onClick={() => setShowModal(false)}><span>{t("btn-no", { ns: "common" })}</span></Button>
                    <Button bsPrefix='btn-custom' size='sm' onClick={handleRemove}><span>{t("btn-yes", { ns: "common" })}</span></Button>
                </Modal.Footer>
            </Modal >
        </div>
    );
}

export default ProductList;