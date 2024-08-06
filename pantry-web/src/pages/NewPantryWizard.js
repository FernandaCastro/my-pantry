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
import { getAccountGroupList } from '../services/apis/mypantry/requests/AccountRequests.js';

function NewPantryWizard() {

    const { t } = useTranslation(['pantry', 'common']);

    const [pantryForm, setPantryForm] = useState({});
    const [productList, setProductList] = useState([]);
    const [finalProductList, setFinalProductList] = useState([]);
    const [accountGroupOptions, setAccountGroupOptions] = useState([])
    const [selectAll, setSelectAll] = useState(true);
    const [expandAllProducts, setExpandAllProducts] = useState(true);
    const [expandAllReview, setExpandAllReview] = useState(true);

    const [isLoading, setIsLoading] = useState(false);
    const { showAlert } = useAlert();

    useEffect(() => {
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

    const handleComplete = () => {
        console.log("Form completed!");
        console.log("Pantry: ", pantryForm);
        console.log("Products: ", finalProductList);
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
                        setExpandAll={setExpandAllReview} />
                </FormWizard.TabContent>

            </FormWizard>
        </div>
    );
}

export default NewPantryWizard;