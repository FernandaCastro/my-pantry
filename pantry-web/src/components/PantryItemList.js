import { deletePantryItem, updatePantryItem, fetchPantryItems } from '../api/mypantry/pantry/pantryService.js';
import React, { useEffect, useRef, useState } from 'react';
import VariantType from '../components/VariantType.js';
import Button from 'react-bootstrap/Button';
import { BsTrash } from "react-icons/bs";
import NumericField from './NumericField.js'
import Form from 'react-bootstrap/Form';
import Image from 'react-bootstrap/Image';
import food from '../assets/images/healthy-food.png'
import { camelCase } from '../util/utils.js';
import useAlert from '../state/useAlert.js';
import { useTranslation } from 'react-i18next';
import { Card, Col, Row } from 'react-bootstrap';
import Modal from 'react-bootstrap/Modal';
import { Loading } from './Loading.js';

function PantryItemList({ refetch, setRefetch, pantryId, setIsEmpty }) {

    const { t } = useTranslation(['pantry', 'common']);

    const [pantryItems, setPantryItems] = useState([]);
    const [filteredItems, setFilteredItems] = useState([]);
    const [searchText, setSearchText] = useState("");
    const { showAlert } = useAlert();
    const [isLoading, setIsLoading] = useState(false);
    const [showModal, setShowModal] = useState(false);
    const [itemToDelete, setItemToDelete] = useState();
    const abortController = useRef(null);

    const [timeoutId, setTimeoutId] = useState(null);

    useEffect(() => {
        if (pantryId && pantryId > 0 & refetch) {
            loadPantryItems();
        }
    }, [pantryId, refetch])

    useEffect(() => {
        filter(searchText);
        setIsEmpty(!pantryItems || pantryItems.length === 0);
    }, [pantryItems])

    async function loadPantryItems() {
        //Avoid racing condition (TODO: check lib react-query )
        abortController.current?.abort();
        abortController.current = new AbortController();

        try {
            setIsLoading(true);
            const res = await fetchPantryItems(pantryId, abortController.current?.signal);
            setPantryItems(res);
            setRefetch(false);
        } catch (error) {
            showAlert(VariantType.DANGER, error.message);
        } finally {
            setIsLoading(false);
        }
    }

    async function fetchDeletePantryItem(pantryId, productId) {
        try {
            await deletePantryItem(pantryId, productId);
            setRefetch(true);
        } catch (error) {
            showAlert(VariantType.DANGER, error.message);
        }
    }

    async function fetchUpdatePantryItem(item) {
        try {
            const res = await updatePantryItem(item.pantry.id, item.product.id, item);
            updatePantryItems(res);
        } catch (error) {
            showAlert(VariantType.DANGER, error.message);
        }
    }

    function updatePantryItems(res) {
        if (res) {
            setPantryItems(prevItems =>
                prevItems.map(item =>
                    item.pantry.id === res.pantry.id && item.product.id === res.product.id ? res : item
                )
            );
        }
    }

    function showConfirmDeletion(item) {
        setItemToDelete(item);
        setShowModal(!showModal);
    }

    function handleRemove(item) {
        if (itemToDelete) {
            fetchDeletePantryItem(itemToDelete.pantry.id, itemToDelete.product.id)
            showAlert(VariantType.SUCCESS, t('delete-item-success'));
        }
        setShowModal(false);

        return
    }

    function handleSave(item) {
        //Debouncing: limita a quantidade de vezes que uma função pode ser chamada

        if (timeoutId) { // clean the previous timeout, if exists
            clearTimeout(timeoutId);
        }

        const id = setTimeout(() => {
            fetchUpdatePantryItem(item);
            //showAlert(VariantType.SUCCESS, t('update-item-success'));

        }, 300);

        setTimeoutId(id);
    }

    useEffect(() => {
        return () => {
            if (timeoutId) {
                clearTimeout(timeoutId); //Clean the timeout when unloading the component
            }
        };
    }, [timeoutId]);

    function filter(text) {
        if (text && text.length > 0)
            setFilteredItems(pantryItems.filter(item => item.product.code.toUpperCase().includes(text.toUpperCase())));
        else
            setFilteredItems(pantryItems);

        setSearchText(text);
    }

    function renderCards() {
        return filteredItems?.map(item => renderCard(item))
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
                                <Button onClick={() => showConfirmDeletion(item)} variant="link" className='pt-0 pb-0 pe-0'><BsTrash className='icon' /></Button>
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
            <Form.Control type="text" id="search" className="form-control mb-1 search-input" value={searchText} placeholder={t('placeholder-search-items', { ns: 'common' })} onChange={(e) => filter(e.target.value)} />

            <div>
                {isLoading && <Loading />}
                <Row key={refetch} xs={1} md={2} lg={3} className='m-0'>
                    {renderCards()}
                </Row>
            </div>
            <Modal className='custom-alert' size='sm' show={showModal} onHide={() => setShowModal(false)} >
                <Modal.Body className='custom-alert-body pb-0'>
                    <span className='title text-center'>
                        {t('delete-item-alert', { item: camelCase(itemToDelete?.product.code) })}
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

export default PantryItemList;