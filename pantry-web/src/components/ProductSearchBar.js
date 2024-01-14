import Form from 'react-bootstrap/Form';
import Button from 'react-bootstrap/Button';
import { useState, useContext } from 'react';
import { BsEraser, BsCheck2All, BsChevronDown, BsPlusLg } from "react-icons/bs";
import VariantType from './VariantType.js';
import { getFilteredProductList, createProduct } from '../services/apis/mypantry/requests/PantryRequests.js';
import { AlertContext } from '../services/context/AppContext.js';
import Table from 'react-bootstrap/Table';
import Accordion from 'react-bootstrap/Accordion';
import { useAccordionButton } from 'react-bootstrap/AccordionButton';
import Card from 'react-bootstrap/Card';
import Stack from 'react-bootstrap/Stack';
import CloseButton from 'react-bootstrap/CloseButton';
import '../assets/styles/ProductSearchBar.css';
import ProductForm from './ProductForm.js';
import { camelCase } from '../services/Utils.js';

function ProductSearchBar({ handleSelectAction, handleClearAction, addButtonVisible }) {

    const notFound = "No product found";
    const [searchText, setSearchText] = useState("");
    const [results, setResults] = useState([]);
    const [notFoundMessage, setNotFoundMessage] = useState("");
    const [showProductForm, setShowProductForm] = useState(false);
    const { alert, setAlert } = useContext(AlertContext);

    const [product, setProduct] = useState({});

    function handleSearch(e) {
        setSearchText(e.target.value);
        if (e.target.value.length < 3 && searchText.length < 4) setResults([]);
        if (e.target.value.length > 2) fetchProduct(e.target.value);
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
        handleSelectAction(item);
        clearSearch();
    }

    async function fetchProduct(value) {
        try {
            const res = await getFilteredProductList(value);
            setNotFoundMessage(res.length === 0 ? notFound : "");
            setResults(res);
        } catch (error) {
            setAlert({
                show: true,
                type: VariantType.DANGER,
                message: error.message
            });
        }
    }

    function renderSearchBar() {
        return (
            <Stack direction="horizontal" gap={2} className="w-100">
                <div className="w-75 pe-0">
                    <Form.Control size="sm" type="text" placeholder='Search for products here'
                        value={searchText}
                        onChange={(e) => handleSearch(e)} />
                </div>
                <div>
                    <Button className="w-0 p-0" type="reset" variant="link" onClick={handleClear} title='Clear search text'><BsEraser /></Button>
                </div>
                <div>
                    {addButtonVisible === true ?
                        <Button className="w-0 p-0" variant="link" onClick={handleNewProduct} title='Create new product' disabled={results && results.length > 0}><BsPlusLg /></Button> : <span />}
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
                            <span>{camelCase(item.code)} {item.description === "" ? "" : ' - ' + item.description}</span></td>
                        <td className="w-0 p-0 border-start-0 colorfy">
                            <Button onClick={() => handleSelect(item)} variant="link" title='Select this product'><BsCheck2All /></Button>
                        </td>
                    </tr>

                );
            })
        );
    }

    function showAlert(type, message) {
        setAlert({
            show: true,
            type: type,
            message: message
        })
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
        handleSelect(res);
        showAlert(VariantType.SUCCESS, "Product saved and added to the list successfully ");
        setShowProductForm(false);
    }

    function renderProductForm() {
        if (showProductForm) {
            return (
                <div>
                    <div className="me-3 d-flex justify-content-end align-items-center">
                        <CloseButton aria-label="Hide" onClick={() => setShowProductForm(false)} />
                    </div>
                    <ProductForm product={product} handleSave={handleSaveAndAddNewProduct} />
                </div>
            );
        }
    }

    return (
        <Accordion flush defaultActiveKey="0" id="search-accordion" >
            <Card.Header className="m-2 d-flex justify-content-between">
                {renderSearchBar()}
                <SearchToggle eventKey="0" />
            </Card.Header>
            <Accordion.Collapse eventKey="0">
                <Card.Body className="m-3 mb-0">
                    <span style={{ color: 'red', fontSize: '11px' }}>{notFoundMessage}</span>
                    {renderProductForm()}
                    {results ? (
                        <Table className="align-middle border-0" hover>
                            <tbody>
                                {renderResults()}
                            </tbody>
                        </Table>)
                        : <span />
                    }
                </Card.Body>
            </Accordion.Collapse>
        </Accordion >

    );

    function SearchToggle({ children, eventKey }) {
        const decoratedOnClick = useAccordionButton(eventKey,
        );

        return (
            <Button variant="link" onClick={decoratedOnClick} ><BsChevronDown /></Button>
        );
    }
}
export default ProductSearchBar;