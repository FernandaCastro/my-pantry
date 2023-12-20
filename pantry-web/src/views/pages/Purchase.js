import React, { useState, useEffect, useContext } from 'react';
import { getOpenPurchaseOrder, postClosePurchaseOrder, postNewPurchaseOrder, getPendingPurchaseItems } from '../../services/apis/mypantry/fetch/requests/PurchaseRequests.js';
import { ListGroup } from 'react-bootstrap';
import Button from 'react-bootstrap/Button';
import Stack from 'react-bootstrap/Stack';
import Row from 'react-bootstrap/Row';
import Col from 'react-bootstrap/Col';
import Image from 'react-bootstrap/Image';
import food from '../../images/healthy-food.png'
import VariantType from '../components/VariantType.js';
import { AlertContext } from '../../services/context/AppContext.js';
import Form from 'react-bootstrap/Form';
import NumericField from '../components/NumericField.js'
import Table from 'react-bootstrap/Table';

export default function Purchase() {

    const [purchase, setPurchase] = useState({});
    const [purchaseItems, setPurchaseItems] = useState([]);
    const [filteredItems, setFilteredItems] = useState([]);

    const [isLoading, setIsLoading] = useState(true);
    const [hasOpenOrder, setHasOpenOrder] = useState(false);
    const [searchText, setSearchText] = useState("");
    const { alert, setAlert } = useContext(AlertContext);

    useEffect(() => {
        fetchOpenPurchaseOrder();
        if (!hasOpenOrder) fetchPendingPurchaseItems();
    }, []);

    useEffect(() => {
        filter(searchText);
    }, [purchaseItems])

    async function fetchOpenPurchaseOrder() {
        try {
            setIsLoading(true);
            const res = await getOpenPurchaseOrder();
            if (!res) return;
            setPurchase(res);
            setPurchaseItems(res.items);
            setHasOpenOrder(true);
            setIsLoading(false);
        } catch (error) {
            showAlert(VariantType.DANGER, error.message);
        }
    }

    async function fetchClosePurchaseOrder(body) {
        try {
            setIsLoading(true);
            const res = await postClosePurchaseOrder(body);
            setPurchase(res);
            setPurchaseItems([]);
            setHasOpenOrder(false);

            showAlert(VariantType.SUCCESS, "Purchase Order closed successfully!");
        } catch (error) {
            showAlert(VariantType.DANGER, error.message);

        } finally {
            setIsLoading(false);
        }
    }

    async function fetchNewPurchaseOrder() {
        try {
            setIsLoading(true);
            const res = await postNewPurchaseOrder();
            setPurchase(res);
            setPurchaseItems(res.items);
            setHasOpenOrder(true);
            showAlert(VariantType.SUCCESS, "Purchase Order created successfully!");
        } catch (error) {
            showAlert(VariantType.DANGER, error.message);
        } finally {
            setIsLoading(false);
        }
    }

    async function fetchPendingPurchaseItems() {
        try {
            setIsLoading(true);
            const res = await getPendingPurchaseItems();

            if (isNull(res) || res.length === 0) {
                return showAlert(VariantType.INFO, "No item to purchase at the moment.");
            }

            setPurchaseItems(res);
            setHasOpenOrder(false);
            setIsLoading(false);
        } catch (error) {
            showAlert(VariantType.DANGER, error.message);
        } finally {
            setIsLoading(false);
        }
    }

    function isNull(object) {
        if (!object || (Object.keys(object).length === 0 && object.constructor === Object)) return true;
        return false;
    }

    function showAlert(type, message) {
        setAlert({
            show: true,
            type: type,
            message: message
        })
    }

    function handleSave() {
        let order = purchase;
        order = {
            ...order,
            items: purchaseItems
        }

        fetchClosePurchaseOrder(order);
    }

    function handleNewOrder() {
        fetchNewPurchaseOrder();
    }

    function handleRefresh() {
        fetchPendingPurchaseItems();
    }

    function handleClear() {
        return setPurchaseItems(purchase.items);
    }

    function renderPurchaseItems() {
        if (isLoading || isNull(purchaseItems)) return;

        return (filteredItems.map((item) => renderPurchaseItem(item)))
    }

    function filter(text) {
        if (text && text.length > 0) {
            setFilteredItems(purchaseItems.filter(item => item.product.code.toUpperCase().includes(text.toUpperCase())));
        } else {
            setFilteredItems(purchaseItems);
        }
        setSearchText(text);
    }

    function updatePurchasedItem(item) {
        const array = purchaseItems.map((c) => {
            return (c.pantryId === item.pantryId && c.product.id === item.product.id) ?
                c = { ...c, qtyPurchased: c.qtyPurchased } : c;
        })
        setPurchaseItems(array);
    }

    function renderPurchaseItem(item) {
        if (isLoading) return;
        return (
            <tr key={item.id} className="border border-primary-subtle align-middle">
                <td>
                    <Stack direction="horizontal" gap={2}>
                        <div><Image src={food} width={20} height={20} rounded /></div>
                        <div><span>{item.product.code}</span></div>
                    </Stack>
                    <span className='d-none d-md-block' hidden={item.productDescriptionn === ''}>
                        <br /> {item.product.descriptionn}  {item.product.size}
                    </span>
                </td>
                <td><span className='d-none d-md-block'>{item.pantryName}</span></td>
                <td><span>{item.qtyProvisioned}</span></td>
                <td><NumericField object={item} attribute="qtyPurchased" onValueChange={updatePurchasedItem} disabled={!hasOpenOrder} /></td>
            </tr>
        )
    }

    function renderPurchaseOrder() {
        if (isLoading || isNull(purchase)) return;

        return (
            <ListGroup.Item variant="primary" key={purchase.id}>
                <Row>
                    <Col xs={1}><span className='pb-2'>Id</span></Col>
                    <Col><span>Open Date</span></Col>
                    <Col><span>Close Date</span></Col>
                </Row>
                <Row className='pt-2'>
                    <Col xs={1}><span className='mt-2'>{purchase.id}</span></Col>
                    <Col><span>{purchase.createdAt}</span></Col>
                    <Col><span>{purchase.processedAt}</span></Col>
                </Row>
            </ListGroup.Item>
        )
    }

    return (
        <Stack gap={3}>
            <div>
            </div>
            <div>
                <ListGroup>
                    <ListGroup.Item variant="primary" className="d-flex justify-content-between align-items-start">
                        <h6 className='text-primary'>Open Purchase Order</h6>
                        <Button variant="primary" size="sm" onClick={handleNewOrder} disabled={hasOpenOrder}>New Order</Button>
                    </ListGroup.Item>
                    {renderPurchaseOrder()}
                </ListGroup>
            </div>
            <div></div>
            <div>
                <ListGroup>
                    <ListGroup.Item variant="primary" className="d-flex justify-content-between align-items-start">
                        <h6 className='text-primary'>Items to Purchase</h6>
                        <Button variant="primary" size="sm" onClick={handleRefresh} disabled={hasOpenOrder}>Refresh</Button>
                    </ListGroup.Item>
                </ListGroup>
            </div>
            <div>
                <Form.Control hidden={isNull(purchaseItems) || purchaseItems.length === 0} size="sm" type="text" id="search" className="form-control mb-1" placeholder="Seacrh for items here" value={searchText} onChange={(e) => filter(e.target.value)} />
                <Table variant="primary" className="rounded-2 overflow-hidden " hover>
                    <tbody >
                        <tr key="0:0" className="border border-primary-subtle align-middle" style={{ borderRadius: '6px', overflow: 'hidden' }}>
                            <th scope="col"><span>Code/Desc.</span></th>
                            <th scope="col"><span className='d-none d-md-block'>Pantry</span></th>
                            <th scopy="col"><span>Provis.</span></th>
                            <th scope="col"><span>Purchase</span></th>
                        </tr>
                        {renderPurchaseItems()}
                    </tbody>
                </Table>
            </div>
            <Stack direction="horizontal" gap={2} className="d-flex justify-content-end">
                <div><Button variant="primary" size="sm" onClick={handleClear} disabled={!hasOpenOrder}>Clear</Button></div>
                <div><Button variant="primary" size="sm" onClick={handleSave} disabled={!hasOpenOrder}>Save</Button></div>
            </Stack>
        </Stack>

    )
}