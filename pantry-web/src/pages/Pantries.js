import React, { useState, useEffect } from 'react';
import { deletePantry, fetchPantryList } from '../api/mypantry/pantry/pantryService.js';
import Stack from 'react-bootstrap/Stack';
import Button from 'react-bootstrap/Button';
import VariantType from '../components/VariantType.js';
import useAlert from '../state/useAlert.js';
import { BsPencil, BsTrash, BsCardChecklist } from "react-icons/bs";
import Modal from 'react-bootstrap/Modal';
import { useTranslation } from 'react-i18next';
import { Card, Col, OverlayTrigger, Row, Tooltip } from 'react-bootstrap';
import iconPantry from '../assets/images/cupboard-gradient.png';
import iconMagicWand from '../assets/images/magic-wand.png';
import Image from 'react-bootstrap/Image';
import useGlobalLoading from '../state/useLoading.js';
import { Link } from 'react-router-dom';
import CustomLink from '../components/CustomLink.js';

export default function Pantries() {

    const { t } = useTranslation(['pantry', 'common']);
    const [pantries, setPantries] = useState([]);
    const [refresh, setRefresh] = useState(true);

    const { showAlert } = useAlert();
    const { setIsLoading } = useGlobalLoading();

    const [showModal, setShowModal] = useState(false);
    const [pantryToDelete, setPantryToDelete] = useState();

    useEffect(() => {
        if (refresh) loadPantries();
    }, [refresh])

    async function loadPantries() {
        setRefresh(true);
        setIsLoading(true);
        try {
            const res = await fetchPantryList();
            setPantries(res);
            setRefresh(false);
        } catch (error) {
            showAlert(VariantType.DANGER, error.message);
        } finally {
            setIsLoading(false);
        }
    }

    async function fetchDeletePantry(id) {
        setRefresh(false);
        try {
            await deletePantry(id);
            setRefresh(true);
            showAlert(VariantType.SUCCESS, t('delete-pantry-success'));
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

    function PantryCard({ item }) {

        return (
            <Col key={item.id} className="d-flex flex-column g-3">
                <Card className="card1 flex-fill">
                    <Card.Body className='d-flex flex-row justify-content-between'>
                        <div>
                            <Card.Title as="h6"><span disabled={!item.isActive}>{item.name}</span></Card.Title>
                            <span disabled={!item.isActive}>{item.accountGroup.name}</span>
                        </div>
                        <div>
                            <Stack direction="horizontal" className="d-flex justify-content-end">
                                <CustomLink to={"/pantries/" + item.id + "/edit"} className="ps-3"><BsPencil className='icon' /></CustomLink>
                                <CustomLink to={"/pantries/" + item.id + "/items"} className="ps-3"><BsCardChecklist className='icon' /></CustomLink>
                                <Button onClick={() => showConfirmDeletion(item)} variant="link"><BsTrash className='icon' /></Button>
                            </Stack>
                        </div>

                    </Card.Body>
                </Card>
            </Col>
        )
    }

    return (
        <>
            <Stack gap={3}>
                <div className="d-flex justify-content-start align-items-end mt-4">
                    <Image src={iconPantry} width={40} height={40} className="ms-2 me-3" />
                    <h6 className='title'>{t('pantry-list-title')}</h6>

                    <OverlayTrigger placement="bottom" delay={{ show: 250, hide: 400 }} overlay={<Tooltip className="custom-tooltip">{t("tooltip-pantry-wizard")}</Tooltip>}>
                        <Link to={"/pantries/new-wizard"} className="pt-0 pb-0 pe-4 ms-auto"><div className="bigger-icon gradient-icon-box-body"><Image src={iconMagicWand} className="bigger-icon" /></div></Link>
                    </OverlayTrigger>
                    <CustomLink bsPrefix="btn-custom" className="ms-2 pe-2 ps-2" to={"/pantries/new"} ><span>{t('btn-new-pantry')}</span></CustomLink>
                </div>
                <div>
                    <Row xs={1} md={2} className="card-group">
                        {pantries?.map((item) => <PantryCard key={item.id} item={item} />)}
                    </Row>
                </div>
            </Stack>
            <Modal className='custom-alert' size='md' show={showModal} onHide={() => setShowModal(false)} >
                <Modal.Body className='custom-alert-body'>
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

