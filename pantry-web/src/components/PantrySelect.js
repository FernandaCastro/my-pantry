import { useState, useEffect } from 'react';
import { FormCheck } from "react-bootstrap";
import Table from 'react-bootstrap/Table';
import VariantType from '../components/VariantType.js';
import useAlert from '../hooks/useAlert.js';
import { getPantryListWithPermission } from '../services/apis/mypantry/requests/PantryRequests.js';

export default function PantrySelect({ handleSelectedPantryList, permission, isSelected }) {

    const [pantries, setPantries] = useState([]);

    const [selectedPantries, setSelectedPantries] = useState([]);
    const { showAlert } = useAlert();

    useEffect(() => {
        fetchPantries();
    }, [])

    async function fetchPantries() {
        try {
            const res = await getPantryListWithPermission(permission);
            setPantries(res);
            //Load Pantry List but with none selected
            if (isSelected){
                loadSelectedPantries(res); 
            }
        } catch (error) {
            showAlert(VariantType.DANGER, error.message);
        }
    }

    function loadSelectedPantries(pantryList) {
        let list = [];
        pantryList.forEach((p) => { if (p.isActive) list = [...list, p.id] } );

        setSelectedPantries(list);
        handleSelectedPantryList(list);
    }

    function addRemovePantry(checked, item) {
        const list = checked ?
            [...selectedPantries, item.id] :
            selectedPantries.filter((p) => p !== item.id);

        setSelectedPantries(list);
        handleSelectedPantryList(list)
    }

    function renderItem(item) {
        const selectedPantry = selectedPantries.find((p) => p === item.id);
        return (
            <tr key={item.id}>
                <td>
                    <FormCheck
                        disabled={!item.isActive}
                        defaultChecked={selectedPantry}
                        onChange={(e) => addRemovePantry(e.target.checked, item)}
                        label={item.name} />
                </td>
                <td><span className='d-none d-md-block' disabled={!item.isActive}>{!item.accountGroup ? "" : item.accountGroup.name}</span></td>
            </tr>
        )
    }

    return (
        <div className="scroll-selectPantries">
            <Table size='sm'>
                <tbody>
                    {pantries.map((item) => renderItem(item))}
                </tbody>
            </Table >
        </div>
    )
}