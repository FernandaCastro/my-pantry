import React, { useState } from 'react';
import { useParams } from 'react-router';
import VariantType from '../components/VariantType.js';
import useAlert from '../state/useAlert.js';
import ProductSearchBar from '../components/ProductSearchBar.js'
import PantryItemList from '../components/PantryItemList.js';
import Button from 'react-bootstrap/Button';
import { useTranslation } from 'react-i18next';
import { Image, Stack } from 'react-bootstrap';
import iconPantry from '../assets/images/cupboard-gradient.png';
import { Loading } from '../components/Loading.js';
import useProfile from '../state/useProfile.js';
import { useGetAccountGroups, useGetAccountGroupsOptions } from '../hooks/fetchCacheApiAccount.js';
import { useCreateNewPantryItem, useGetPantry, useAnalysePantry } from '../hooks/fetchCacheApiPantry.js';


export default function PantryItems() {

    const { t } = useTranslation(['pantry', 'common']);
    const { profile } = useProfile();
    const [isEmpty, setIsEmpty] = useState(true);
    const [refetch, setRefetch] = useState(true);
    const { showAlert } = useAlert();
    const [isLoading, setIsLoading] = useState(false);

    let { id } = useParams();

    const { data: pantry } = useGetPantry(
        { id },
        { onError: (error) => showAlert(VariantType.DANGER, error) }
    );

    const { data: accountGroups } = useGetAccountGroups(
        { email: profile?.email },
        { onError: (error) => showAlert(VariantType.DANGER, error) }
    );

    const { data: accountGroupOptions } = useGetAccountGroupsOptions({ email: profile?.email, accountGroups: accountGroups })

    const [analysePantryClick, setAnalysePantryClick] = useState(false);

    const handleAnalysePantrySuccess = (data) => {
        setIsLoading(false);
        setAnalysePantryClick(false);
        showAlert(VariantType.SUCCESS, t('balance-success'));
        setRefetch(true);
    }

    const handleAnalysePantryError = (error) => {
        setIsLoading(false);
        setAnalysePantryClick(false);
        showAlert(VariantType.DANGER, error.message);
    }

    useAnalysePantry({ id: pantry?.id, runQuery: analysePantryClick },
        {
            onSuccess: (data) => handleAnalysePantrySuccess(data),
            onError: (error) => handleAnalysePantryError(error)
        });

    const handleCreatePantryItemSuccess = (data) => {
        setIsLoading(false);
        showAlert(VariantType.SUCCESS, t('add-item-success'));
        setRefetch(true);
    }

    const handleCreatePantryItemError = (error) => {
        setIsLoading(false);
        showAlert(VariantType.DANGER, error);
    }

    const { mutate } = useCreateNewPantryItem({ onSuccess: handleCreatePantryItemSuccess, onError: handleCreatePantryItemError })

    function handleAddItem(product, itemQuantity) {
        const newItem = {
            pantry: pantry,
            product: product,
            idealQty: itemQuantity.idealQty,
            currentQty: itemQuantity.currentQty
        }

        setIsLoading(true);
        mutate({ pantryId: pantry?.id, newItem: newItem });
    }

    function handleRebalance() {
        if (isLoading) return;

        setIsLoading(true);
        setAnalysePantryClick(!analysePantryClick);
    }

    return (
        <Stack gap={3}>
            <div className="mt-4">
                <div className="d-flex justify-content-start align-items-end">
                    <Image src={iconPantry} width={40} height={40} className="ms-2 me-3" />
                    <h6 className='title'>{t('pantry-items-title', { pantry: pantry?.name })}</h6>
                </div>

                <div className="mt-4" style={{ display: pantry && pantry?.id > 0 ? 'block' : 'none' }}>
                    <ProductSearchBar accountGroupId={pantry?.accountGroup?.id} accountGroupOptions={accountGroupOptions} handleSelectAction={handleAddItem} addButtonVisible={true} />
                </div>

                <div className="d-flex flex-column mt-4" style={{ display: pantry && pantry?.id > 0 ? 'block' : 'none' }}>
                    <Button className="align-self-end mb-3" bsPrefix='btn-custom' size="sm" onClick={handleRebalance} title={t('tooltip-btn-balance-inventory')} disabled={isEmpty}>
                        <span className="gradient-text">{t('btn-balance-inventtory')}</span>
                    </Button>
                    {isLoading && <Loading />}
                    <PantryItemList refetch={refetch} setRefetch={setRefetch} pantryId={pantry?.id} setIsEmpty={setIsEmpty} />
                </div>
            </div>
        </Stack>
    );
}