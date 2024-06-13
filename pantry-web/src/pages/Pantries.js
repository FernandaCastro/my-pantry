import React, { useState, useEffect } from 'react';
import { getPantryList, deletePantry } from '../services/apis/mypantry/requests/PantryRequests.js';
import Stack from 'react-bootstrap/Stack';
import Button from 'react-bootstrap/Button';
import VariantType from '../components/VariantType.js';
import useAlert from '../hooks/useAlert.js';
import { BsPencil, BsTrash } from "react-icons/bs";
import Modal from 'react-bootstrap/Modal';
import { useTranslation } from 'react-i18next';
import { Card, Col, Row } from 'react-bootstrap';
import iconPantry from '../assets/images/cupboard-gradient.png';
import Image from 'react-bootstrap/Image';

export default function Pantries() {

    const { t } = useTranslation(['pantry', 'common']);
    const [pantries, setPantries] = useState([]);
    const [refresh, setRefresh] = useState(true);

    const [isLoading, setIsLoading] = useState(true);
    const { showAlert } = useAlert();
    const [showModal, setShowModal] = useState(false);
    const [pantryToDelete, setPantryToDelete] = useState();

    useEffect(() => {
        if (refresh) fetchPantries();
    }, [refresh])

    async function fetchPantries() {
        setRefresh(true);
        setIsLoading(true);
        try {
            const res = await getPantryList();
            setPantries(res);
            setRefresh(false);
            setIsLoading(false);
        } catch (error) {
            showAlert(VariantType.DANGER, error.message);
        }
    }

    async function fetchDeletePantry(id) {
        setRefresh(false);
        try {
            await deletePantry(id);
            setRefresh(true);
            showAlert(VariantType.SUCCESS, t('delete-success'));
        } catch (error) {
            showAlert(VariantType.DANGER, error.message);
        }
    }

    function showConfirmDeletion(item) {
        setPantryToDelete(item);
        setShowModal(!showModal);
    }

    function handleRemove() {
        if (pantryToDelete) {
            fetchDeletePantry(pantryToDelete.id);
        }
        setShowModal(false);
    }

    function renderCards() {
        return (
            <Row xs={1} md={2} className="card-group">
                {pantries.map((item) => {

                    return (
                        <Col key={item.id} className="g-3">
                            <Card className="card1">
                                <Card.Body className='d-flex flex-row justify-content-between'>
                                    <div>
                                        <Card.Title as="h6"><span disabled={!item.isActive}>{item.name}</span></Card.Title>
                                        <span >{item.accountGroup.name}</span>
                                    </div>
                                    <div>
                                        <Stack direction="horizontal" className="d-flex justify-content-end">
                                            <Button href={"/pantries/" + item.id + "/edit"} variant="link"><BsPencil className='icon' /></Button>
                                            <Button onClick={() => showConfirmDeletion(item)} variant="link"><BsTrash className='icon' /></Button>
                                        </Stack>
                                    </div>

                                </Card.Body>
                            </Card>
                        </Col>
                    )
                })}
            </Row>
        )
    }

    return (
        <>
            <Stack gap={3}>
                <div className="d-flex justify-content-start align-items-end mt-4">
                    <Image src={iconPantry} width={40} height={40} className="ms-2 me-3"/>
                    <h6 className='title'>{t('pantry-list-title')}</h6>
                    <Button bsPrefix="btn-custom" href={"/pantries/new"} className="pe-2 ps-2 ms-auto"><span>{t('btn-new-pantry')}</span></Button>
                </div>
                <div>
                    {renderCards()}
                </div>
            </Stack>
            <Modal className='custom-alert' size='sm' show={showModal} onHide={() => setShowModal(false)} >
                <Modal.Body className='custom-alert-body pb-0'>
                    <span className='title text-center'>
                        {t('delete-pantry-alert')}
                    </span>
                </Modal.Body>
                <Modal.Footer className='custom-alert-footer p-2'>
                    <Button bsPrefix='btn-custom' size='sm' onClick={() => setShowModal(false)}><span>{t("btn-no", { ns: "common" })}</span></Button>
                    <Button bsPrefix='btn-custom' size='sm' onClick={handleRemove}><span>{t("btn-yes", { ns: "common" })}</span></Button>
                </Modal.Footer>
            </Modal >

        </>
    )
}

