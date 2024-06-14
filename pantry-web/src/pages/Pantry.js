import React, { useEffect, useState } from 'react';
import { useParams } from 'react-router';
import { getPantry, updatePantry, createPantry, createPantryItem, getPantryRebalance } from '../services/apis/mypantry/requests/PantryRequests.js';
import Stack from 'react-bootstrap/Stack';
import VariantType from '../components/VariantType.js';
import useAlert from '../hooks/useAlert.js';
import PantryForm from '../components/PantryForm.js';
import PantryItemList from '../components/PantryItemList.js';
import Button from 'react-bootstrap/Button';
import { getAccountGroupList } from '../services/apis/mypantry/requests/AccountRequests.js';
import { useTranslation } from 'react-i18next';
import { Image } from 'react-bootstrap';
import iconPantry from '../assets/images/cupboard-gradient.png';
import { BsCardChecklist } from 'react-icons/bs';

export default function Pantry({ mode }) {

    const { t } = useTranslation(['pantry', 'common']);

    let { id } = useParams();
    const [accountGroupOptions, setAccountGroupOptions] = useState([]);
    const [showPantry, setShowPantry] = useState(true);
    const [isEmpty, setIsEmpty] = useState(true);

    const [pantry, setPantry] = useState(
        {
            id: 0,
            name: "",
            type: "R",
            isActive: true,
            accountGroup: { id: 0 }
        });

    const [isLoading, setIsLoading] = useState(false);
    const [refresh, setRefresh] = useState(false);
    const { showAlert } = useAlert();

    useEffect(() => {
        if (id && mode === 'edit') {
            fetchPantry();
        }

        if (!accountGroupOptions || accountGroupOptions.length === 0) {
            fetchAccountGroups();
        }
    }, [])


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
            setIsLoading(false);
        } catch (error) {
            showAlert(VariantType.DANGER, error.message);
        }
    }

    async function fetchPantry() {
        setIsLoading(true);
        try {
            const res = await getPantry(id);
            setPantry(res);

        } catch (error) {
            showAlert(VariantType.DANGER, error.message);
        } finally {
            setIsLoading(false);
        }
    }

    async function fetchPantryRebalance() {
        setIsLoading(true);
        setRefresh(true);
        try {
            await getPantryRebalance(id);

            showAlert(VariantType.SUCCESS, t('balance-success'));
        } catch (error) {
            showAlert(VariantType.DANGER, error.message);
        } finally {
            setRefresh(false);
            setIsLoading(false);
        }
    }

    async function fetchSavePantry(body) {
        setIsLoading(true);
        try {
            const res = mode === 'new' ? await createPantry(body) : await updatePantry(id, body);
            setPantry(res);

            const msg = mode === 'edit' ? t('update-pantry-success') : t('create-pantry-success');
            showAlert(VariantType.SUCCESS, msg);

        } catch (error) {
            showAlert(VariantType.DANGER, error.message);
        } finally {
            setIsLoading(false);
        }
    }

    async function fetchSavePantryItem(body) {
        setIsLoading(true);
        setRefresh(true);
        try {
            await createPantryItem(pantry.id, body);
            showAlert(VariantType.SUCCESS, t('add-item-success'));
        } catch (error) {
            showAlert(VariantType.DANGER, error.message);
        } finally {
            setRefresh(false);
            setIsLoading(false);
        }
    }

    function handleAddItem(product) {
        const body = {
            pantry: pantry,
            product: product
        }
        fetchSavePantryItem(body);
    }

    function handleRebalance() {
        fetchPantryRebalance();
    }

    function renderPantryList() {
        return (
            <Stack gap={2}>
                <div className="d-flex justify-content-end"><Button bsPrefix='btn-custom' size="sm" onClick={handleRebalance} title={t('tooltip-btn-balance-inventory')} disabled={isEmpty}><span className="gradient-text">{t('btn-balance-inventtory')}</span></Button></div>
                <div><PantryItemList key={refresh} pantryId={pantry.id} setIsEmpty={setIsEmpty} /></div>
            </Stack>
        )
    }

    return (
        <div className="mt-4">
            <div className='d-flex justify-content-start align-items-end mt-4 mb-4'>
                <Image src={iconPantry} width={40} height={40} className="ms-2 me-3" />
                <h6 className="title">{t('pantry-title')}</h6>
                <Button bsPrefix="btn-custom" href={"/pantries/" + pantry.id + "/items"} className="pe-2 ps-2 ms-auto"><span>{t('btn-add-pantry-items')}</span></Button>
            </div>
            <div>
                {mode === "edit" && isLoading ?
                    <h6>Loading...</h6> :
                    <PantryForm key={pantry.id} pantry={pantry} handleSave={fetchSavePantry} accountGroupOptions={accountGroupOptions} />}
            </div>
        </div>
    );
}