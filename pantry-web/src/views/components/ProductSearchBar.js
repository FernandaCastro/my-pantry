import Form from 'react-bootstrap/Form';
import Button from 'react-bootstrap/Button';
import { useState, useContext } from 'react';
import { BsEraser, BsCheck2All, BsChevronDown } from "react-icons/bs";
import VariantType from '../components/VariantType.js';
import { getFilteredProductList } from '../../services/apis/mypantry/fetch/requests/PantryRequests.js';
import { SetAlertContext } from '../../services/context/PantryContext.js';
import Table from 'react-bootstrap/Table';
import Accordion from 'react-bootstrap/Accordion';
import { useAccordionButton } from 'react-bootstrap/AccordionButton';
import Card from 'react-bootstrap/Card';
import Stack from 'react-bootstrap/Stack';

import '../../styles/ProductSearchBar.css';

function ProductSearchBar({ handleSelectAction, handleClearAction }) {

    const [type, setType] = useState("code");
    const [text, setText] = useState("");
    const [results, setResults] = useState([]);
    const setAlert = useContext(SetAlertContext);

    function handleSearch(e) {
        if (e.target.value.length < 3 && text.length < 4) setResults([]);
        if (e.target.value.length > 2) fetchProduct(e.target.value);
        setText(e.target.value);
    }
    function clearSearch() {
        setText("");
        setResults([]);
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
            const res = await getFilteredProductList(type, value);
            console.log(res);
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
                <div className="pe-0">
                    <Form.Select size="sm" name="searchType" defaultValue={type}
                        onChange={(e) => setType(e.target.value)}>
                        <option value="code">Code</option>
                        <option value="description">Description</option>
                    </Form.Select>
                </div>
                <div className="w-75 pe-0">
                    <Form.Control size="sm" type="text" placeholder='Enter your search text here'
                        value={text}
                        onChange={(e) => handleSearch(e)} />
                </div>
                <div>
                    <Button className="w-0 p-0" type="reset" variant="link" onClick={handleClear}><BsEraser /></Button>
                </div>
            </Stack>
        );
    }

    function renderResults() {
        return (
            results.map((item) => {
                return (
                    <tr key={item.id} className="w-0 p-0">
                        <td className="w-0 p-0 border-end-0">
                            <span>{item.code} - {item.description}</span></td>
                        <td className="w-0 p-0 border-start-0">
                            <Button onClick={() => handleSelect(item)} variant="link"><BsCheck2All /></Button>
                        </td>
                    </tr>

                );
            })
        );
    }

    return (
        <Accordion flush defaultActiveKey="0" id="search-accordion" className="border border-primary-subtle rounded">
            <Card.Header className="m-2 d-flex justify-content-between">
                {renderSearchBar()}
                <SearchToggle eventKey="0" />
            </Card.Header>
            <Accordion.Collapse eventKey="0">
                <Card.Body className="m-3 mb-0">
                    {results ? (
                        <Table className="table table-sm align-middle" hover>
                            <tbody>
                                {renderResults()}
                            </tbody>
                        </Table>)
                        : <span />
                    }
                </Card.Body>
            </Accordion.Collapse>

        </Accordion>

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