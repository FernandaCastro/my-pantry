import React, { useEffect, useState } from 'react';
import { useParams } from 'react-router';
import { getPantry, createPantryItem, getPantryRebalance } from '../services/apis/mypantry/requests/PantryRequests.js';
import VariantType from '../components/VariantType.js';
import useAlert from '../hooks/useAlert.js';
import ProductSearchBar from '../components/ProductSearchBar.js'
import PantryItemList from '../components/PantryItemList.js';
import Button from 'react-bootstrap/Button';
import { getAccountGroupList } from '../services/apis/mypantry/requests/AccountRequests.js';
import { useTranslation } from 'react-i18next';
import { Image, Stack } from 'react-bootstrap';
import iconPantry from '../assets/images/cupboard-gradient.png';

export default function PantryItems() {

    const { t } = useTranslation(['pantry', 'common']);

    let { id } = useParams();
    const [pantry, setPantry] = useState();
    const [accountGroupOptions, setAccountGroupOptions] = useState([]);
    const [isEmpty, setIsEmpty] = useState(true);

    const [isLoading, setIsLoading] = useState(false);
    const [refresh, setRefresh] = useState(false);
    const { showAlert } = useAlert();

    useEffect(() => {
        if (id) {
            fetchPantry();
        }

        if (!accountGroupOptions || accountGroupOptions.length === 0) {
            fetchAccountGroups();
        }
    }, [])

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

    async function fetchPantryRebalance() {
        setIsLoading(true);
        setRefresh(true);
        try {
            await getPantryRebalance(pantry.id);

            showAlert(VariantType.SUCCESS, t('balance-success'));
        } catch (error) {
            showAlert(VariantType.DANGER, error.message);
        } finally {
            setRefresh(false);
            setIsLoading(false);
        }
    }

    async function fetchSavePantryItem(body) {
        setIsLoading(true);
        setRefresh(true);
        try {
            await createPantryItem(id, body);
            showAlert(VariantType.SUCCESS, t('add-item-success'));
        } catch (error) {
            showAlert(VariantType.DANGER, error.message);
        } finally {
            setRefresh(false);
            setIsLoading(false);
        }
    }

    function handleAddItem(product, itemQuantity) {
        const body = {
            pantry: pantry,
            product: product,
            idealQty: itemQuantity.idealQty,
            currentQty: itemQuantity.currentQty
        }
        fetchSavePantryItem(body);
    }

    function handleRebalance() {
        fetchPantryRebalance();
    }

    return (
        <Stack gap={3}>
        <div className="mt-4">
            <div className="d-flex justify-content-start align-items-end">
                <Image src={iconPantry} width={40} height={40} className="ms-2 me-3" />
                <h6 className='title'>{t('pantry-items-title', {pantry: pantry?.name})}</h6>
            </div>

            <div className="mt-4" style={{ display: pantry && pantry?.id > 0 ? 'block' : 'none' }}>
                <ProductSearchBar accountGroupId={pantry?.accountGroup?.id} accountGroupOptions={accountGroupOptions} handleSelectAction={handleAddItem} addButtonVisible={true} />
            </div>

            <div className="d-flex flex-column mt-4" style={{ display: pantry && pantry?.id > 0 ? 'block' : 'none' }}>
                <Button className="align-self-end mb-3" bsPrefix='btn-custom' size="sm" onClick={handleRebalance} title={t('tooltip-btn-balance-inventory')} disabled={isEmpty}>
                    <span className="gradient-text">{t('btn-balance-inventtory')}</span>
                </Button>
                <PantryItemList key={refresh} pantryId={pantry?.id} setIsEmpty={setIsEmpty} />
            </div>
        </div>
        </Stack>
    );
}