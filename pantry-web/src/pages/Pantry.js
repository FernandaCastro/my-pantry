import React, { useEffect, useState } from 'react';
import { useParams } from 'react-router';
import { updatePantry, createPantry, fetchPantry } from '../api/mypantry/pantry/pantryService.js';
import VariantType from '../components/VariantType.js';
import useAlert from '../hooks/useAlert.js';
import PantryForm from '../components/PantryForm.js';
import Button from 'react-bootstrap/Button';
import { fetchAccountGroupList } from '../api/mypantry/account/accountService.js';
import { useTranslation } from 'react-i18next';
import { Image, Stack } from 'react-bootstrap';
import iconPantry from '../assets/images/cupboard-gradient.png';
import { useNavigate } from 'react-router-dom';
import { useGlobalLoading } from '../hooks/useGlobalLoading.js';

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

    const { showAlert } = useAlert();
    const { setIsLoading } = useGlobalLoading();

    useEffect(() => {
        if (id && mode === 'edit') {
            getPantry();
        }

        if (!accountGroupOptions || accountGroupOptions.length === 0) {
            loadAccountGroups();
        }
    }, [])


    async function loadAccountGroups() {
        setIsLoading(true);
        try {
            const res = await fetchAccountGroupList();

            var list = [];
            res.forEach(group => {
                list = [...list,
                {
                    value: group.id,
                    label: group.name
                }]
            });

            setAccountGroupOptions(list);
        } catch (error) {
            showAlert(VariantType.DANGER, error.message);
        } finally {
            setIsLoading(false);
        }
    }

    async function getPantry() {
        setIsLoading(true);
        try {
            const res = await fetchPantry(id);
            setPantry(res);

        } catch (error) {
            showAlert(VariantType.DANGER, error.message);
            if (error.status === 403) {
                navigate('/pantries');
            }
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
                <PantryForm key={pantry.id + accountGroupOptions.length} pantry={pantry} handleSave={fetchSavePantry} accountGroupOptions={accountGroupOptions} />
            </div>
        </Stack >
    );
}