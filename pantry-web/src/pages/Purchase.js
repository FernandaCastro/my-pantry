import React, { useContext, useRef, useState } from 'react';
import { postClosePurchaseOrder, postNewPurchaseOrder } from '../api/mypantry/purchase/purchaseService'
import Button from 'react-bootstrap/Button';
import VariantType from '../components/VariantType.js';
import useAlert from '../hooks/useAlert.js';
import { BsChevronDown } from "react-icons/bs";
import Collapse from 'react-bootstrap/Collapse';
import PantrySelect from '../components/PantrySelect.js'
import PurchaseOrderList from '../components/PurchaseOrderList.js'
import PurchaseItemList from '../components/PurchaseItemList.js'
import { useTranslation } from 'react-i18next';
import iconPurchase from '../assets/images/shoppingcart-gradient.png';
import Image from 'react-bootstrap/Image';
import { Stack } from 'react-bootstrap';
import { PurchaseContext } from '../context/AppContext.js';
import { RippleLoading } from '../components/RippleLoading';

export default function Purchase() {

    const { t } = useTranslation(['purchase', 'common']);
    const purchaseItemListRef = useRef();
    const { purchaseCtx, setPurchaseCtx } = useContext(PurchaseContext);

    const [pantries, setPantries] = useState([]);

    const [purchase, setPurchase] = useState();
    const [purchaseItems, setPurchaseItems] = useState([]);

    const [isOpenOrder, setIsOpenOrder] = useState(false);
    const [showPantries, setShowPantries] = useState(true);
    const [showOrder, setShowOrder] = useState(false);

    const [refreshOrders, setRefreshOrders] = useState(false);
    const [isLoading, setIsLoading] = useState(false);

    const { showAlert } = useAlert();

    async function fetchClosePurchaseOrder(order) {
        if (isLoading) return;

        try {
            setIsLoading(true);
            setRefreshOrders(false);

            await postClosePurchaseOrder(order);

            setPurchase();
            setIsOpenOrder(false);
            setRefreshOrders(true);
            removeFromCache(order.id)
            
            showAlert(VariantType.SUCCESS, t("close-purchase-order-success"));
        } catch (error) {
            showAlert(VariantType.DANGER, error.message);
        }finally {
            setIsLoading(false);
        }
    }

    async function fetchNewPurchaseOrder() {
        try {
            setRefreshOrders(false);
            const res = await postNewPurchaseOrder(pantries);
            setPurchase(res);
            setIsOpenOrder(true);
            setRefreshOrders(true);
            showAlert(VariantType.SUCCESS, t("create-purchase-order-success"));
        } catch (error) {
            showAlert(VariantType.DANGER, error.message);
        } finally {
            setShowOrder(true);
        }
    }

    function removeFromCache(orderId) {
        var cache = [...purchaseCtx];
        var index = cache.findIndex(c => c.id === orderId);

        //Remove the order from cache
        if (index > -1) {
            cache.splice(index, 1);
            setPurchaseCtx(cache);
        }
    }

    function handleCloseOrder() {
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

    function selectPurchase(p) {
        setPurchase(p);
        const open = p && !p.processedAt ? true : false;
        setIsOpenOrder(open);
    }

    function handleRefresh() {
        purchaseItemListRef.current?.refreshPendingItens();
        if (!purchaseItems || purchaseItems.length === 0) {
            showAlert(VariantType.INFO, t("no-item-to-purchase"));
        }
    }

    return (
        <>
        {isLoading && <RippleLoading /> }
        <Stack gap={3}>
            <div className='mt-4'>
                <div className='d-flex justify-content-start align-items-center gap-2' onClick={() => setShowPantries(!showPantries)}>
                    <h6 className="text-start fs-6 lh-lg title">{t("purchase-title")}</h6>
                    <BsChevronDown className='small-icon' />
                    <Image src={iconPurchase} width={40} height={40} className='ms-auto me-4 mb-2' style={{ transform: 'rotateY(180deg)' }} />
                </div>

                <Collapse in={showPantries} >
                    <div className='mt-0'><PantrySelect handleSelectedPantryList={setPantries} permission='purchase_pantry' isSelected={false} /></div>
                </Collapse>
            </div>

            <div className='d-flex justify-content-start align-items-center gap-2 mt-3' onClick={() => setShowOrder(!showOrder)}>
                <h6 className='simple-title highlight'>{purchase ? isOpenOrder ? t("purchase-order-open") : t("purchase-order-closed") : t("purchase-order-pending")}</h6>
                <BsChevronDown className='small-icon' />
            </div>

            <div className="pb-0">
                <Collapse in={showOrder} >
                    <div className='mt-0 mb-3'>
                        <PurchaseOrderList key={refreshOrders} selectedPantries={pantries} handleSelectedPurchase={selectPurchase} />
                    </div>
                </Collapse>
            </div>

            <div className="d-flex justify-content-evenly align-items-start mt-0" >
                <Button bsPrefix="btn-custom" size="sm" onClick={handleNewOrder} disabled={((!purchase && purchaseItems.length === 0) || (purchase && purchase.processedAt !== null) || isOpenOrder || pantries.length === 0 || isLoading)}><span>{t("btn-new-order")}</span></Button>
                <Button bsPrefix="btn-custom" size="sm" onClick={handleRefresh} disabled={purchase || pantries.length === 0 || isLoading}><span>{t("btn-refresh")}</span></Button>
                <Button bsPrefix="btn-custom" size="sm" onClick={handleCloseOrder} disabled={isLoading || !isOpenOrder || pantries.length === 0}><span>{t("btn-checkout")}</span></Button>
            </div>

            <div className='mt-2'>
                <PurchaseItemList key={purchase?.id} ref={purchaseItemListRef} selectedPurchase={purchase} selectedPantries={pantries} setOuterPurchaseItems={setPurchaseItems} />
            </div>

        </Stack>
        </>
    )
}