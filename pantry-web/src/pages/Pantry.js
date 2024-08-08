import React, { useEffect, useState } from 'react';
import { useParams } from 'react-router';
import { getPantry, updatePantry, createPantry } from '../services/apis/mypantry/requests/PantryRequests.js';
import VariantType from '../components/VariantType.js';
import useAlert from '../hooks/useAlert.js';
import PantryForm from '../components/PantryForm.js';
import Button from 'react-bootstrap/Button';
import { getAccountGroupList } from '../services/apis/mypantry/requests/AccountRequests.js';
import { useTranslation } from 'react-i18next';
import { Image, Stack } from 'react-bootstrap';
import iconPantry from '../assets/images/cupboard-gradient.png';
import { useNavigate } from 'react-router-dom';

export default function Pantry({ mode }) {

    const { t } = useTranslation(['pantry', 'common']);
    const navigate = useNavigate();

    let { id } = useParams();
    const [accountGroupOptions, setAccountGroupOptions] = useState([]);

    const [pantry, setPantry] = useState(
        {
            id: 0,
            name: "",
            type: "R",
            isActive: true,
            accountGroup: { id: 0 }
        });

    const [isLoading, setIsLoading] = useState(false);
    const { showAlert } = useAlert();

    useEffect(() => {
        if (id && mode === 'edit') {
            fetchPantry();
        }

        if (!accountGroupOptions || accountGroupOptions.length === 0) {
            fetchAccountGroups();
        }
    }, [])


    async function fetchAccountGroups() {
        setIsLoading(true);
        try {
            const res = await getAccountGroupList();

            var list = [];
            res.forEach(group => {
                list = [...list,
                {
                    value: group.id,
                    label: group.name
                }]
            });

            setAccountGroupOptions(list);
            setIsLoading(false);
        } catch (error) {
            showAlert(VariantType.DANGER, error.message);
        }
    }

    async function fetchPantry() {
        setIsLoading(true);
        try {
            const res = await getPantry(id);
            setPantry(res);

        } catch (error) {
            showAlert(VariantType.DANGER, error.message);
        } finally {
            setIsLoading(false);
        }
    }

    async function fetchSavePantry(body) {
        setIsLoading(true);
        try {
            const res = mode === 'new' ? await createPantry(body) : await updatePantry(id, body);
            setPantry(res);

            const msg = mode === 'edit' ? t('update-pantry-success') : t('create-pantry-success');
            showAlert(VariantType.SUCCESS, msg);
            navigate('/pantries');

        } catch (error) {
            showAlert(VariantType.DANGER, error.message);
        } finally {
            setIsLoading(false);
        }
    }

    return (
        <Stack gap={4}>
            <div className='d-flex justify-content-start align-items-end mt-4'>
                <Image src={iconPantry} width={40} height={40} className="ms-2 me-3" />
                <h6 className="title">{t('pantry-title')}</h6>
                <Button bsPrefix="btn-custom" href={"/pantries/" + pantry.id + "/items"} className="pe-2 ps-2 ms-auto" disabled={pantry.id === 0}><span>{t('btn-add-pantry-items')}</span></Button>
            </div>
            <div>
                {mode === "edit" && isLoading ?
                    <h6>Loading...</h6> :
                    <PantryForm key={pantry.id} pantry={pantry} handleSave={fetchSavePantry} accountGroupOptions={accountGroupOptions} />}
            </div>
        </Stack >
    );
}