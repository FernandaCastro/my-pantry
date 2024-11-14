import React, { useEffect, useState } from 'react';
import { useTranslation } from 'react-i18next';
import FormWizard from "react-form-wizard-component";
import "react-form-wizard-component/dist/style.css";
import iconProduct from '../assets/images/food-gradient.png';
import iconPantry from '../assets/images/cupboard-gradient.png';
import { Image } from 'react-bootstrap';
import { BsCheck2All } from "react-icons/bs";
import ProductSelectionWizard from '../components/NewPantryProductWizard.js';
import NewPantryReviewWizard from '../components/NewPantryReviewWizard.js';
import NewPantryDetailsWizard from '../components/NewPantryDetailsWizard.js';
import VariantType from '../components/VariantType.js';
import useAlert from '../hooks/useAlert.js';
import { useNavigate } from 'react-router-dom';
import { createPantryWizard } from '../api/mypantry/pantry/pantryService.js';
import { fetchAccountGroupList } from '../api/mypantry/account/accountService.js';
import useLoading from '../hooks/useGlobalLoading';

function NewPantryWizard() {

    const { t } = useTranslation(['pantry', 'common']);
    const navigate = useNavigate();

    const [pantryForm, setPantryForm] = useState({});
    const [productList, setProductList] = useState([]);
    const [finalProductList, setFinalProductList] = useState([]);
    const [accountGroupOptions, setAccountGroupOptions] = useState([])
    const [accountGroups, setAccountGroups] = useState([])
    const [selectAll, setSelectAll] = useState(true);
    const [expandAllProducts, setExpandAllProducts] = useState(true);
    const [expandAllReview, setExpandAllReview] = useState(true);
    const [analysePantry, setAnalysePantry] = useState(true);

    const { showAlert } = useAlert();
    const { setIsLoading } = useLoading();

    useEffect(() => {
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

            setAccountGroups(res);
            setAccountGroupOptions(list);
        } catch (error) {
            showAlert(VariantType.DANGER, error.message);
        } finally {
            setIsLoading(false);
        }
    }

    async function fetchCreatePantryWizard(pantryWizardDto) {
        setIsLoading(true);
        try {
            const res = await createPantryWizard(pantryWizardDto);;

            console.log(res);
            showAlert(VariantType.SUCCESS, t('create-pantry-success'));
            navigate('/pantries');
        } catch (error) {
            showAlert(VariantType.DANGER, error.message);
        } finally {
            setIsLoading(false);
        }
    }

    const handleComplete = () => {
        const accountGroup = accountGroups.find(g => g.id === pantryForm.accountGroup.value);

        const pantryWizardDto = {
            accountGroup: accountGroup,
            name: pantryForm.name,
            type: pantryForm.type.value,
            items: finalProductList,
            analysePantry: analysePantry
        }

        console.log("PantryWizardDto: ", pantryWizardDto);
        fetchCreatePantryWizard(pantryWizardDto);
    };

    const checkValidateStep1 = () => {
        if (pantryForm &&
            pantryForm.name && pantryForm.type &&
            pantryForm.size && pantryForm.accountGroup &&
            pantryForm.name.length > 0 &&
            pantryForm.type.value.length > 0 &&
            pantryForm.size.length > 0 &&
            pantryForm.accountGroup.value > 0) {
            return true;
        }
        return false;
    };

    return (
        <div>
            <FormWizard className="new-pantry-wizard"
                shape="circle"
                stepSize="xs"
                onComplete={handleComplete}
                nextButtonText={t('wizard-btn-next')}
                backButtonText={t('wizard-btn-back')}
                finishButtonText={t('wizard-btn-finish')}
            >
                <FormWizard.TabContent title={t('wizard-step-1')} icon={<Image src={iconPantry} className="menu-icon" />}>
                    <NewPantryDetailsWizard accountGroupOptions={accountGroupOptions} pantryForm={pantryForm} setPantryForm={setPantryForm} />
                </FormWizard.TabContent>

                <FormWizard.TabContent title={t('wizard-step-2')} icon={<Image src={iconProduct} className="menu-icon" />}
                    isValid={checkValidateStep1()}
                    validationError={() => showAlert(VariantType.DANGER, t('wizard-step-1-invalid'))}>
                    <ProductSelectionWizard
                        pantrySize={pantryForm.size}
                        productList={productList}
                        setProductList={setProductList}
                        selectAll={selectAll}
                        setSelectAll={setSelectAll}
                        expandAll={expandAllProducts}
                        setExpandAll={setExpandAllProducts} />
                </FormWizard.TabContent>

                <FormWizard.TabContent title={t('wizard-step-3')} icon={<BsCheck2All />}>
                    <NewPantryReviewWizard
                        pantry={pantryForm}
                        productList={productList}
                        setFinalProductList={setFinalProductList}
                        expandAll={expandAllReview}
                        setExpandAll={setExpandAllReview}
                        analysePantry={analysePantry}
                        setAnalysePantry={setAnalysePantry} />
                </FormWizard.TabContent>

            </FormWizard>
        </div>
    );
}

export default NewPantryWizard;