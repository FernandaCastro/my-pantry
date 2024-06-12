import Form from 'react-bootstrap/Form';
import Table from 'react-bootstrap/Table';
import React, { useState, useEffect } from 'react';
import { getPendingPurchaseItems, getPurchaseItems, getAllSupermarkets } from '../services/apis/mypantry/requests/PurchaseRequests';
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
import OverlayTrigger from 'react-bootstrap/OverlayTrigger';
import Tooltip from 'react-bootstrap/Tooltip';
import { useTranslation } from 'react-i18next';

export default function PurchaseItemList({ purchase, selectedPantries, setOuterPurchaseItems }) {

    const { t } = useTranslation(['purchase', 'common', 'categories']);

    const [purchaseItems, setPurchaseItems] = useState([]);
    const [filteredItems, setFilteredItems] = useState([]);
    const [categories, setCategories] = useState([]);

    const [supermarketOption, setSupermarketOption] = useState({ value: "", label: "", categories: [] });
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
                fetchPendingItems();
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

        if (hasPurchaseItems) {
            purchase && purchase.id > 0 ?
                fetchPurchaseItems() :
                fetchPendingItems();
        }
    }, [supermarketOption.value])

    async function fetchSupermarketOptions() {
        try {
            const res = await getAllSupermarkets();

            var list = [];
            res.forEach(s => {
                list = [...list,
                {
                    value: s.id,
                    label: camelCase(s.name),
                    categories: s.categories
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
            const res = await getPurchaseItems(purchase.id, selectedPantries, supermarketOption.value);

            if (isNull(res) || res.length === 0) {
                setPurchaseItems([]);
                return showAlert(VariantType.INFO, t("purchase-order-empty"));
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

    async function fetchPendingItems() {
        try {
            setIsLoading(true);
            const res = await getPendingPurchaseItems(selectedPantries, supermarketOption.value);

            if (isNull(res) || res.length === 0) {
                return showAlert(VariantType.INFO, t("no-item-to-purchase"));
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
                var found = categories.find(c => c.id == category);

                list = [...list,
                {
                    id: i.product.category,
                    isOpen: found != null ? found.isOpen : true,
                    isSupermarketCategory: supermarketOption.categories.some(c => c === i.product.category)
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
        fetchPendingItems();
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

    function isSupermarketCategory(category) {
        if (supermarketOption.value > 0) {
            var found = categories.find(c => c.id == category);
            if (found) {
                return found.isSupermarketCategory;
            }
            return false;
        }
        return true;
    }

    function renderCategory(category, item) {
        return (
            <>
                <tbody>
                    <tr key={category} className="highlight" >
                        <td className="highlight" colSpan={4}>
                            <div className="category">
                                <Button variant="link" aria-controls={category} onClick={() => handleExpansion(category)}><BsArrow90DegRight className='icon' /></Button>
                                <h6 className='title' style={{ color: !isSupermarketCategory(category) ? "red" : "" }} data-title={!isSupermarketCategory(category) ? t('tooltip-category-not-associated-to-supermarket', { ns: "common" }) : null}>{!category || category === "" ? t("other") : t(category, { ns: 'categories' })}</h6>
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

                        <div id="productDetail" style={{ display: expandProdDetail ? 'block' : 'none' }}>
                            <span hidden={item.productDescription === ''}>
                                {item.product.description}  {item.product.size}
                            </span>
                        </div>

                    </td>
                    <td><span className='d-none d-md-block'>{item.pantryName}</span></td>
                    <td className='text-center'><span className='text-center'>{item.qtyProvisioned}</span></td>
                    <td>
                        <div className='d-flex justify-content-end me-2'>
                            <NumericField object={item} attribute="qtyPurchased" onValueChange={updatePurchasedItem} disabled={!isOpenOrder} />
                        </div>
                    </td>
                </tr>
            </tbody>
        )
    }

    return (
        <>
            <div className="d-flex justify-content-between align-items-center gap-2 pt-2">
                <div style={{ width: '82%' }}>
                    <Select name="supermarket"
                        placeholder={t("placeholder-select-supermarket")}
                        options={supermarkets}
                        onChange={setSupermarketOption}
                    />
                </div>
                <Button bsPrefix="btn-custom" size="sm" onClick={handleRefresh} disabled={purchase}><span className={purchase ? "": "gradient-text"}>{t("btn-refresh")}</span></Button>
            </div>
            <div className="pt-2">
                <Form.Control size="sm" type="text" id="search" className="form-control mb-1" placeholder={t("placeholder-search-items", { ns: "common" })} value={searchText} onChange={(e) => filter(e.target.value)} />
                <div className='scroll-purchaseItems'>
                    <Table size='sm'>
                        <thead >
                            <tr className="align-middle">
                                <th className='d-flex flex-row align-items-center gap-2'>
                                    <OverlayTrigger
                                        placement="top"
                                        delay={{ show: 250, hide: 250 }}
                                        overlay={
                                            <Tooltip className="custom-tooltip">
                                                {t("tooltip-switch-product-detail", { ns: "common" })}
                                            </Tooltip>
                                        }
                                    >
                                        <FormCheck className='form-switch'
                                            defaultChecked={expandProdDetail}
                                            onChange={() => setExpandProdDetail(!expandProdDetail)} />
                                    </OverlayTrigger>
                                    <h6 className="title">{t("code-description", { ns: "common" })}</h6>
                                </th>
                                <th><h6 className="title d-none d-md-block ">{t("pantry", { ns: "common" })}</h6></th>
                                <th className='text-center'><h6 className="title">{t("provisioned", { ns: "common" })}</h6></th>
                                <th className='text-center'><h6 className="title">{t("quantity", { ns: "common" })}</h6></th>
                            </tr>
                        </thead>
                        {renderPurchaseItems()}
                    </Table>
                </div>
            </div>
        </>
    )
}