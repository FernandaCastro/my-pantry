import Navbar from './Navbar.js';
import { useState, useEffect } from 'react';

export default function Purchase() {

    const URL_GET_OPEN_PURCHASE = "http://192.168.0.12:8081/purchases/open";
    const URL_POST_PURCHASE = "http://192.168.0.12:8081/purchases";
    const URL_POST_CLOSE_PURCHASE = "http://192.168.0.12:8081/purchases/close";
    const URL_GET_PENDING_PURCHASE = "http://192.168.0.12:8081/purchases/items";

    const [purchase, setPurchase] = useState({});
    const [purchaseItems, setPurchaseItems] = useState([]);

    useEffect(() => {

        fetch(URL_GET_OPEN_PURCHASE)
            .then((response) => response.json())
            .then((data) => {
                setPurchase(data);
                setPurchaseItems(data.items);
            })
            .catch((error) => { console.log(error) })
    }, [URL_GET_OPEN_PURCHASE]);

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

    function handleSave() {
        let copy = purchase;
        copy = {
            ...copy,
            items: purchaseItems
        }

        fetch(URL_POST_CLOSE_PURCHASE,
            {
                method: 'POST',
                headers: {
                    "Content-Type": "application/json",
                },
                body: JSON.stringify(copy)
            })

            .then((response) => response.json())
            .then((response) => {
                setPurchase(response);
                setPurchaseItems(response.items);
                alert("Purchase Order closed successfully!");
            })
            .catch((error) => { console.log(error) })
    }

    function handleNewOrder() {
        fetch(URL_POST_PURCHASE,
            {
                method: 'POST',
                headers: {
                    "Content-Type": "application/json",
                },
                body: JSON.stringify({})
            })

            .then((response) => response.json())
            .then((response) => {
                setPurchase(response);
                setPurchaseItems(response.items);
                alert("Purchase Order created successfully!");
            })
            .catch((error) => { console.log(error) })
    }

    function handleLoadItems() {
        fetch(URL_GET_PENDING_PURCHASE)
            .then((response) => response.json())
            .then((response) => {
                return setPurchaseItems(response);
            })
            .catch((error) => { console.log(error) })
    }

    function renderItem(index, item) {

        return (
            <li className="collection-item avatar" key={item.id}>
                <div className="row">
                    <div className="col s4">
                        <img src="./healthy-food.png" alt="" className="circle" />
                        <span className="title" >{item.productId}</span>
                    </div>
                    <div className="col s3"><span className="teal-text text-lighten-2" >Pantry</span></div>
                    <div className="col s2"><span className="teal-text text-lighten-2" >Provisioned</span></div>
                    <div className="col s3"><span className="teal-text text-lighten-2" >Purchased</span></div>
                </div>
                <div className="row">

                    <div className="col s4">
                        <p>
                            {item.productDescription} <br />
                            {item.productSize}
                        </p>
                    </div>

                    <div className="col s3">
                        <span>{item.pantryName}</span>
                    </div>

                    <div className="col s2">
                        <span>{item.qtyProvisioned}</span>
                    </div>

                    <div className="col s3">
                        <center>
                            <a href="#!" className="left waves-effect waves-circle waves-light btn-floating secondary-content-content"
                                style={item.qtyPurchased === 0 ? { pointerEvents: "none" } : { pointerEvents: "auto" }}
                                onClick={() => handleDecrease(index)} >
                                <i className="material-icons" >remove</i></a>
                            <span>{item.qtyPurchased}</span>
                            <a href="#!" className="right waves-effect waves-circle waves-light btn-floating secondary-content-content"
                                onClick={() => handleIncrease(index)}>
                                <i className="material-icons" >add</i></a>
                        </center>
                    </div>
                </div>
            </li>
        )
    }


    function renderItems() {
        if (purchaseItems != null) {
            let index = 0;
            return (purchaseItems.map((item) => renderItem(index++, item)))
        }
    }

    function renderPurchase() {
        if (purchase != null) {
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
    }

    function renderNewOrderButton() {
        if (Object.keys(purchase).length === 0 && purchase.constructor === Object) {
            return (
                <a href="#!" className="right waves-effect waves-light btn-small"
                    onClick={handleNewOrder}>
                    <i className="material-icons left">fiber_new</i>
                    New Order
                </a>
            )
        }
    }

    function renderLoadItemsButton() {
        if (Object.keys(purchase).length === 0 && purchase.constructor === Object) {
            return (
                <a href="#!" className="right waves-effect waves-light btn-small"
                    onClick={handleLoadItems}>
                    <i className="material-icons left">autorenew</i>
                    Refresh
                </a>
            )
        }
    }

    return (
        <>
            <Navbar />
            <div className='row'>
                <div className="col s12">
                    <ul className="collection with-header">
                        <li className="collection-header teal-text text-lighten-2">
                            {renderNewOrderButton()}
                            <h5>Open Purchase Order</h5>
                        </li>
                        {renderPurchase()}
                    </ul>
                </div>
            </div>
            <div className='row'>
                <div className="col s12">
                    <ul className="collection with-header">
                        <li className="collection-header teal-text text-lighten-2">
                            {renderLoadItemsButton()}
                            <h5>Items to Purchase</h5>
                        </li>
                        {renderItems()}
                    </ul>
                </div>
            </div>
            <div className='row'>
                <div className="col s12">
                    <a href="#!" className="right waves-effect waves-light btn-small"
                        style={purchase !== null && purchase.items !== null ? { pointerEvents: "auto" } : { pointerEvents: "none" }}
                        onClick={handleSave}>
                        <i className="material-icons left">done_all</i>
                        Save
                    </a>
                    <span>&nbsp;&nbsp;&nbsp;&nbsp;</span>
                    <a href="#!" className='right waves-effect waves-light btn-small'
                        style={purchase !== null && purchase.items !== null ? { pointerEvents: "auto" } : { pointerEvents: "none" }}
                        onClick={() => resetItemsList()}>
                        <i className="material-icons left">clear</i>
                        Clear
                    </a>
                </div>
            </div>
        </>
    )
}