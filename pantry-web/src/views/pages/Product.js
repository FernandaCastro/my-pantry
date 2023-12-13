import React, { useState, useContext } from 'react';
import { updateProduct, createProduct, deleteProduct, getProductList } from '../../services/apis/mypantry/fetch/requests/PantryRequests.js';
import Stack from 'react-bootstrap/Stack';
import VariantType from '../components/VariantType.js';
import { AlertContext } from '../../services/context/AppContext.js';
import ProductForm from '../components/ProductForm.js';
import ProductList from '../components/ProductList.js';
import ProductSearchBar from '../components/ProductSearchBar.js'
import Button from 'react-bootstrap/Button';
import ListGroup from 'react-bootstrap/ListGroup';
import CloseButton from 'react-bootstrap/CloseButton';

export default function Product() {

    const [product, setProduct] = useState({});
    const [mode, setMode] = useState("");
    const [productLabel, setProductLabel] = useState("");
    const [productList, setProductList] = useState([]);
    const [isLoading, setIsLoading] = useState(true);
    const [showList, setShowList] = useState(false);
    const { alert, setAlert } = useContext(AlertContext);

    async function fetchSaveProduct(body) {
        try {
            const res = mode === 'new' ? await createProduct(body) : await updateProduct(product.id, body);
            setProduct(res);
        } catch (error) {
            showAlert(VariantType.DANGER, error.message);
        }
    }

    async function fetchDeleteProduct(productId) {
        try {
            await deleteProduct(productId);
        } catch (error) {
            showAlert(VariantType.DANGER, error.message);
        }
    }

    async function fetchProductList() {
        try {
            setIsLoading(true);
            const res = await getProductList();
            setProductList(res);
            setIsLoading(false);
        } catch (error) {
            showAlert(VariantType.DANGER, error.message);
        }
    }

    function handleSelectAction(selectedProduct) {
        setMode("edit");
        setProduct(selectedProduct);
        setProductLabel(selectedProduct.code);
    }
    function handleClearAction() {
        setMode("");
        setProduct({});
        setProductLabel("");
    }

    function handleNew() {
        setMode("new");
        setProduct({});
        setProductLabel("");
    }

    function handleSave(jsonProduct) {
        fetchSaveProduct(jsonProduct);
        showAlert(VariantType.SUCCESS, "Product saved successfully ");
        handleClearAction();
        if (showList) fetchProductList();
    }

    function handleRemove() {
        fetchDeleteProduct(product.id);
        showAlert(VariantType.SUCCESS, "Product removed successfully ");
        handleClearAction();
        if (showList) fetchProductList();
    }

    function handleListAll() {
        fetchProductList();
        setShowList(true);
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

    function renderProductForm() {
        if (mode === "") return;
        return (
            <div>
                <div className="me-3 d-flex justify-content-end align-items-center">
                    <CloseButton aria-label="Hide" onClick={handleClearAction} />
                </div>
                <ProductForm key={productLabel} product={product} handleSave={handleSave} />
            </div>
        );
    }

    function renderProductList() {
        if (!showList) return;
        return (
            <div>
                <div className="me-3 mb-2 d-flex justify-content-end align-items-center">
                    <CloseButton aria-label="Hide" onClick={() => setShowList(false)} />
                </div>
                {isLoading ? <h6>Loading...</h6> : <ProductList productList={productList} handleOnSelection={handleOnListSelection} />}
            </div>
        );
    }

    return (
        <Stack gap={3}>
            <div></div>
            <div>
                <ListGroup >
                    <ListGroup.Item variant="primary" className="d-flex justify-content-between align-items-center">
                        <span>{productLabel}</span>
                        <div className='justify-content-end'>
                            <Button variant="primary" size="sm" onClick={handleNew} className='me-2' disabled={(mode === "edit") || (product && Object.keys(product) > 0)}>New</Button>
                            <Button variant="primary" size="sm" onClick={handleRemove} className='me-2' disabled={!(mode === "edit") || !product || Object.keys(product) === 0}>Remove</Button>
                            <Button variant="primary" size="sm" onClick={handleListAll} >List All</Button>
                        </div>
                    </ListGroup.Item>
                </ListGroup>
            </div>
            <div><ProductSearchBar handleSelectAction={handleSelectAction} handleClearAction={handleClearAction} /></div>
            <div>
                {renderProductForm()}
            </div>
            <div>
                {renderProductList()}
            </div>
            <div></div>
        </Stack>
    );

}
