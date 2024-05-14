import Form from 'react-bootstrap/Form';
import Table from 'react-bootstrap/Table';
import React, { useState, useEffect } from 'react';
import { getPendingPurchaseItems, getPurchaseItems, getAllProperty } from '../services/apis/mypantry/requests/PurchaseRequests';
import Button from 'react-bootstrap/Button';
import Stack from 'react-bootstrap/Stack';
import Image from 'react-bootstrap/Image';
import food from '../assets/images/healthy-food.png'
import VariantType from '../components/VariantType.js';
import useAlert from '../hooks/useAlert.js';
import NumericField from '../components/NumericField.js'
import Select from '../components/Select.js';
import Collapse from 'react-bootstrap/Collapse';
import { camelCase } from '../services/Utils.js';
import { BsArrow90DegRight } from "react-icons/bs";
import { FormCheck } from "react-bootstrap";

export default function PurchaseItemList({ purchase, selectedPantries, setOuterPurchaseItems }) {

    const [purchaseItems, setPurchaseItems] = useState([]);
    const [filteredItems, setFilteredItems] = useState([]);
    const [categories, setCategories] = useState([]);

    const [supermarketOption, setSupermarketOption] = useState({ value: "", label: "" });
    const [supermarkets, setSupermarkets] = useState([]);

    const [searchText, setSearchText] = useState("");
    const { showAlert } = useAlert();
    const [isLoading, setIsLoading] = useState(true);
    const [isOpenOrder, setIsOpenOrder] = useState(false);

    const [expandProdDetail, setExpandProdDetail] = useState(false);


    useEffect(() => {
        fetchSupermarketOptions();
    }, []);

    useEffect(() => {
        if (!selectedPantries || selectedPantries.length === 0) {
            setPurchaseItems([]);
        }
        else {
            if (!purchase) {
                fetchPendingPurchaseItems();
            }
        }
    }, [selectedPantries])

    useEffect(() => {
        if (purchase && Object.keys(purchase).length > 0) {
            (!purchase.processedAt) ? setIsOpenOrder(true) : setIsOpenOrder(false);
            fetchPurchaseItems();
        } else {
            setPurchaseItems([]);
        }
    }, [purchase])

    useEffect(() => {
        if (purchaseItems) {
            filter(searchText);
            populateCategories();
        }
        setOuterPurchaseItems(purchaseItems);
    }, [purchaseItems])

    useEffect(() => {
        const hasPurchaseItems = purchaseItems && purchaseItems.length > 0;
        const shouldSort = supermarketOption.value.length > 0;

        if (hasPurchaseItems && shouldSort) {
            hasPurchaseItems ?
                fetchPurchaseItems() :
                fetchPendingPurchaseItems();
        }
    }, [supermarketOption])

    async function fetchSupermarketOptions() {
        try {
            const res = await getAllProperty("%25.supermarket.categories");

            var list = [{ value: " ", label: "Alphabetically" }];
            res.forEach(s => {
                var name = s.propertyKey.substring(0, s.propertyKey.indexOf("."));

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

    async function fetchPurchaseItems(clear) {
        try {
            setIsLoading(true);
            const res = await getPurchaseItems(purchase.id, supermarketOption.value);

            if (isNull(res) || res.length === 0) {
                setPurchaseItems([]);
                return showAlert(VariantType.INFO, "This Shopping List is empty.");
            }

            //keep the already entered qtyPurchased when sorting by supermarket
            if (clear) {
                setPurchaseItems(res);

            } else {

                (Object.keys(purchaseItems).length > 0) ?
                    keepPurchasedQty(res) : setPurchaseItems(res);
            }

            setIsLoading(false);
        } catch (error) {
            showAlert(VariantType.DANGER, error.message);
        } finally {
            setIsLoading(false);
        }
    }

    async function fetchPendingPurchaseItems() {
        try {
            setIsLoading(true);
            const res = await getPendingPurchaseItems(selectedPantries, supermarketOption.value);

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

    function keepPurchasedQty(items) {
        const updatedList = items.map(i => {

            var found = purchaseItems.find((p) => p.id === i.id);
            return (found) ?
                i = { ...i, qtyPurchased: found.qtyPurchased } : i;
        });
        setPurchaseItems(updatedList);
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

    function handleExpansion(id) {

        var newList = categories.map((c) => {
            return (c.id === id) ?
                c = { ...c, isOpen: !c.isOpen } : c;
        });

        setCategories(newList);
    }

    function isNull(object) {
        if (!object || (Object.keys(object).length === 0 && object.constructor === Object)) return true;
        return false;
    }

    function handleRefresh() {
        fetchPendingPurchaseItems();
    }

    function renderPurchaseItems() {
        let category = "";

        return (filteredItems.map((item) => {
            if (category === item.product.category) {
                return renderCategoryItem(category, item);
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
                    {renderItem(item)}
                </Collapse>
            </>
        )
    }

    function renderCategoryItem(category, item) {
        return (
            <Collapse in={getOpen(category)}>
                {renderItem(item)}
            </Collapse>
        )
    }

    function renderItem(item) {
        return (
            <tbody>
                <tr key={item.id} >
                    <td>
                        <Stack direction="horizontal" gap={2}>
                            <div><Image src={food} width={20} height={20} rounded /></div>
                            <div><span>{camelCase(item.product.code)}</span></div>
                        </Stack>

                        <div id="productDetail" style={{display: expandProdDetail ? 'block' : 'none'}}>
                            <span className='d-none d-md-block' hidden={item.productDescription === ''}>
                                {item.product.description}  {item.product.size}
                            </span>
                        </div>

                    </td>
                    <td><span className='d-none d-md-block'>{item.pantryName}</span></td>
                    <td><span>{item.qtyProvisioned}</span></td>
                    <td><NumericField object={item} attribute="qtyPurchased" onValueChange={updatePurchasedItem} disabled={!isOpenOrder} /></td>
                </tr>
            </tbody>
        )
    }

    return (
        <>
            <div className="d-flex justify-content-between align-items-center gap-2 pt-2">
                <Select name="supermarket"
                    placeholder="Sort by Supermarket?"
                    options={supermarkets}
                    onChange={setSupermarketOption}
                />
                <Button bsPrefix="btn-custom" size="sm" onClick={handleRefresh} disabled={purchase}>Refresh</Button>
            </div>
            <div className="pt-2">
                <Form.Control size="sm" type="text" id="search" className="form-control mb-1" placeholder="Search for items here" value={searchText} onChange={(e) => filter(e.target.value)} />
                <div className='scroll-purchaseItems'>
                    <Table size='sm'>
                        <thead >
                            <tr className="align-middle">
                                <th className='d-flex flex-row align-items-center gap-2'>
                                    <FormCheck
                                        className='d-none d-md-block'
                                        defaultChecked={expandProdDetail}
                                        onChange={() => setExpandProdDetail(!expandProdDetail)} />
                                    <h6 className="title">Code/Desc.</h6>
                                </th>
                                <th><h6 className="title d-none d-md-block ">Pantry</h6></th>
                                <th><h6 className="title">Provis.</h6></th>
                                <th><h6 className="title">Purchased</h6></th>
                            </tr>
                        </thead>
                        {renderPurchaseItems()}
                    </Table>
                </div>
            </div>
        </>
    )
}