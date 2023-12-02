import React, { useEffect, useState, useContext } from 'react';
import { useParams, useNavigate, redirect } from 'react-router';
import { getPantry, updatePantry, createPantry } from '../../services/apis/mypantry/fetch/requests/PantryRequests.js';
import Stack from 'react-bootstrap/Stack';
import VariantType from '../components/VariantType.js';
import PantryForm from '../components/PantryForm.js';
import { SetAlertContext } from '../../services/context/PantryContext.js';


export default function PantryUpdate() {

    let { id } = useParams();

    const [pantry, setPantry] = useState({});
    const [isLoading, setIsLoading] = useState(true);

    const setAlert = useContext(SetAlertContext);
    const navigate = useNavigate();

    useEffect(() => {
        if (id > 0) fetchPantry();
        setIsLoading(false);
    }, [])

    async function fetchPantry() {
        setIsLoading(true);
        try {
            const res = await getPantry(id);
            setPantry(() => res);
        } catch (error) {
            showAlert(VariantType.DANGER, error.message);
        }
    }

    async function fetchSavePantry(body) {
        setIsLoading(true);
        try {
            const res = id === '0' ? await createPantry(body) : await updatePantry(id, body);
            if (!res) return;
            setPantry(res);
            setIsLoading(false);
        } catch (error) {
            showAlert(VariantType.DANGER, error.message);
        }
    }

    function showAlert(type, message) {
        setAlert({
            show: true,
            type: type,
            message: message
        })
    }

    function handleSave(body) {
        fetchSavePantry(body);
        const message = id === '0' ? "created!" : "updated!"
        showAlert(VariantType.SUCCESS, "Pantry successfully " + message);
        //return navigate("/",);
        return redirect("/pantries/" + body.id);
    }

    return (
        <Stack gap={3}>
            <div>
            </div>
            <div>
                {isLoading ? <h6>Loading...</h6> :
                    <PantryForm pantry={pantry} handleSave={handleSave} />
                }
            </div>
            <div>
            </div>
        </Stack>
    );
}