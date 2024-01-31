import React, { useState, useEffect, useContext } from 'react';
import { getOpenPurchaseOrder, postClosePurchaseOrder, postNewPurchaseOrder, getPendingPurchaseItems, getPurchaseItems, getAllProperty } from '../services/apis/mypantry/requests/PurchaseRequests.js';
import { FormGroup } from 'react-bootstrap';
import Button from 'react-bootstrap/Button';
import Stack from 'react-bootstrap/Stack';
import Image from 'react-bootstrap/Image';
import food from '../assets/images/healthy-food.png'
import VariantType from '../components/VariantType.js';
import { AlertContext } from '../services/context/AppContext.js';
import Form from 'react-bootstrap/Form';
import NumericField from '../components/NumericField.js'
import Table from 'react-bootstrap/Table';
import Select from '../components/Select.js';
import { camelCase } from '../services/Utils.js';
import { BsArrow90DegRight, BsChevronDown } from "react-icons/bs";
import Collapse from 'react-bootstrap/Collapse';

export default function Purchase() {

    const [purchase, setPurchase] = useState({});
    const [purchaseItems, setPurchaseItems] = useState([]);
    const [filteredItems, setFilteredItems] = useState([]);

    const [supermarketOption, setSupermarketOption] = useState({ value: "", label: "" });
    const [supermarkets, setSupermarkets] = useState([{}]);
    const [categories, setCategories] = useState([{}]);

    const [isLoading, setIsLoading] = useState(true);
    const [hasOpenOrder, setHasOpenOrder] = useState(false);
    const [showOrder, setShowOrder] = useState(true);
    const [showSupermarkets, setShowSupermarkets] = useState(true);

    const [searchText, setSearchText] = useState("");
    const { setAlert } = useContext(AlertContext);

    useEffect(() => {
        fetchOpenPurchaseOrder();
        fetchSupermarketOptions();
    }, []);

    useEffect(() => {
        filter(searchText);
        populateCategories();
    }, [purchaseItems])

    useEffect(() => {
        if (supermarketOption.value) {
            if (purchase && Object.keys(purchase).length > 0)
                fetchPurchaseItems(supermarketOption.value);
            else
                fetchPendingPurchaseItems(supermarketOption.value);
        }
    }, [supermarketOption])

    async function fetchSupermarketOptions() {
        try {
            const res = await getAllProperty("%25.supermarket.categories");

            var list = [{ value: " ", label: "Alphabetically ordered" }];
            res.forEach(s => {
                var name = s.propertyKey.substring(0, s.propertyKey.indexOf("."));
                //var categories = JSON.parse(s.propertyValue).categories;

                list = [...list,
                {
                    value: name,
                    label: camelCase(name)
                }]
            });

            setSupermarkets(list);
        } catch (error) {
            showAlert(VariantType.DANGER, "Unable to load supermarkets: " + error.message);
        }
    }

    async function fetchOpenPurchaseOrder() {
        try {
            setIsLoading(true);
            const res = await getOpenPurchaseOrder();
            if (!res) {
                fetchPendingPurchaseItems();
                return;
            }
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

    async function fetchPurchaseItems(supermarket) {
        try {
            setIsLoading(true);
            const res = await getPurchaseItems(purchase.id, supermarket);

            if (isNull(res) || res.length === 0) {
                return showAlert(VariantType.INFO, "No item to purchase at the moment.");
            }

            //keep the already entered qtyPurchased when sorting by supermarket
            Object.keys(purchaseItems).length > 0 ?
                keepPurchasedQty(res) :
                setPurchaseItems(res);

            setIsLoading(false);
        } catch (error) {
            showAlert(VariantType.DANGER, error.message);
        } finally {
            setIsLoading(false);
        }
    }

    function keepPurchasedQty(items) {
        const updatedList = items.map(i => {

            var found = purchaseItems.find((p) => p.id === i.id);
            return (found) ?
                i = { ...i, qtyPurchased: found.qtyPurchased } : i;
        });
        setPurchaseItems(updatedList);
    }


    async function fetchPendingPurchaseItems(supermarket) {
        try {
            setIsLoading(true);
            const res = await getPendingPurchaseItems(supermarket);

            if (isNull(res) || res.length === 0) {
                return showAlert(VariantType.INFO, "No item to purchase at the moment.");
            }

            setPurchaseItems(res);
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

    function renderPurchaseItems() {
        if (isLoading || isNull(purchaseItems)) return;

        let category = "";

        return (filteredItems.map((item) => {
            if (category === item.product.category) {
                return renderItem(category, item);
            }
            else {
                category = item.product.category;
                return renderCategory(category, item);
            }
        }))
    }

    function renderCategory(category, item) {
        return (
            <>
                <tbody>
                    <tr key={category} className="highlight" >
                        <td className="highlight" colSpan={4}>
                            <div className="category">
                                <Button variant="link" aria-controls={category} onClick={() => handleExpansion(category)}><BsArrow90DegRight className='icon' /></Button>
                                <h6 className='title'>{!category || category === "" ? "Other" : category}</h6>
                            </div>
                        </td>
                    </tr>
                </tbody>

                <Collapse in={getOpen(category)}>
                    <tbody >
                        <tr key={item.id}>
                            <td>
                                <Stack direction="horizontal" gap={2}>
                                    <div><Image src={food} width={20} height={20} rounded /></div>
                                    <div><span>{camelCase(item.product.code)}</span></div>
                                </Stack>
                                <span className='d-none d-md-block' hidden={item.productDescription === ''}>
                                    {item.product.description}  {item.product.size}
                                </span>
                            </td>
                            <td><span className='d-none d-md-block'>{item.pantryName}</span></td>
                            <td><span>{item.qtyProvisioned}</span></td>
                            <td><NumericField object={item} attribute="qtyPurchased" onValueChange={updatePurchasedItem} disabled={!hasOpenOrder} /></td>
                        </tr>
                    </tbody>
                </Collapse>
            </>
        )
    }

    function renderItem(category, item) {
        return (
            <Collapse in={getOpen(category)}>
                <tbody>
                    <tr key={item.id} >
                        <td>
                            <Stack direction="horizontal" gap={2}>
                                <div><Image src={food} width={20} height={20} rounded /></div>
                                <div><span>{camelCase(item.product.code)}</span></div>
                            </Stack>
                            <span className='d-none d-md-block' hidden={item.productDescription === ''}>
                                {item.product.description}  {item.product.size}
                            </span>
                        </td>
                        <td><span className='d-none d-md-block'>{item.pantryName}</span></td>
                        <td><span>{item.qtyProvisioned}</span></td>
                        <td><NumericField object={item} attribute="qtyPurchased" onValueChange={updatePurchasedItem} disabled={!hasOpenOrder} /></td>
                    </tr>
                </tbody>
            </Collapse>
        )
    }

    function renderPurchaseOrder() {
        if (isLoading || isNull(purchase)) return;

        return (
            <Collapse in={showOrder} >
                <div>
                    <Table className="bordered">
                        <thead >
                            <tr key="order:0" className="align-middle">
                                <th><h6 className='title'>Id</h6></th>
                                <th><h6 className='title'>Open Date</h6></th>
                                <th><h6 className='title'>Close Date</h6></th>
                            </tr>
                        </thead>
                        <tbody>
                            <tr key={purchase.id}>
                                <td><span>{purchase.id}</span></td>
                                <td><span>{purchase.createdAt}</span></td>
                                <td><span>{purchase.processedAt}</span></td>
                            </tr>
                        </tbody>
                    </Table>
                </div>
            </Collapse>
        )
    }

    function handleExpansion(id) {

        var newList = categories.map((c) => {
            return (c.id === id) ?
                c = { ...c, isOpen: !c.isOpen } : c;
        });

        setCategories(newList);
    }

    function getOpen(id) {
        var isOpen = false;
        categories.forEach((c) => {
            if (c.id === id) isOpen = c.isOpen;
        });
        return isOpen;
    }

    function populateCategories() {
        let list = [{}];
        let category = "";

        purchaseItems.forEach((i) => {
            if (category !== i.product.category) {
                category = i.product.category;
                list = [...list,
                {
                    id: i.product.category,
                    isOpen: true
                }
                ]
            }
        });
        setCategories(list);
    }

    return (
        <Stack gap={2}>
            <div>
            </div>
            <div className="item d-flex justify-content-between align-items-start" onClick={() => setShowOrder(!showOrder)}>
                <div className='d-flex justify-content-start align-items-center gap-2'><h6 className='title'>Open Purchase Order</h6><BsChevronDown className='icon' /></div>
                <Button bsPrefix="btn-custom" size="sm" onClick={handleNewOrder} disabled={hasOpenOrder}>New Order</Button>
            </div>
            <div>{renderPurchaseOrder()}</div>
            <div className='section'></div>
            <div className="d-flex justify-content-between align-items-start " onClick={() => setShowSupermarkets(!showSupermarkets)}>
                <div className='d-flex justify-content-start align-items-center gap-2'><h6 className='title'>Items to Purchase</h6><BsChevronDown className='icon' /></div>
                <Button bsPrefix="btn-custom" size="sm" onClick={handleRefresh} disabled={hasOpenOrder}>Refresh</Button>
            </div>
            <div>
                <Collapse in={showSupermarkets} >
                    <div>
                        <FormGroup hidden={isNull(purchaseItems) || purchaseItems.length === 0}>
                            <Select name="supermarket"
                                placeholder="In which order would you like to see the items?"
                                options={supermarkets}
                                onChange={setSupermarketOption} />
                        </FormGroup>
                    </div>
                </Collapse>
            </div>
            <div>
                <Form.Control hidden={isNull(purchaseItems) || purchaseItems.length === 0} size="sm" type="text" id="search" className="form-control mb-1" placeholder="Seacrh for items here" value={searchText} onChange={(e) => filter(e.target.value)} />
                <div className='scroll-purchase'>
                    <Table>
                        <thead >
                            <tr key="0:0" className="align-middle">
                                <th><h6 className="title">Code/Desc.</h6></th>
                                <th><h6 className="title d-none d-md-block ">Pantry</h6></th>
                                <th><h6 className="title">Provis.</h6></th>
                                <th><h6 className="title">Purchased</h6></th>
                            </tr>
                        </thead>
                        {renderPurchaseItems()}
                    </Table>
                </div>
            </div>
            <div>
                <Stack direction="horizontal" gap={2} className="d-flex justify-content-end">
                    <Button bsPrefix="btn-custom" size="sm" onClick={handleClear} disabled={!hasOpenOrder}>Clear</Button>
                    <Button bsPrefix="btn-custom" size="sm" onClick={handleSave} disabled={!hasOpenOrder}>Checkout</Button>
                </Stack >
            </div>

        </Stack >

    )
}