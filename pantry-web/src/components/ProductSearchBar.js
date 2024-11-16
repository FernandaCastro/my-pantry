import Form from 'react-bootstrap/Form';
import Button from 'react-bootstrap/Button';
import { useState } from 'react';
import { BsEraser, BsCheck2All, BsChevronDown, BsPlusLg } from "react-icons/bs";
import VariantType from './VariantType.js';
import useAlert from '../state/useAlert.js';
import { createProduct, fetchFilteredProductList } from '../api/mypantry/pantry/pantryService.js';
import Card from 'react-bootstrap/Card';
import Stack from 'react-bootstrap/Stack';
import CloseButton from 'react-bootstrap/CloseButton';
import ProductForm from './ProductForm.js';
import { camelCase } from '../util/utils.js';
import Collapse from 'react-bootstrap/Collapse';
import { useTranslation } from 'react-i18next';
import NumericField from './NumericField.js';

function ProductSearchBar({ accountGroupId, accountGroupOptions, handleSelectAction, handleClearAction, addButtonVisible }) {

    const { t } = useTranslation(['product', 'common']);

    const notFound = "No product found";
    const [searchText, setSearchText] = useState("");
    const [results, setResults] = useState([]);
    const [notFoundMessage, setNotFoundMessage] = useState("");
    const [showProductForm, setShowProductForm] = useState(false);
    const [show, setShow] = useState(false);
    const { showAlert } = useAlert();
    const [product, setProduct] = useState({});
    const [itemQuantity, setItemQuantity] = useState({ idealQty: 0, currentQty: 0 });

    function handleSearch(e) {
        setSearchText(e.target.value);
        if (e.target.value.length < 3 && searchText.length < 4) setResults([]);
        if (e.target.value.length > 2) loadProduct(e.target.value);
    }

    function clearSearch() {
        setSearchText("");
        setResults([]);
        setNotFoundMessage("");
    }
    function handleClear() {
        clearSearch();
        if (handleClearAction) handleClearAction();
    }
    function handleSelect(item) {
        handleSelectAction(item, itemQuantity);
        clearSearch();
    }

    async function loadProduct(value) {
        try {
            const res = await fetchFilteredProductList(accountGroupId, value);
            setNotFoundMessage(res.length === 0 ? notFound : "");
            setResults(res);
        } catch (error) {
            showAlert(VariantType.DANGER, error.message);
        }
    }

    function renderSearchBar() {
        return (
            <Stack direction="horizontal" gap={2} className="w-100">
                <div className="w-75 pe-0">
                    <Form.Control type="text" placeholder={t('placeholder-search-product')}
                        value={searchText}
                        onChange={(e) => handleSearch(e)} />
                </div>
                <div>
                    <Button id='btn-seacrh-clear' className="w-0 p-0" type="reset" variant="link" onClick={handleClear} title={t('tooltip-btn-search-clear')}><BsEraser className='icon' /></Button>
                </div>
                <div>
                    {addButtonVisible === true ?
                        <Button id='btn-new' className="w-0 p-0" variant="link" onClick={handleNewProduct} title={t('tooltip-btn-new')} disabled={results && results.length > 0}><BsPlusLg className='icon' /></Button> : <span />}
                </div>
            </Stack>
        );
    }

    function renderResults() {
        return (
            results.map((item) => {
                return (
                    <tr key={item.id} className="w-0 p-0 colorfy">
                        <td className="w-0 p-0 border-end-0 colorfy">
                            <span>{camelCase(item.code)} {item.description && item.description !== "" ? ' - ' + item.description : ""}</span></td>
                        <td className="w-0 p-0 border-start-0 colorfy">
                            <Button onClick={() => handleSelect(item)} variant="link" title={t('tooltip-add-to-pantry')}><BsCheck2All className='icon' /></Button>
                        </td>
                    </tr>

                );
            })
        );
    }

    async function fetchSaveProduct(body) {
        try {
            const res = await createProduct(body);
            setProduct(res);
            return res;
        } catch (error) {
            showAlert(VariantType.DANGER, error.message);
        }
    }

    function handleNewProduct() {
        setProduct({ id: 0, code: searchText });
        setShowProductForm(true);
    }

    async function handleSaveAndAddNewProduct(jsonProduct) {
        const res = await fetchSaveProduct(jsonProduct);
        if (res) {
            handleSelect(res, itemQuantity);
            showAlert(VariantType.SUCCESS, t('create-add-product-to-pantry-success'));
        }
        setShowProductForm(false);
    }

    function renderProductForm() {
        if (showProductForm) {
            return (
                <div>
                    <div className="me-3 d-flex justify-content-end align-items-center">
                        <CloseButton className="btn-close" aria-label="Hide" onClick={() => setShowProductForm(false)} />
                    </div>
                    <ProductForm product={product} accountGroupId={accountGroupId} accountGroupOptions={accountGroupOptions} handleSave={handleSaveAndAddNewProduct} />
                </div>
            );
        }
    }

    function handleItem(newItem) {
        setItemQuantity(newItem);
    }

    function renderFooter() {
        if ((results && results.length > 0) || showProductForm) {
            return (
                <div className="d-flex justify-content-start gap-5">
                    <div className="d-flex flex-column align-items-center">
                        <h6 className='simple-title'>{t('ideal', { ns: 'common' })}</h6>
                        <NumericField object={itemQuantity} attribute="idealQty" onValueChange={handleItem} />
                    </div>
                    <div className="d-flex flex-column align-items-center ">
                        <h6 className='simple-title'>{t('current', { ns: 'common' })}</h6>
                        <NumericField object={itemQuantity} attribute="currentQty" onValueChange={handleItem} />
                    </div>
                </div>
            )
        }
    }

    return (
        <>
            <div onClick={() => setShow(!show)} className="d-flex justify-content-start gap-3 align-items-center mt-4 ">
                <h6 className='simple-title'>{t('search-product-title')}</h6>
                <BsChevronDown className='small-icon' />
            </div>
            <div className='custom-card'>
                <Collapse in={show} >
                    <Card>
                        <Card.Header className="m-0 p-2 d-flex justify-content-between">
                            {renderSearchBar()}
                        </Card.Header>
                        <Card.Body className="m-0 p-2">
                            <span style={{ color: 'red', fontSize: '11px' }}>{notFoundMessage}</span>
                            {renderProductForm()}
                            {results && results.length > 0 ? (
                                <table>
                                    <tbody>
                                        {renderResults()}
                                    </tbody>
                                </table>)
                                : <span />
                            }
                        </Card.Body>
                        <Card.Footer >
                            {renderFooter()}
                        </Card.Footer>
                    </Card>

                </Collapse>
            </div>
        </>

    );
}
export default ProductSearchBar;