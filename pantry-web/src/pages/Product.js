import React, { useState, useContext, useEffect } from 'react';
import { updateProduct, createProduct } from '../services/apis/mypantry/requests/PantryRequests.js';
import { getProperty } from '../services/apis/mypantry/requests/PurchaseRequests.js';
import Stack from 'react-bootstrap/Stack';
import VariantType from '../components/VariantType.js';
import { AlertContext } from '../services/context/AppContext.js';
import ProductForm from '../components/ProductForm.js';
import ProductList from '../components/ProductList.js';
import Button from 'react-bootstrap/Button';
import CloseButton from 'react-bootstrap/CloseButton';
import { getAccountGroupList } from '../services/apis/mypantry/requests/AccountRequests.js';

export default function Product() {

    const [product, setProduct] = useState({});
    const [mode, setMode] = useState("");
    const [productLabel, setProductLabel] = useState("");
    const [refresh, setRefresh] = useState(0);
    const { setAlert } = useContext(AlertContext);
    const [showForm, setShowForm] = useState(false);
    const [categories, setCategories] = useState([]);

    const [accountGroupOptions, setAccountGroupOptions] = useState([]);
    const [isLoading, setIsLoading] = useState(false);

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
                    label: category
                }]
            });

            setCategories(list);
        } catch (error) {
            showAlert(VariantType.DANGER, "Unable to load categories: " + error.message);
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
            showAlert(VariantType.SUCCESS, "Product saved successfully ");
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
        handleClearAction();
    }

    function handleRemove(productId) {
        if (showForm && productId === product.id) {
            handleClearAction();
        }
    }

    function handleOnListSelection(item) {
        handleSelectAction(item);
    }

    function showAlert(type, message) {
        setAlert({
            show: true,
            type: type,
            message: message
        })
    }

    return (
        <>
            <div hidden={!showForm} className="mt-4">
                <div className='border-custom'>
                    <div className="me-3 d-flex justify-content-end align-items-center">
                        <CloseButton aria-label="Hide" onClick={handleClearAction} />
                    </div>
                    <ProductForm key={productLabel} product={product} categories={categories} accountGroupOptions={accountGroupOptions} handleSave={handleSave} />
                </div>
            </div>
            <div hidden={showForm} className="mt-4">
                <Stack direction="horizontal" gap={2} className='mb-3 d-flex justify-content-between'>
                    <h6 className="text-start fs-6 lh-lg title">{productLabel} </h6>
                    <Button bsPrefix="btn-custom" size="sm" onClick={handleNew} className='me-2' disabled={(mode === "edit") || (product && Object.keys(product) > 0)}>New Product</Button>
                </Stack>

                <ProductList key={refresh} disabled={showForm} onEdit={handleOnListSelection} onRemove={handleRemove} />
            </div>
        </>
    );

}
