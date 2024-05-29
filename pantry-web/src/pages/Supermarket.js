import { useEffect, useState } from 'react'
import { FormCheck, Stack, Button, Table, Form } from "react-bootstrap";
import { BsPencil, BsTrash, BsCheck2All, BsXLg } from "react-icons/bs";
import i18n from 'i18next';
import { useTranslation } from 'react-i18next';
import { CategoryDragDrop } from '../components/CategoryDragDrop';
import useAlert from '../hooks/useAlert.js';
import VariantType from '../components/VariantType.js';

export function Supermarket() {

    const { t } = useTranslation(['supermarket', 'common']);

    const [supermarkets, setSupermarkets] = useState([]);
    const [supermarketCategories, setSupermarketCategories] = useState([]);

    const [name, setName] = useState("");
    const [selectedSupermarket, setSelectedSupermarket] = useState({ id: 0, categories: [] });
    const [edit, setEdit] = useState(0);

    const [showNew, setShowNew] = useState(false);
    const [refresh, setRefresh] = useState(true);
    const [isLoading, setIsLoading] = useState(true);

    const { showAlert } = useAlert();

    useEffect(() => {
        if (refresh) fetchSupermarkets();
    }, [refresh])

    async function fetchSupermarkets() {
        setRefresh(true);
        setIsLoading(true);
        try {
            //const res = await getSupermarkets();
            const res = [
                { id: 1, name: 'Aldi', categories: JSON.parse('["cookies", "fruit-and-vegetables", "refrigerated", "meat", "dairy", "beverages", "grocery", "frozen", "cleaning", "personal-hygiene", "other"]') },
                { id: 2, name: 'Rewe', categories: JSON.parse('["bakery", "cookies", "fruit-and-vegetables", "dairy", "beverages", "grocery", "frozen", "cleaning", "personal-hygiene", "other"]') }
            ]
            setSupermarkets(res);

            setRefresh(false);
            setIsLoading(false);
        } catch (error) {
            showAlert(VariantType.DANGER, error.message);
        }
    }

    async function fetchCreate(group) {
        try {
            setIsLoading(true);
            //await createSupermarket(group);
            showAlert(VariantType.SUCCESS, t("create-group-success"));
        } catch (error) {
            showAlert(VariantType.DANGER, error.message);
        } finally {
            setRefresh(true);
            setIsLoading(false);
        }
    }

    async function fetchEdit(group) {
        try {
            setIsLoading(true);
            //await editAccountGroup(group);
            showAlert(VariantType.SUCCESS, t("update-group-success"));
        } catch (error) {
            showAlert(VariantType.DANGER, error.message);
        } finally {
            setRefresh(true);
            setIsLoading(false);
        }
    }

    async function fetchDelete(groupId) {
        try {
            setIsLoading(true);
            //await deleteAccountGroup(groupId);
            showAlert(VariantType.SUCCESS, t("delete-group-success"));
        } catch (error) {
            showAlert(VariantType.DANGER, error.message);
        } finally {
            setRefresh(true);
            setIsLoading(false);
        }
    }

    function handleNew() {
        const group = { id: 0, name: name };
        fetchCreate(group);
        setShowNew(false);
    }

    function handleEdit(item) {
        const edited = {
            ...item,
            name: name
        }
        fetchEdit(edited);
        setEdit(0);
    }

    async function handleRemove(id) {
        //Show modal to confirm deletion when there's any object associated to the group
        //const pantries = await fetchAssociatedPantries(id);
        //(pantries && pantries.length > 0) ? setShowModal(true) : fetchDeleteGroup(id);
    }

    function renderSupermarkets() {

        if (isLoading)
            return (<span>Loading...</span>)

        return (
            <Table size='sm' className='bordered'>
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
                {edit === item.id ?
                    <td colSpan={2}>
                        <Stack direction="horizontal" gap={1} className="d-flex justify-content-start">
                            <div><FormCheck key={selectedSupermarket.id} type="radio" checked={true} disabled={true} /></div>
                            <div className='w-50'><Form.Control size="sm" type="text" defaultValue={item.name} onChange={(e) => setName(e.target.value)} /></div>
                            <div><Button onClick={() => handleEdit(item, name)} variant="link" className='pe-0'><BsCheck2All className='icon' /></Button></div>
                            <div><Button onClick={() => setEdit(0)} variant='link' title='Clear text'><BsXLg className='icon' /></Button></div>
                        </Stack >
                    </td >
                    :
                    <>
                        <td><FormCheck key={selectedSupermarket.id} type="radio" defaultValue={selectedSupermarket && selectedSupermarket.id === item.id}
                            defaultChecked={selectedSupermarket && selectedSupermarket.id === item.id}
                            onChange={() => setSelectedSupermarket(item)} style={{ color: "hsl(219, 11%, 25%)" }}
                            label={item.name} />
                        </td>
                        {/* <td><span>{!item.parentAccountGroup ? t('parent') : t('child')}</span></td> */}
                        <td>
                            <Stack direction="horizontal" gap={1} className="d-flex justify-content-end">
                                <div><Button onClick={() => setEdit(item.id)} variant="link"><BsPencil className='icon' /></Button></div>
                                <div><Button onClick={() => handleRemove(item.id)} variant="link" disabled={supermarkets.length === 1}><BsTrash className='icon' /></Button></div>
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
                        <div className='w-50'><Form.Control size="sm" type="text" placeholder='Supermarket Name' defaultValue={name} onChange={(e) => setName(e.target.value)} /></div>
                        <div><Button onClick={handleNew} variant="link" className='pe-0' disabled={name.length === 0}><BsCheck2All className='icon' /></Button></div>
                        <div><Button onClick={() => setShowNew(false)} variant='link' title='Clear text'><BsXLg className='icon' /></Button></div>
                    </Stack>
                </td>
            </tr>
        )
    }


    return (
        <Stack gap={4}>
            <div></div>
            <div className="d-flex align-items-center gap-2">
                <h6 className='title flex-grow-1'>{t("supermarket-title")}</h6>
                <Button bsPrefix="btn-custom" size="sm" onClick={() => setShowNew(true)} className="pe-2 ps-2">{t("btn-create-group")}</Button>
            </div>
            <div>
                {renderSupermarkets()}
            </div>
            <div>
                <CategoryDragDrop key={selectedSupermarket.id} innitialList={selectedSupermarket.categories} />
            </div>
        </Stack>
    )

}