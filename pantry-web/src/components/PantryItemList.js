import { getPantryItems, deletePantryItem, updatePantryItem } from '../services/apis/mypantry/requests/PantryRequests.js';
import React, { useEffect, useState } from 'react';
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
import useAlert from '../hooks/useAlert.js';
import { useTranslation } from 'react-i18next';
import { Card, Col, Row } from 'react-bootstrap';
import CurrentQuantityField from './CurrentQuantityField.js';

function PantryItemList({ pantryId, setIsEmpty }) {

    const { t } = useTranslation(['pantry', 'common']);

    const [isLoading, setIsLoading] = useState(true);
    const [refresh, setRefresh] = useState(true);
    const [pantryItems, setPantryItems] = useState([]);
    const [filteredItems, setFilteredItems] = useState([]);
    const [searchText, setSearchText] = useState("");
    const { showAlert } = useAlert();

    useEffect(() => {
        setIsLoading(true);
        if (pantryId && pantryId > 0 && refresh) {
            fetchPantryItems();
        }
    }, [pantryId, refresh])

    useEffect(() => {
        filter(searchText);
        setIsEmpty(!pantryItems || pantryItems.length === 0);
    }, [pantryItems])

    async function fetchPantryItems() {
        try {
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
            await updatePantryItem(item.pantry.id, item.product.id, item);
        } catch (error) {
            showAlert(VariantType.DANGER, error.message);
        } finally {
            setRefresh(true);
        }
    }

    function handleRemove(item) {
        fetchDeletePantryItem(item.pantry.id, item.product.id)
        showAlert(VariantType.SUCCESS, t('delete-item-success'));
        return
    }

    function handleSave(item) {
        fetchUpdatePantryItem(item);
        //showAlert(VariantType.SUCCESS, t('update-item-success'));
    }

    function filter(text) {
        if (text && text.length > 0)
            setFilteredItems(pantryItems.filter(item => item.product.code.toUpperCase().includes(text.toUpperCase())));
        else
            setFilteredItems(pantryItems);

        setSearchText(text);
    }

    function renderCards() {
        return filteredItems.map(item => renderCard(item))
    }

    function renderCard(item) {
        if (item) {
            return (
                <Col key={item.pantry.id + "-" + item.product.id} className="d-flex flex-column g-2">
                    <Card className="card1 flex-fill">
                        <Card.Body className="d-flex  flex-column h-100">

                            <div className="d-flex justify-content-between" >
                                <div className='d-flex gap-2'>
                                    <Image src={food} width={20} height={20} rounded />
                                    <Card.Title as="h6" className='mb-0'><span className='text-wrap'>{camelCase(item.product.code)}</span></Card.Title>
                                </div>
                                <Button onClick={() => handleRemove(item)} variant="link" className='pt-0 pb-0 pe-0'><BsTrash className='icon' /></Button>
                            </div>

                            <div className='d-flex gap-2 mb-2'>
                                <span className="small">{item.product.description}</span>
                                <span className="small">{item.product.size}</span>
                            </div>

                            <div className="d-flex justify-content-evenly align-items-end mt-auto">
                                <div className="d-flex flex-column align-items-center">
                                    <h6 className='simple-title'>{t('ideal', { ns: 'common' })}</h6>
                                    <NumericField key={item.idealQty} object={item} attribute="idealQty" onValueChange={handleSave} />
                                </div>
                                <div className="d-flex flex-column align-items-center ">
                                    <h6 className='simple-title'>{t('current', { ns: 'common' })}</h6>
                                    <NumericField key={item.currentQty} object={item} attribute="currentQty" onValueChange={handleSave} />
                                </div>

                            </div>

                            <div className="d-flex gap-3 mt-2">
                                <span className="small" hidden={item.lastProvisioning === null}>{t('provisioned', { ns: 'common' })}: {item.provisionedQty}</span>
                                <span className="small" hidden={item.lastProvisioning === null}>{t('provisioned-on', { ns: 'common' })}: {item.lastProvisioning ? t('datetime', { ns: "common", date: new Date(item.lastProvisioning) }) : ""}</span>
                            </div>

                        </Card.Body>
                    </Card>
                </Col>
            )
        }
    }

    return (
        <div>
            <Form.Control size="sm" type="text" id="search" className="form-control mb-1" value={searchText} placeholder={t('placeholder-search-items', { ns: 'common' })} onChange={(e) => filter(e.target.value)} />

            <Row key="row-0" xs={1} md={2} lg={3} className='m-0'>
                {renderCards()}
            </Row>
        </div>
    );
}

export default PantryItemList;