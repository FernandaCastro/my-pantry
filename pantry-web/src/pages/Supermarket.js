import { useEffect, useState } from 'react'
import { FormCheck, Stack, Button, Table, Form } from "react-bootstrap";
import { BsPencil, BsTrash, BsCheck2All, BsXLg } from "react-icons/bs";
import { useTranslation } from 'react-i18next';
import { CategoryDragDrop } from '../components/CategoryDragDrop';
import useAlert from '../hooks/useAlert.js';
import VariantType from '../components/VariantType.js';
import Select from '../components/Select';
import { getAccountGroupList } from '../services/apis/mypantry/requests/AccountRequests.js';
import { getAllSupermarkets, createSupermarket, updateSupermarket, deleteSupermarket } from '../services/apis/mypantry/requests/PurchaseRequests.js'

export function Supermarket() {

    const { t } = useTranslation(['supermarket', 'common']);

    const [supermarkets, setSupermarkets] = useState([]);

    const [accountGroupOption, setAccountGroupOption] = useState({ value: 0, label: "" });
    const [accountGroupOptions, setAccountGroupOptions] = useState([]);

    const [selectedSupermarket, setSelectedSupermarket] = useState({ id: 0, name: "", categories: [] });
    const [editSupermarketId, setEditSupermarketId] = useState(0);

    const [showNew, setShowNew] = useState(false);
    const [refresh, setRefresh] = useState(false);
    const [isLoading, setIsLoading] = useState(true);

    const { showAlert } = useAlert();

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
            setIsLoading(false);
        } catch (error) {
            showAlert(VariantType.DANGER, error.message);
        }
    }

    async function fetchSupermarkets() {
        setIsLoading(true);
        try {
            const res = await getAllSupermarkets(accountGroupOption.value);
            // const res = [
            //     { id: 1, name: 'Aldi', categories: JSON.parse('["cookies", "fruit-and-vegetables", "refrigerated", "meat", "dairy", "beverages", "grocery", "frozen", "cleaning", "personal-hygiene", "other"]') },
            //     { id: 2, name: 'Rewe', categories: JSON.parse('["bakery", "cookies", "fruit-and-vegetables", "dairy", "beverages", "grocery", "frozen", "cleaning", "personal-hygiene", "other"]') },
            //     { id: 3, name: 'Edeka', categories: JSON.parse('["bakery", "cookies", "fruit-and-vegetables", "refrigerated", "meat", "dairy", "beverages", "grocery", "frozen", "cleaning", "personal-hygiene", "other"]') },
            //     { id: 4, name: 'Lidl', categories: JSON.parse('["fruit-and-vegetables", "bakery", "cookies", "dairy", "beverages", "grocery", "frozen", "cleaning", "personal-hygiene", "other"]') }
            // ]
            setSupermarkets(res);

            if (selectedSupermarket.id === 0 && res && res.length > 0) {
                setSelectedSupermarket(res[0]);
            }
            else if (selectedSupermarket.id > 0 && (!res || res.length === 0)) {
                setSelectedSupermarket({ id: 0, name: "", categories: [] })
            }

            setIsLoading(false);
        } catch (error) {
            showAlert(VariantType.DANGER, error.message);
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
            if (res && res.length > 0) {
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
            showAlert(VariantType.SUCCESS, t("delete-supermarket-success"));
        } catch (error) {
            showAlert(VariantType.DANGER, error.message);
        } finally {
            setRefresh(true);
            setIsLoading(false);
        }
    }

    function handleClickEdit(item) {
        setSelectedSupermarket(item);
        setEditSupermarketId(item.id);
    }

    function handleClickNew() {
        setSelectedSupermarket({ id: 0, name: "", categories: [] });
        setShowNew(true);
    }

    function handleCategoriesChange(list) {
        setSelectedSupermarket(prev => ({ ...prev, categories: list }));
        //save here or wait main save?
    }

    function renderSupermarkets() {

        if (isLoading)
            return (<span>Loading...</span>)

        return (
            <Table size='sm'>
                <tbody>
                    {showNew ? renderNewSupermarket() : <></>}
                    {supermarkets.map((item) => (renderSupermarket(item)))}
                </tbody>
            </Table>
        )
    }

    function renderSupermarket(item) {
        return (
            <tr key={item.id} className="align-middle">
                {editSupermarketId === item.id ?
                    <td colSpan={2}>
                        <Stack direction="horizontal" gap={1} className="d-flex justify-content-start m-0 p-0">
                            <div><FormCheck key={selectedSupermarket.id} type="radio" checked={true} disabled={true} /></div>
                            <div className='flex-grow-1'>
                                <Form.Control size="sm" type="text" defaultValue={selectedSupermarket.name}
                                    onChange={(e) => setSelectedSupermarket({ ...selectedSupermarket, name: e.target.value })} /></div>

                            <div><Button onClick={fetchEdit} variant="link" className='pe-0'><BsCheck2All className='icon' /></Button></div>
                            <div><Button onClick={() => setEditSupermarketId(0)} variant='link' title='Clear text'><BsXLg className='icon' /></Button></div>
                        </Stack >
                    </td >
                    :
                    <>
                        <td><FormCheck key={selectedSupermarket.id} type="radio" defaultValue={selectedSupermarket && selectedSupermarket.id === item.id}
                            defaultChecked={selectedSupermarket && selectedSupermarket.id === item.id}
                            onChange={() => setSelectedSupermarket(item)} style={{ color: "hsl(219, 11%, 25%)" }}
                            label={item.name}
                            className="flex-grow-1"
                            disabled={editSupermarketId > 0} />
                        </td>
                        <td>
                            <Stack direction="horizontal" gap={0} className="d-flex justify-content-end">
                                <div><Button onClick={() => handleClickEdit(item)} variant="link" disabled={editSupermarketId > 0}><BsPencil className='icon' /></Button></div>
                                <div><Button onClick={() => fetchDelete(item.id)} variant="link" disabled={setEditSupermarketId > 0}><BsTrash className='icon' /></Button></div>
                            </Stack>
                        </td>
                    </>
                }
            </tr >
        )
    }

    function renderNewSupermarket() {
        return (
            <tr key={0}>
                <td colSpan={2}>
                    <Stack direction="horizontal" gap={1} className="d-flex justify-content-start">
                        <div><FormCheck type="radio" checked={false} disabled={true} /></div>
                        <div className='w-50'><Form.Control size="sm" type="text" placeholder='Supermarket Name' defaultValue={selectedSupermarket.name} onChange={(e) => setSelectedSupermarket({ ...selectedSupermarket, name: e.target.value })} /></div>
                        <div><Button onClick={fetchCreate} variant="link" className='pe-0' disabled={selectedSupermarket.name.length === 0}><BsCheck2All className='icon' /></Button></div>
                        <div><Button onClick={() => setShowNew(false)} variant='link' title='Clear text'><BsXLg className='icon' /></Button></div>
                    </Stack>
                </td>
            </tr>
        )
    }


    return (
        <Stack gap={4}>
            <div></div>
            <div className='d-flex flex-row align-text-center gap-2'>
                <span className="title">{t('group-title')}</span>
                <Form.Group className="mb-2 flex-grow-1" controlId="formAccountGroups" size="sm">
                    {isLoading ? <span>Loading...</span> :
                        <Select name="accountGroup" key={accountGroupOption?.value}
                            defaultValue={accountGroupOption}
                            options={accountGroupOptions}
                            onChange={setAccountGroupOption} />
                    }
                </Form.Group>
            </div>
            <div className="d-flex align-items-center gap-2">
                <h6 className='title flex-grow-1'>{t("supermarket-title")}</h6>
                <Button bsPrefix="btn-custom" size="sm" onClick={handleClickNew} className="pe-2 ps-2" disabled={editSupermarketId > 0 || showNew}>{t("btn-create")}</Button>
            </div>
            <div className="scroll-supermarkets">
                {renderSupermarkets()}
            </div>
            <div>
                <CategoryDragDrop key={selectedSupermarket.id} innitialList={selectedSupermarket.categories} handleListChange={handleCategoriesChange} disabled={editSupermarketId === 0 && !showNew} />
            </div>
        </Stack>
    )

}