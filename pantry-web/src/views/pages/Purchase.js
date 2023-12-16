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
            if (!res) return;
            setPurchase(res);
            setPurchaseItems([]);
            setHasOpenOrder(false);
            setIsLoading(false);
        } catch (error) {
            showAlert(VariantType.DANGER, error.message);
        }
    }

    async function fetchNewPurchaseOrder() {
        try {
            setIsLoading(true);
            const res = await postNewPurchaseOrder();
            if (isNull(res)) return;
            setPurchase(res);
            setPurchaseItems(res.items);
            setHasOpenOrder(true);
            setIsLoading(false);
        } catch (error) {
            showAlert(VariantType.DANGER, error.message);
        }
    }

    async function fetchPendingPurchaseItems() {
        try {
            setIsLoading(true);
            const res = await getPendingPurchaseItems();
            if (isNull(res) || res.length === 0) return;
            setPurchaseItems(res);
            setHasOpenOrder(false);
            setIsLoading(false);
        } catch (error) {
            showAlert(VariantType.DANGER, error.message);
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
        try {
            let order = purchase;
            order = {
                ...order,
                items: purchaseItems
            }

            fetchClosePurchaseOrder(order);
            showAlert(VariantType.SUCCESS, "Purchase Order closed successfully!");
        } catch (error) {
            showAlert(VariantType.DANGER, error.message);
        }
    }

    function handleNewOrder() {
        fetchNewPurchaseOrder();
        showAlert(VariantType.SUCCESS, "Purchase Order created successfully!");
    }

    function handleRefresh() {
        fetchPendingPurchaseItems();
        if (isNull(purchaseItems) || purchaseItems.length === 0) {
            showAlert(VariantType.INFO, "No item to purchase at the moment.");
        }
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
            setFilteredItems(purchaseItems.filter(item => item.productCode.toUpperCase().includes(text.toUpperCase())));
        } else {
            setFilteredItems(purchaseItems);
        }
        setSearchText(text);
    }

    function updatePurchasedItem(item) {
        const array = purchaseItems.map((c) => {
            return (c.pantryId === item.pantryId && c.productId === item.productId) ?
                c = { ...c, qtyPurchased: c.qtyPurchased } : c;
        })
        setPurchaseItems(array);
    }

    function renderPurchaseItem(item) {
        if (isLoading) return;
        return (
            <ListGroup.Item variant="primary" key={item.id} className="align-items-start">
                <Row>
                    <Col xs={5}>
                        <Stack direction="horizontal" gap={3}>
                            <div>
                                <Image src={food} width={25} height={25} rounded />
                            </div>
                            <div>
                                <span>{item.productCode}</span>
                            </div>
                        </Stack>
                    </Col>
                    <Col className='d-none d-md-block'><span>Pantry</span></Col>
                    <Col><span>Provisioned</span></Col>
                    <Col><span>Purchased</span></Col>
                </Row>
                <Row>
                    <Col xs={5}>
                        <p className='pt-1 d-none d-md-block'>
                            {item.productDescription} - {item.productSize}
                        </p>
                    </Col>
                    <Col className='d-none d-md-block'>{item.pantryName}</Col>
                    <Col>{item.qtyProvisioned}</Col>
                    <Col>
                        <NumericField object={item} attribute="qtyPurchased" onValueChange={updatePurchasedItem} disabled={!hasOpenOrder} />
                    </Col>
                </Row>
            </ListGroup.Item>
        )

        //     <Stack direction="horizontal" gap={1} >
        //     <div><Button variant='link' disabled={item.qtyPurchased === 0} onClick={() => handleDecrease(index)} className='m-0 p-0 d-flex align-items-start'><BsCaretDown /></Button></div>
        //     <div><span className='ms-1 me-1 ps-1 pe-1'>{item.qtyPurchased}</span></div>
        //     <div><Button variant='link' disabled={!hasOpenOrder} onClick={() => handleIncrease(index)} className='m-0 p-0 d-flex align-items-start'><BsCaretUp /></Button></div>
        // </Stack>
    }

    function renderPurchaseOrder() {
        if (isLoading || isNull(purchase)) return;

        return (
            <ListGroup.Item variant="primary" key={purchase.id}>
                <Row>
                    <Col xs={2}><span>Id</span></Col>
                    <Col><span>Open Date</span></Col>
                    <Col><span>Close Date</span></Col>
                </Row>
                <Row>
                    <Col xs={2}><span>{purchase.id}</span></Col>
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
                        <h6>Open Purchase Order</h6>
                        <Button variant="primary" size="sm" onClick={handleNewOrder} disabled={hasOpenOrder}>New Order</Button>
                    </ListGroup.Item>
                </ListGroup>
            </div>
            <div>
                <ListGroup>
                    {renderPurchaseOrder()}
                </ListGroup>
            </div>
            <div></div>
            <div>
                <ListGroup>
                    <ListGroup.Item variant="primary" className="d-flex justify-content-between align-items-start">
                        <h6>Items to Purchase</h6>
                        <Button variant="primary" size="sm" onClick={handleRefresh} disabled={hasOpenOrder}>Refresh</Button>
                    </ListGroup.Item>
                </ListGroup>
            </div>
            <div>
                <Form.Control hidden={isNull(purchaseItems) || purchaseItems.length === 0} size="sm" type="text" id="search" className="form-control mb-1" placeholder="Seacrh for items here" value={searchText} onChange={(e) => filter(e.target.value)} />
                <ListGroup>
                    {renderPurchaseItems()}
                </ListGroup>
            </div>
            <Stack direction="horizontal" gap={2} className="d-flex justify-content-end">
                <div><Button variant="primary" size="sm" onClick={handleClear} disabled={!hasOpenOrder}>Clear</Button></div>
                <div><Button variant="primary" size="sm" onClick={handleSave} disabled={!hasOpenOrder}>Save</Button></div>
            </Stack>
        </Stack>

    )
}