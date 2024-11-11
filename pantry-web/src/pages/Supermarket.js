import { useEffect, useState } from 'react'
import { FormCheck, Stack, Button, Form, Image, Card, Col, Row } from "react-bootstrap";
import { BsPencil, BsTrash, BsCheck2All, BsXLg } from "react-icons/bs";
import { useTranslation } from 'react-i18next';
import { CategoryDragDrop } from '../components/CategoryDragDrop';
import useAlert from '../hooks/useAlert.js';
import VariantType from '../components/VariantType.js';
import Select from '../components/Select';
import { getAccountGroupList } from '../api/mypantry/account/accountService.js';
import { getSupermarketsByGroup, createSupermarket, updateSupermarket, deleteSupermarket } from '../api/mypantry/purchase/purchaseService'
import iconSupermarket from '../assets/images/supermarket-gradient.png';
import Modal from 'react-bootstrap/Modal';
import { useGlobalLoading } from '../hooks/useGlobalLoading';

export function Supermarket() {

    const { t } = useTranslation(['supermarket', 'common']);

    const [supermarkets, setSupermarkets] = useState([]);

    const [accountGroupOption, setAccountGroupOption] = useState({ value: 0, label: "" });
    const [accountGroupOptions, setAccountGroupOptions] = useState([]);

    const [selectedSupermarket, setSelectedSupermarket] = useState({ id: 0, name: "", categories: [] });
    const [editSupermarketId, setEditSupermarketId] = useState(0);

    const [showNew, setShowNew] = useState(false);
    const [refresh, setRefresh] = useState(false);
    const [showModal, setShowModal] = useState(false);
    const [supermarketToDelete, setSupermarketToDelete] = useState();

    const { showAlert } = useAlert();
    const { isLoading, setIsLoading } = useGlobalLoading();

    useEffect(() => {
        fetchAccountGroups();
    }, [])

    useEffect(() => {
        if (refresh || accountGroupOption.value > 0) fetchSupermarkets();
    }, [refresh, accountGroupOption.value])

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
            setAccountGroupOption(list[0]);
        } catch (error) {
            showAlert(VariantType.DANGER, error.message);
        } finally {
            setIsLoading(false);
        }
    }

    async function fetchSupermarkets() {
        setIsLoading(true);
        try {
            const res = await getSupermarketsByGroup(accountGroupOption.value);

            setSupermarkets(res);

            if (selectedSupermarket.id === 0 && res && res.length > 0) {
                setSelectedSupermarket(res[0]);
            }
            else if (selectedSupermarket.id > 0 && (!res || res.length === 0)) {
                setSelectedSupermarket({ id: 0, name: "", categories: [] })
            }
        } catch (error) {
            showAlert(VariantType.DANGER, error.message);
        } finally {
            setIsLoading(false);
        }
    }

    async function fetchCreate() {
        try {
            setRefresh(false);
            setIsLoading(true);

            const stringified = {
                ...selectedSupermarket,
                accountGroup: { id: accountGroupOption.value }
            };
            const res = await createSupermarket(stringified);
            if (res) {
                setSelectedSupermarket(res);
            }
            setEditSupermarketId(0);
            setShowNew(false);

            showAlert(VariantType.SUCCESS, t("create-supermarket-success"));
        } catch (error) {
            showAlert(VariantType.DANGER, error.message);
        } finally {
            setRefresh(true);
            setIsLoading(false);
        }
    }

    async function fetchEdit() {
        try {
            setRefresh(false);
            setIsLoading(true);
            await updateSupermarket(selectedSupermarket.id, selectedSupermarket);
            setEditSupermarketId(0);

            showAlert(VariantType.SUCCESS, t("update-supermarket-success"));
        } catch (error) {
            showAlert(VariantType.DANGER, error.message);
        } finally {
            setRefresh(true);
            setIsLoading(false);
        }
    }

    async function fetchDelete(id) {
        try {
            setRefresh(false);
            setIsLoading(true);
            await deleteSupermarket(id);
            setSelectedSupermarket({ id: 0, name: "", categories: [] });

            showAlert(VariantType.SUCCESS, t("delete-supermarket-success"));
        } catch (error) {
            showAlert(VariantType.DANGER, error.message);
        } finally {
            setRefresh(true);
            setIsLoading(false);
        }
    }

    function showConfirmDeletion(item) {
        setSupermarketToDelete(item);
        setShowModal(!showModal);
    }

    function handleRemove() {
        if (supermarketToDelete) {
            fetchDelete(supermarketToDelete.id);
        }
        setShowModal(false);
    }

    function handleClickEdit(item) {
        setSelectedSupermarket(item);
        setEditSupermarketId(item.id);
    }

    function handleClickNew() {
        const newSupermarket = { id: 0, name: "", categories: [] };
        setSelectedSupermarket(newSupermarket);
        setShowNew(true);
    }

    function handleCategoriesChange(list) {
        setSelectedSupermarket(prev => ({ ...prev, categories: list }));
    }

    function renderCards() {

        const elements = [];

        if (showNew) { elements.push(renderNewCard()) }

        supermarkets.map((item) => (elements.push(renderCard(item))))

        return elements;
    }

    function renderNewCard() {

        return (
            <Col key={0} className="d-flex flex-column g-2">
                <Card className="card1 flex-fill">
                    <Card.Body className="d-flex  flex-column h-100">
                        <div className="d-flex justify-content-start aling-items-center">
                            <FormCheck type="radio" checked={false} disabled={true} />
                            <Form.Control className="ms-1" size="sm" type="text" placeholder='Supermarket Name' defaultValue={selectedSupermarket.name} onChange={(e) => setSelectedSupermarket({ ...selectedSupermarket, name: e.target.value })} />
                            <Button onClick={fetchCreate} variant="link" className='pe-0' disabled={selectedSupermarket.name.length === 0}><BsCheck2All className='icon' /></Button>
                            <Button onClick={() => setShowNew(false)} variant='link' title='Clear text'><BsXLg className='icon' /></Button>
                        </div>
                    </Card.Body>
                </Card>
            </Col >
        )
    }

    function renderEditCard() {
        return (
            <Stack direction="horizontal" gap={1} className="d-flex justify-content-start m-0 p-0">
                <div><FormCheck key={selectedSupermarket.id} type="radio" checked={true} disabled={true} /></div>
                <div className='flex-grow-1'>
                    <Form.Control size="sm" type="text" defaultValue={selectedSupermarket.name}
                        onChange={(e) => setSelectedSupermarket({ ...selectedSupermarket, name: e.target.value })} /></div>

                <div><Button onClick={fetchEdit} variant="link" className='pe-0'><BsCheck2All className='icon' /></Button></div>
                <div><Button onClick={() => setEditSupermarketId(0)} variant='link' title='Clear text'><BsXLg className='icon' /></Button></div>
            </Stack >
        )
    }

    function renderViewCard(item) {
        return (
            <div className='d-flex justify-content-between'>
                <FormCheck key={selectedSupermarket.id} type="radio" defaultValue={selectedSupermarket && selectedSupermarket.id === item.id}
                    defaultChecked={selectedSupermarket && selectedSupermarket.id === item.id}
                    onChange={() => setSelectedSupermarket(item)} style={{ color: "hsl(219, 11%, 25%)" }}
                    label={item.name}
                    disabled={editSupermarketId > 0} />

                <div className="d-flex justify-content-end gap-4">
                    <Button className="m-0 p-0 pe-2" onClick={() => handleClickEdit(item)} variant="link" disabled={editSupermarketId > 0}><BsPencil className='icon' /></Button>
                    <Button className="m-0 p-0" onClick={() => showConfirmDeletion(item)} variant="link" disabled={setEditSupermarketId > 0}><BsTrash className='icon' /></Button>
                </div>
            </div>
        )
    }
    function renderCard(item) {
        return (
            <Col key={item.id} className="d-flex flex-column g-2">
                <Card className="card1 flex-fill">
                    <Card.Body className="d-flex flex-column h-100">

                        {editSupermarketId === item.id ? renderEditCard() : renderViewCard(item)}

                    </Card.Body>
                </Card>
            </Col>
        )
    }

    const accountGroupStyles = {
        singleValue: (provided, state) => ({
            ...provided,
            color: 'var(--text-color)',
        }),

        control: (provided, state) => ({
            ...provided,
            backgroundColor: 'var(--background)',
            borderColor: 'var(--border-color)',
            minHeight: '45px',
            height: '45px',
            boxShadow: null,
            fontSize: '16px',
            "&:hover": {
                borderColor: 'var(--link-color)'
            }
        }),

        valueContainer: (provided, state) => ({
            ...provided,
            height: '45px',
            padding: '0 6px',
        }),

        placeholder: (provided, state) => ({
            ...provided,
            fontSize: '16px',
            color: 'var(--text-color-2)',
        }),

        indicatorSeparator: state => ({
            display: 'none',
        }),

        indicatorsContainer: (provided, state) => ({
            ...provided,
            height: '40px',
        }),

        menu: (provided, state) => ({
            ...provided,
            backgroundColor: 'var(--background)',
        }),

        option: (provided, { data, isDisabled, isFocused, isSelected }) => ({
            ...provided,
            backgroundColor: isSelected ? 'var(--highlight-item-list)' : 'var(--background)',
            color: 'var(--text-color)',
            minHeight: '45px',
            height: '45px',
            fontSize: '16px',
            "&:hover": {
                color: 'var(--highlight-text)'
            }
        }),
    };


    return (
        <>
            <Stack gap={3}>
                <div className="d-flex align-items-end mt-4">
                    <Image src={iconSupermarket} width={40} height={40} className="ms-2 me-3" />
                    <h6 className='title flex-grow-1'>{t("supermarket-title")}</h6>
                    <Button bsPrefix="btn-custom" size="sm" onClick={handleClickNew} className="pe-2 ps-2" disabled={editSupermarketId > 0 || showNew}><span>{t("btn-create")}</span></Button>
                </div>
                <div className='d-flex flex-row mt-5 align-items-center gap-2'>
                    <div><span className="title">{t('group-title')}</span></div>
                    <Form.Group className="mb-2 flex-grow-1" controlId="formAccountGroups" size="sm">
                        <Select name="accountGroup" key={accountGroupOption?.value}
                            defaultValue={accountGroupOption}
                            options={accountGroupOptions}
                            onChange={setAccountGroupOption}
                            customStyles={accountGroupStyles} />
                    </Form.Group>
                </div>
                <Row xs={1} md={2} lg={3} className='m-0'>
                    {renderCards()}
                </Row>
                <div className="mt-4">
                    <CategoryDragDrop key={selectedSupermarket.id} innitialList={selectedSupermarket.categories} handleListChange={handleCategoriesChange} disabled={editSupermarketId === 0 && !showNew} />
                </div>
            </Stack>
            <Modal className='custom-alert' size='sm' show={showModal} onHide={() => setShowModal(false)} >
                <Modal.Body className='custom-alert-body pb-0'>
                    <span className='title text-center'>
                        {t('delete-supermarket-alert', { supermarket: supermarketToDelete?.name })}
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