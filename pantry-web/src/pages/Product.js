import React, { useState, useEffect } from 'react';
import { updateProduct, createProduct } from '../services/apis/mypantry/requests/PantryRequests.js';
import { getProperty } from '../services/apis/mypantry/requests/PurchaseRequests.js';
import Stack from 'react-bootstrap/Stack';
import VariantType from '../components/VariantType.js';
import useAlert from '../hooks/useAlert.js';
import ProductForm from '../components/ProductForm.js';
import ProductList from '../components/ProductList.js';
import Button from 'react-bootstrap/Button';
import CloseButton from 'react-bootstrap/CloseButton';
import { getAccountGroupList } from '../services/apis/mypantry/requests/AccountRequests.js';
import { useTranslation } from 'react-i18next';
import iconProduct from '../assets/images/food-gradient.png';
import Image from 'react-bootstrap/Image';

export default function Product() {

    const { t } = useTranslation(['product', 'categories']);

    const [product, setProduct] = useState({});
    const [mode, setMode] = useState("");
    const [productLabel, setProductLabel] = useState("");
    const [refresh, setRefresh] = useState(0);
    const [showForm, setShowForm] = useState(false);
    const [categories, setCategories] = useState([]);

    const [accountGroupOptions, setAccountGroupOptions] = useState([]);
    const [isLoading, setIsLoading] = useState(false);
    const { showAlert } = useAlert();

    useEffect(() => {
        fetchCategories();
        if (!accountGroupOptions || accountGroupOptions.length === 0) {
            fetchAccountGroups();
        }
    }, []);

    async function fetchCategories() {
        try {
            const res = await getProperty("product.categories");

            const resCategories = JSON.parse(res.propertyValue);
            var list = [];
            resCategories.forEach(category => {
                list = [...list,
                {
                    value: category,
                    label: t(category, { ns: 'categories' })
                }]
            });

            setCategories(list);
        } catch (error) {
            showAlert(VariantType.DANGER, t('fetch-categories-error') + error.message);
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

    async function fetchSaveProduct(body) {
        try {
            setRefresh(0);
            const res = mode === 'new' ? await createProduct(body) : await updateProduct(product.id, body);
            setProduct(res);
            setRefresh(res.id);
            showAlert(VariantType.SUCCESS, t('save-product-success'));
            handleClearAction();
        } catch (error) {
            showAlert(VariantType.DANGER, error.message);
        }
    }

    function handleSelectAction(selectedProduct) {
        setMode("edit");
        setProduct(selectedProduct);
        setProductLabel(selectedProduct.code);
        return setShowForm(true);
    }

    function handleClearAction() {
        setMode(m => { "" });
        setProduct({});
        setProductLabel("");
        return setShowForm(false);
    }


    function handleNew() {
        setMode("new");
        setProduct({});
        setProductLabel("");
        return setShowForm(true);
    }

    async function handleSave(jsonProduct) {
        await fetchSaveProduct(jsonProduct);
    }

    function handleRemove(productId) {
        if (showForm && productId === product.id) {
            handleClearAction();
        }
    }

    function handleOnListSelection(item) {
        handleSelectAction(item);
    }

    return (
        <Stack gap={3}>
            <div hidden={!showForm} className="mt-4">
                <div>
                    <div className="me-3 mb-3 d-flex justify-content-start align-items-end">
                        <Image src={iconProduct} width={40} height={40} className='ms-3 me-3 ' />
                        <h6 className='title'>{t('product-title', { product: product.code })}</h6>
                        <CloseButton className="ms-auto" aria-label="Hide" onClick={handleClearAction} />
                    </div>
                    <ProductForm key={productLabel} product={product} categories={categories} accountGroupOptions={accountGroupOptions} handleSave={handleSave} />
                </div>
            </div>
            <div hidden={showForm} className="mt-4">
                <Stack direction="horizontal" gap={2} className='mb-3 d-flex justify-content-start align-items-end'>
                    <Image src={iconProduct} width={40} height={40} className='ms-3 me-2' />
                    <h6 className='title'>{t('product-list-title')}</h6>
                    <Button bsPrefix="btn-custom" size="sm" onClick={handleNew} className='me-2 ms-auto' disabled={(mode === "edit") || (product && Object.keys(product) > 0)}><span>{t('btn-new-product')}</span></Button>
                </Stack>

                <ProductList key={refresh} disabled={showForm} onEdit={handleOnListSelection} onRemove={handleRemove} />
            </div>
        </Stack>
    );

}
