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

    function renderItems() {
        return filteredItems.map((item) => renderItem(item))
    }

    function renderItem(item) {
        return (
            <tr key={item.product.id} className="border border-primary-subtle align-middle">
                <td>
                    <Stack direction="horizontal" gap={2}>
                        <div><Image src={food} width={20} height={20} rounded /></div>
                        <div><span className='text-wrap'>{camelCase(item.product.code)}</span></div>
                    </Stack>
                    <p className='d-none d-md-block ms-4 mb-0' hidden={item.product.description === ''}>
                        {item.product.description}  {item.product.size}
                    </p>
                </td>
                <td><NumericField key={item.idealQty} object={item} attribute="idealQty" onValueChange={handleSave} /></td>
                <td><NumericField key={item.currentQty} object={item} attribute="currentQty" onValueChange={handleSave} /></td>
                <td className='ms-0 pe-0 text-center'><span>{item.provisionedQty}</span></td>
                <td className='ms-0 ps-0 me-1 pe-2'>
                    <Button onClick={() => handleRemove(item)} variant="link" className='pt-0 pb-0 pe-0'><BsTrash className='icon' /></Button>
                </td>
            </tr >
        )
    }

    function filter(text) {
        if (text && text.length > 0)
            setFilteredItems(pantryItems.filter(item => item.product.code.toUpperCase().includes(text.toUpperCase())));
        else
            setFilteredItems(pantryItems);

        setSearchText(text);
    }

    return (
        <div>
            <Form.Control size="sm" type="text" id="search" className="form-control mb-1" value={searchText} placeholder={t('placeholder-search-item', {ns: 'common'})} onChange={(e) => filter(e.target.value)} />
            <div className='scroll-pantryItems'>
                <Table>
                    <thead>
                        <tr key="0:0" className="align-middle">
                            <th><span className='title'>{t('code-description', { ns: 'common' })}</span></th>
                            <th><span className='title'>{t('ideal', { ns: 'common' })}</span></th>
                            <th ><span className='title'>{t('current', { ns: 'common' })}</span></th>
                            <th className='ms-0 ps-0'><span className="title">{t('provisioned', { ns: 'common' })}</span></th>
                            <th className='ms-0 ps-0 me-2 pe-2'></th>
                        </tr>
                    </thead>
                    <tbody >
                        {renderItems()}
                    </tbody>
                </Table>
            </div>
        </div>
    );
}

export default PantryItemList;