import Form from 'react-bootstrap/Form';
import Row from 'react-bootstrap/Row';
import Col from 'react-bootstrap/Col';
import Button from 'react-bootstrap/Button';
import { useState, useContext } from 'react';
import { BsEraser, BsCheck2All } from "react-icons/bs";
import VariantType from '../components/VariantType.js';
import { getProductList } from '../../services/apis/mypantry/fetch/requests/PantryRequests.js';
import { SetAlertContext } from '../../services/context/PantryContext.js';
import Table from 'react-bootstrap/Table';
import Accordion from 'react-bootstrap/Accordion';
import { AccordionHeader, AccordionItem } from 'react-bootstrap';
import AccordionBody from 'react-bootstrap/esm/AccordionBody.js';
import '../../styles/ProductSearchBar.css';

function ProductSearchBar({ handleAction }) {

    const [type, setType] = useState("code");
    const [text, setText] = useState("");
    const [results, setResults] = useState([]);
    const setAlert = useContext(SetAlertContext);

    function handleSearch(e) {
        if (e.target.value.length > 2) fetchProduct();
        setText(e.target.value)
    }
    function handleClear() {
        setText("");
        setResults([]);
    }
    function handleAdd(item) {
        handleAction(item);
    }

    async function fetchProduct() {
        try {
            const res = await getProductList(type, text);
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
            <Form id="searchBar">
                <Row className="align-items-center">
                    <Col xs={2} className="w-25 pe-0">
                        <Form.Select size="sm" name="searchType" defaultValue={type}
                            onChange={(e) => setType(e.target.value)}>
                            <option value="code">Code</option>
                            <option value="description">Description</option>
                        </Form.Select>
                    </Col>
                    <Col xs={4} className="w-50 pe-0">
                        <Form.Control size="sm" type="text" placeholder='Enter your search text here'
                            value={text}
                            onChange={(e) => handleSearch(e)} />
                    </Col>
                    <Col xs={2} className="ps-0 pe-0">
                        <Button type="reset" variant="link" onClick={handleClear}><BsEraser /></Button>
                    </Col>
                </Row>
            </Form >
        );
    }

    function renderResults() {
        return (
            results.map((item) => {
                return (
                    <tr key={item.id} className="align-items-center">
                        <td className="p-0 border-end-0">
                            <span>{item.code} - {item.description}</span></td>
                        <td className="p-0 border-start-0">
                            <Button onClick={() => handleAdd(item)} variant="link"><BsCheck2All /></Button>
                        </td>
                    </tr>

                );
            })
        );
    }

    return (
        <Accordion flush defaultActiveKey="" id="search-accordion" className="border border-primary-subtle rounded">
            <AccordionItem className='rounded'>
                <AccordionHeader>
                    {renderSearchBar()}
                </AccordionHeader>
                <AccordionBody>
                    {results ?
                        <Table responsive="sm" hover>
                            <tbody>
                                {renderResults()}
                            </tbody>
                        </Table>
                        : <span />}
                </AccordionBody>
            </AccordionItem>
        </Accordion>

    );
}
export default ProductSearchBar;