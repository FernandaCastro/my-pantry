import React, { useState, useEffect } from 'react';
import { getAllPurchaseOrders } from '../services/apis/mypantry/requests/PurchaseRequests.js';
import VariantType from '../components/VariantType.js';
import useAlert from '../hooks/useAlert.js';
import Table from 'react-bootstrap/Table';
import Button from 'react-bootstrap/Button';
import { BsXCircle } from 'react-icons/bs';

export default function PurchaseOrderList({ selectedPantries, handleSelectedPurchase }) {

    const [purchases, setPurchases] = useState([]);
    const [purchase, setPurchase] = useState();
    const { showAlert } = useAlert();

    useEffect(() => {
        if (selectedPantries && selectedPantries.length > 0) {
            fetchAllPurchaseOrders();
        } else {
            setPurchase();
            setPurchases([]);
        }
    }, [selectedPantries]);

    async function fetchAllPurchaseOrders() {
        try {
            const res = await getAllPurchaseOrders(selectedPantries);
            setPurchases(res);
            return res;
        } catch (error) {
            showAlert(VariantType.DANGER, error.message);
        }
    }

    function renderPurchaseOrders(p) {
        const highlight = (purchase && purchase.id === p.id) ? "highlight" : '';
        return (
            <tr key={p.id} onClick={(e) => selectPurchase(e, p)} >
                <td className={highlight}><span>{p.id} - {p.processedAt ? 'Closed' : 'Open'}</span></td>
                <td className={highlight}><span className='text-small'>{p.createdAt}</span></td>
                <td className={highlight}><span className='text-small'>{p.processedAt}</span></td>
            </tr>
        )
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
        <div className="scroll-purchase">
            <Table>
                < thead >
                    <tr key="order:0" className="align-middle">
                        <th><h6 className='title'>Id - Status</h6></th>
                        <th><h6 className='title'>Created at</h6></th>
                        <th><h6 className='title'>Checkout at</h6></th>
                    </tr>
                </thead >
                <tbody>
                    {purchases.map((p) => renderPurchaseOrders(p))}
                </tbody>
            </Table >
        </div >
    )
}