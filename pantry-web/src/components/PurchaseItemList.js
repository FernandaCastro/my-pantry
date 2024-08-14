import Form from 'react-bootstrap/Form';
import React, { useState, useEffect, forwardRef, useImperativeHandle } from 'react';
import { getPendingPurchaseItems, getPurchaseItems, getAllSupermarkets } from '../services/apis/mypantry/requests/PurchaseRequests';
import Button from 'react-bootstrap/Button';
import Image from 'react-bootstrap/Image';
import food from '../assets/images/healthy-food.png'
import VariantType from '../components/VariantType.js';
import useAlert from '../hooks/useAlert.js';
import Select from '../components/Select.js';
import Collapse from 'react-bootstrap/Collapse';
import { camelCase } from '../services/Utils.js';
import { BsArrow90DegRight } from "react-icons/bs";
import { Card, Col, FormCheck, Row } from "react-bootstrap";
import { useTranslation } from 'react-i18next';
import NumericField from './NumericField';

function PurchaseItemList({ selectedPurchase, selectedPantries, setOuterPurchaseItems }, ref) {

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
    const [showPantryCol, setShowPantryCol] = useState(false);


    useEffect(() => {
        fetchSupermarketOptions();
    }, []);

    useEffect(() => {
        setPurchaseItems([]);

        if (!selectedPurchase && (selectedPantries && selectedPantries.length > 0)) {
            fetchPendingItems();
        }
    }, [selectedPantries])

    useEffect(() => {
        setPurchaseItems([]);
        if (selectedPurchase && Object.keys(selectedPurchase).length > 0) {
            (!selectedPurchase.processedAt) ? setIsOpenOrder(true) : setIsOpenOrder(false);
            fetchPurchaseItems();
        } else {
            if (selectedPantries && selectedPantries.length > 0) { fetchPendingItems() };
        }
    }, [selectedPurchase])

    useEffect(() => {
        if (purchaseItems) {
            filter(searchText);
            populateCategories();
        }
        setOuterPurchaseItems(purchaseItems);
    }, [purchaseItems])

    //Sort items based on Supermarket categories order
    useEffect(() => {
        const hasPurchaseItems = purchaseItems && purchaseItems.length > 0;

        if (hasPurchaseItems) {
            selectedPurchase && selectedPurchase.id > 0 ?
                fetchPurchaseItems() :
                fetchPendingItems();
        }
    }, [supermarketOption.value])

    //This method can be called from parent component (refresh button)
    useImperativeHandle(ref, () => ({
        refreshPendingItens() {
            fetchPendingItems();
        },
    }));

    async function fetchPendingItems() {
        try {
            setIsLoading(true);
            const res = await getPendingPurchaseItems(selectedPantries, supermarketOption.value);

            if (isNull(res) || res.length === 0) {
                setPurchaseItems([]);
                //return showAlert(VariantType.INFO, t("no-item-to-purchase"));
            }

            setPurchaseItems(res);
            setIsLoading(false);
        } catch (error) {
            showAlert(VariantType.DANGER, error.message);
        } finally {
            setIsLoading(false);
        }
    }

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
            const res = await getPurchaseItems(selectedPurchase.id, selectedPantries, supermarketOption.value);

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

    function renderCards() {
        var elements = [];
        var index = 0;
        var found = filteredItems.at(index);
        var category = found ? found.product.category : "";

        while (category !== "" && index < filteredItems.length) {
            var filteredCategory = filteredItems.filter(i => i.product.category === category);

            elements.push(renderCategoryCard(category, filteredCategory))

            index = index + filteredCategory.length;
            found = filteredItems.at(index);
            category = found ? found.product.category : "";
        }
        return elements;
    }

    function renderCategoryCard(category, filteredCategory) {
        return (
            <div className="flex-column pt-2 pb-2">
                <div className="category" onClick={() => handleExpansion(category)}>
                    <Button variant="link" aria-controls={category} onClick={() => handleExpansion(category)}><BsArrow90DegRight className='small-icon' /></Button>
                    <h6 className='title' style={{ color: !isSupermarketCategory(category) ? "red" : "" }} data-title={!isSupermarketCategory(category) ? t('tooltip-category-not-associated-to-supermarket', { ns: "common" }) : null}>{!category || category === "" ? t("other") : t(category, { ns: 'categories' })}</h6>
                </div>
                <Collapse in={getOpen(category)}>
                    <Row xs={1} md={2} lg={3} xl={4} className='m-0'>
                        {filteredCategory?.map(item => renderItemCard(item))}
                    </Row>
                </Collapse>
            </div>
        )
    }

    function renderItemCard(item) {
        return (

            <Col key={item.id} className="d-flex flex-column g-2">
                <Card className="card1 flex-fill">
                    <Card.Body className="d-flex  flex-column h-100">

                        <div className="d-flex justify-content-between" >
                            <div className='d-flex gap-2'>
                                <Image src={food} width={20} height={20} rounded />
                                <Card.Title as="h6" className='mb-0'><span className={item.qtyProvisioned === 0 ? "text-wrap removed" : "text-wrap"}>{camelCase(item.product.code)}</span></Card.Title>
                            </div>
                            <NumericField object={item} attribute="qtyPurchased" onValueChange={updatePurchasedItem} disabled={!isOpenOrder} />
                        </div>

                        <div className="d-flex justify-content-between " >
                            <div className='d-flex flex-column'>
                                <span className="mt-0 small" hidden={!expandProdDetail}>
                                    {item.product.description} {item.product.size}
                                </span>
                                <span className='text-wrap small' hidden={!showPantryCol}>{item.pantryName}</span>
                            </div>
                        </div>

                        <div className="d-flex gap-3 mt-auto">
                            <span className={item.qtyProvisioned === 0 ? "small removed" : "small"}>{t('provisioned')}: {item.qtyProvisioned}</span>
                        </div>

                    </Card.Body>
                </Card>
            </Col>

        )
    }

    return (
        <div>
            {selectedPantries?.length === 0 ? "" : isLoading ? "Loading..." :
                <div className="pt-2">
                    <div className='d-flex justify-content-evenly pb-2'>
                        <FormCheck label={t('tooltip-switch-product-detail', { ns: 'common' })}
                            className='form-switch'
                            defaultChecked={expandProdDetail}
                            onChange={() => setExpandProdDetail(!expandProdDetail)} />

                        <FormCheck label={t('tooltip-switch-pantry', { ns: 'common' })}
                            className='d-block form-switch'
                            defaultChecked={showPantryCol}
                            onChange={() => setShowPantryCol(!showPantryCol)}
                        />
                    </div>
                    <div style={{ width: '100%' }} className="pb-2">
                        <Select name="supermarket"
                            placeholder={t("placeholder-select-supermarket")}
                            options={supermarkets}
                            onChange={setSupermarketOption}
                        />
                    </div>
                    <Form.Control size="sm" type="text" id="search" className="form-control mb-1" placeholder={t("placeholder-search-items", { ns: "common" })} value={searchText} onChange={(e) => filter(e.target.value)} />
                    {renderCards()}
                </div>
            }
        </div>
    )
}

export default forwardRef(PurchaseItemList);