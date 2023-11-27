import React, { useState, useEffect } from 'react';
import { getOpenPurchaseOrder, postClosePurchaseOrder, postNewPurchaseOrder, getPendingPurchaseItems } from '../../../services/apis/mypantry/fetch/requests/PurchaseRequests.js';

export default function Purchase() {

    const [purchase, setPurchase] = useState({});
    const [purchaseItems, setPurchaseItems] = useState([]);
    const [isLoading, setIsLoading] = useState(true);
    const [hasOpenOrder, setHasOpenOrder] = useState(false);

    useEffect(() => {
        fetchOpenPurchaseOrder();
        if (!hasOpenOrder) fetchPendingPurchaseItems();
    }, []);

    async function fetchOpenPurchaseOrder() {
        const res = await getOpenPurchaseOrder();
        if (!res) return;
        setPurchase(res);
        setPurchaseItems(res.items);
        setHasOpenOrder(true);
        setIsLoading(false);
    }

    async function fetchClosePurchaseOrder(body) {
        const res = await postClosePurchaseOrder(body);
        if (!res) return;
        setPurchase(res);
        setPurchaseItems([]);
        setHasOpenOrder(false);
        setIsLoading(false);
    }

    async function fetchNewPurchaseOrder() {
        const res = await postNewPurchaseOrder();
        if (!res) return;
        setPurchase(res);
        setPurchaseItems(res.items);
        setHasOpenOrder(true);
        setIsLoading(false);
    }

    async function fetchPendingPurchaseItems() {
        const res = await getPendingPurchaseItems();
        if (!res) return;
        setPurchaseItems(res);
        setHasOpenOrder(false);
        setIsLoading(false);
    }

    function isNull(object) {
        if (!object || (Object.keys(object).length === 0 && object.constructor === Object)) return true;
        return false;
    }

    function handleSaveButton() {
        let order = purchase;
        order = {
            ...order,
            items: purchaseItems
        }

        fetchClosePurchaseOrder(order);
        alert("Purchase Order closed successfully!");
    }

    function handleNewOrderButton() {
        fetchNewPurchaseOrder();
        alert("Purchase Order created successfully!");
    }

    function handleRefreshItemsButton() {
        fetchPendingPurchaseItems();
    }

    function handleDecrease(index) {
        const array = purchaseItems.map((c, i) => {
            if (i === index) {
                return c =
                {
                    ...c,
                    qtyPurchased: c.qtyPurchased - 1
                };
            } else {
                return c;
            }
        });
        return setPurchaseItems(array);
    }

    function handleIncrease(index) {
        const array = purchaseItems.map((c, i) => {
            if (i === index) {
                return c =
                {
                    ...c,
                    qtyPurchased: c.qtyPurchased + 1
                };
            } else {
                return c;
            }
        });
        return setPurchaseItems(array);
    }

    function resetItemsList() {
        return setPurchaseItems(purchase.items);
    }

    function renderItem(index, item) {

        return (
            <li className="collection-item avatar" key={item.id}>
                <div className="row">
                    <div className="col s4">
                        <img src="./healthy-food.png" alt="" className="circle" />
                        <span className="title" >{item.productCode}</span>
                    </div>
                    <div className="col s3"><span className="teal-text text-lighten-2" >Pantry</span></div>
                    <div className="col s2"><span className="teal-text text-lighten-2" >Provisioned</span></div>
                    <div className="col s3"><span className="teal-text text-lighten-2" >Purchased</span></div>
                </div>
                <div className="row">

                    <div className="col s4">
                        <p className="blue-grey-text text-darken-3">
                            {item.productDescription} <br />
                            {item.productSize}
                        </p>
                    </div>

                    <div className="col s3">
                        <span className="blue-grey-text text-darken-3">{item.pantryName}</span>
                    </div>

                    <div className="col s2">
                        <span className="blue-grey-text text-darken-3">{item.qtyProvisioned}</span>
                    </div>

                    <div className="col s3">
                        <center>
                            <a href="#!" className="left"
                                style={item.qtyPurchased === 0 ? { pointerEvents: "none" } : { pointerEvents: "auto" }}
                                onClick={() => handleDecrease(index)} >
                                <i className="material-icons" >remove</i></a>
                            <span>{item.qtyPurchased}</span>
                            <a href="#!" className="right"
                                style={!hasOpenOrder ? { pointerEvents: "none" } : { pointerEvents: "auto" }}
                                onClick={() => handleIncrease(index)}>
                                <i className="material-icons" >add</i></a>
                        </center>
                    </div>
                </div>
            </li>
        )
    }


    function renderItems() {
        if (isNull(purchaseItems)) return;

        let index = 0;
        return (purchaseItems.map((item) => renderItem(index++, item)))
    }

    function renderPurchase() {
        if (isNull(purchase)) return;

        return (
            <li className="collection-item" key={purchase.id}>
                <div className="row">
                    <div className="col s2"><span className="teal-text text-lighten-2" >Id</span></div>
                    <div className="col s3"><span className="teal-text text-lighten-2" >Open Date</span></div>
                    <div className="col s3"><span className="teal-text text-lighten-2" >Close Date</span></div>
                </div>
                <div className="row">
                    <div className="col s2">
                        <span>{purchase.id}</span>
                    </div>

                    <div className="col s3">
                        <span>{purchase.createdAt}</span>
                    </div>

                    <div className="col s3">
                        <span>{purchase.processedAt}</span>
                    </div>
                </div>
            </li >
        )

    }

    function renderNewOrderButton() {
        if (!hasOpenOrder) {
            return (
                <a href="#!" className="right waves-effect waves-light btn-small"
                    onClick={handleNewOrderButton}>
                    <i className="material-icons left">fiber_new</i>
                    New Order
                </a>
            )
        }
    }

    function renderRefreshItemsButton() {
        if (!hasOpenOrder) {
            return (
                <a href="#!" className="right waves-effect waves-light btn-small"
                    onClick={handleRefreshItemsButton}>
                    <i className="material-icons left">autorenew</i>
                    Refresh
                </a>
            )
        }
    }

    return (
        <>
            <div className='row'>
                <div className="col s12">
                    <ul className="collection with-header">
                        <li className="collection-header teal-text text-lighten-2">
                            {renderNewOrderButton()}
                            <h6>Open Purchase Order</h6>
                        </li>
                        {renderPurchase()}
                    </ul>
                </div>
            </div>
            <div className='row'>
                <div className="col s12">
                    <ul className="collection with-header">
                        <li className="collection-header teal-text text-lighten-2">
                            {renderRefreshItemsButton()}
                            <h5>Items to Purchase</h5>
                        </li>
                        {renderItems()}
                    </ul>
                </div>
            </div>
            <div className='row'>
                <div className="col s12">
                    <a href="#!" className="right waves-effect waves-light btn-small"
                        style={hasOpenOrder ? { pointerEvents: "auto" } : { pointerEvents: "none" }}
                        onClick={handleSaveButton}>
                        <i className="material-icons left">done_all</i>
                        Save
                    </a>
                    <span>&nbsp;&nbsp;&nbsp;&nbsp;</span>
                    <a href="#!" className='right waves-effect waves-light btn-small'
                        style={hasOpenOrder ? { pointerEvents: "auto" } : { pointerEvents: "none" }}
                        onClick={() => resetItemsList()}>
                        <i className="material-icons left">clear</i>
                        Clear
                    </a>
                </div>
            </div>
        </>
    )
}