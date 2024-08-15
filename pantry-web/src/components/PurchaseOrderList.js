import React, { useState, useEffect, useRef } from 'react';
import { getAllPurchaseOrders } from '../services/apis/mypantry/requests/PurchaseRequests.js';
import VariantType from '../components/VariantType.js';
import useAlert from '../hooks/useAlert.js';
import Table from 'react-bootstrap/Table';
import { useTranslation } from 'react-i18next';

export default function PurchaseOrderList({ selectedPantries, handleSelectedPurchase }) {

    const { t } = useTranslation(['purchase', 'common']);
    const trRef = useRef(null);

    const [purchases, setPurchases] = useState([]);
    const [purchase, setPurchase] = useState();
    const { showAlert } = useAlert();

    useEffect(() => {
        setPurchase();
        handleSelectedPurchase();
        setPurchases([]);
        if (selectedPantries && selectedPantries.length > 0) {
            fetchAllPurchaseOrders();
        }
    }, [selectedPantries]);

    async function fetchAllPurchaseOrders() {
        try {
            const res = await getAllPurchaseOrders(selectedPantries);
            setPurchases(res);
            selectOpenOrder(res);
            return res;
        } catch (error) {
            showAlert(VariantType.DANGER, error.message);
        }
    }

    function renderPurchaseOrders(p) {
        const highlight = (purchase && purchase.id === p.id) ? "highlight" : '';
        return (
            <tr key={p.id} ref={trRef} onClick={(e) => selectPurchase(e, p)} >
                <td className={highlight}><span className={!p.processedAt ? 'highlight-text' : ''}>{p.id} - {p.processedAt ? t("closed") : t("open")}</span></td>
                <td className={highlight}><span className={!p.processedAt ? 'highlight-text text-small' : 'text-small'}>{t('datetime', { ns: "common", date: new Date(p.createdAt) })}</span></td>
                <td className={highlight}><span className='text-small'>{p.processedAt ? t('datetime', { ns: "common", date: new Date(p.processedAt) }) : ""}</span></td>
            </tr>
        )
    }

    const handleClick = () => {
        if (trRef.current) {
            trRef.current.click();
        }
    };


    function selectOpenOrder(list) {
        const openOrders = list?.filter(p => !p.processedAt);
        if (openOrders?.length > 0) {
            const openOrder = openOrders[0];

            setPurchase(openOrder);
            handleSelectedPurchase(openOrder);

            handleClick();
        }
    }

    function selectPurchase(e, p) {
        if (purchase && p.id === purchase.id) {
            setPurchase();
            handleSelectedPurchase();
            return;
        }
        setPurchase(p);
        handleSelectedPurchase(p);
    }

    return (
        <div className="scroll-purchase mt-0 pt-0">
            <Table size='sm'>
                < thead >
                    <tr key="order:0" className="align-middle">
                        <th><h6 className='simple-title'>{t("id-status")}</h6></th>
                        <th><h6 className='simple-title'>{t("createdAt")}</h6></th>
                        <th><h6 className='simple-title'>{t("checkoutAt")}</h6></th>
                    </tr>
                </thead >
                <tbody>
                    {purchases?.map((p) => renderPurchaseOrders(p))}
                </tbody>
            </Table >
        </div >
    )
}