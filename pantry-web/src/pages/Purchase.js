import React, { useState } from 'react';
import { postClosePurchaseOrder, postNewPurchaseOrder } from '../services/apis/mypantry/requests/PurchaseRequests.js';
import Button from 'react-bootstrap/Button';
import Stack from 'react-bootstrap/Stack';
import VariantType from '../components/VariantType.js';
import useAlert from '../hooks/useAlert.js';
import { BsChevronDown } from "react-icons/bs";
import Collapse from 'react-bootstrap/Collapse';
import PantrySelect from '../components/PantrySelect.js'
import PurchaseOrderList from '../components/PurchaseOrderList.js'
import PurchaseItemList from '../components/PurchaseItemList.js'

export default function Purchase() {

    const [selectedPantries, setSelectedPantries] = useState([]);

    const [purchase, setPurchase] = useState();
    const [purchaseItems, setPurchaseItems] = useState([]);

    const [isLoading, setIsLoading] = useState(true);
    const [hasOpenOrder, setHasOpenOrder] = useState(false);
    const [showPantries, setShowPantries] = useState(true);
    const [showOrder, setShowOrder] = useState(true);
    const [showOrderDetails, setShowOrderDetails] = useState(true);

    const [refreshOrders, setRefreshOrders] = useState(false);

    const { showAlert } = useAlert();

    async function fetchClosePurchaseOrder(body) {
        try {
            setIsLoading(true);
            setRefreshOrders(false);
            await postClosePurchaseOrder(body);
            setPurchase();
            setHasOpenOrder(false);
            setRefreshOrders(true);
            showAlert(VariantType.SUCCESS, "Shopping List closed successfully!");
        } catch (error) {
            showAlert(VariantType.DANGER, error.message);

        } finally {
            setIsLoading(false);
        }
    }

    async function fetchNewPurchaseOrder() {
        try {
            setIsLoading(true);
            setRefreshOrders(false);
            const res = await postNewPurchaseOrder(selectedPantries);
            setPurchase(res);
            setHasOpenOrder(true);
            setRefreshOrders(true);
            showAlert(VariantType.SUCCESS, "Shopping List created successfully!");
        } catch (error) {
            showAlert(VariantType.DANGER, error.message);
        } finally {
            setShowOrder(true);
            setIsLoading(false);
        }
    }

    function isNull(object) {
        if (!object || (Object.keys(object).length === 0 && object.constructor === Object)) return true;
        return false;
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

    function handleClear() {
        // return fetchPurchaseItems(true);
    }

    function handleSelectedPantries(list) {
        setSelectedPantries(list);
    }

    function selectPurchase(p) {
        setPurchase(p);
        (!isNull(p) && !p.processedAt) ? setHasOpenOrder(true) : setHasOpenOrder(false);
    }

    return (
        <Stack gap={3}>
            <div />
            <div>
                <div className='d-flex justify-content-start align-items-center gap-2' onClick={() => setShowPantries(!showPantries)}>
                    <h6 className="text-start fs-6 lh-lg title">Pantries </h6>
                    <BsChevronDown className='icon' />
                </div>

                <Collapse in={showPantries} >
                    <div><PantrySelect handleSelectedPantryList={handleSelectedPantries} permission='purchase_pantry' /></div>
                </Collapse>
            </div>
            
            <div className="item d-flex justify-content-between align-items-start" >
                <div className='d-flex justify-content-start align-items-center gap-2' onClick={() => setShowOrder(!showOrder)}>
                    <h6 className='title'>Shopping Lists</h6>
                    <BsChevronDown className='icon' />
                </div>
                <Button bsPrefix="btn-custom" size="sm" onClick={handleNewOrder} disabled={hasOpenOrder}>New Order</Button>
            </div>

            <div>
                <Collapse in={showOrder} >
                    <div>
                        <PurchaseOrderList key={refreshOrders} selectedPantries={selectedPantries} handleSelectedPurchase={selectPurchase} />
                    </div>
                </Collapse>
            </div>

            <div>
                <div className='d-flex justify-content-start align-items-center gap-2' onClick={() => setShowOrderDetails(!showOrderDetails)} aria-controls="purchaseItems" >
                    <h6 className='title'>{purchase ? purchase.processedAt ? 'Closed Shopping List - Details' : 'Open Shopping List - Details' : 'Items not in a Shopping List'}</h6>
                    <BsChevronDown className='icon' />
                </div>
                <Collapse in={showOrderDetails} >
                    <div id="purchaseItems" className='purchaseList'>
                        <PurchaseItemList purchase={purchase} selectedPantries={selectedPantries} setOuterPurchaseItems={setPurchaseItems} />
                    </div>
                </Collapse>
            </div>


            <div className='d-flex justify-content-end gap-2'>
                {/* <Button bsPrefix="btn-custom" size="sm" onClick={handleClear} disabled={!hasOpenOrder}>Clear</Button> */}
                <Button bsPrefix="btn-custom" size="sm" onClick={handleSave} disabled={!hasOpenOrder}>Checkout</Button>
            </div>

        </Stack >

    )
}