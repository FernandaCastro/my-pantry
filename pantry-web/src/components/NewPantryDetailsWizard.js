import { useEffect, useState } from "react";
import { Form } from "react-bootstrap";
import { useTranslation } from "react-i18next";
import Select from "./Select";

function NewPantryDetailsWizard({ accountGroupOptions, pantryForm, setPantryForm }) {

    const { t } = useTranslation(['pantry', 'common']);

    const [accountGroupOption, setAccountGroupOption] = useState();
    const [typeOption, setTypeOption] = useState();

    const [typeOptions] = useState([
        { value: "R", label: t('type-recurring') },
        { value: "N", label: t('type-no-recurring') },
    ]);

    useEffect(() => {
        if (pantryForm && pantryForm.type) {
            var found = typeOptions.find((i) => i.value === pantryForm.type);
            setTypeOption(found);
        }
        const group = accountGroupOptions[0];
        setAccountGroupOption(() => group);
    }, [])

    function handleOnChangeGroup(e) {
        setAccountGroupOption(e);
        setPantryForm({ ...pantryForm, accountGroup: e });
    }
    function handleOnChangeType(e) {
        setTypeOption(e);
        setPantryForm({ ...pantryForm, type: e });
    }
    function handleOnChangePantrySize(size, e) {
        if (e) {
            setPantryForm({ ...pantryForm, size: size })
        }
    }

    return (
        <div className="d-flex flex-column gap-3">
            <div>
                <Form.Label className="mb-1 title">{t('account-group', { ns: 'common' })}</Form.Label>
                <Select name="accountGroup" key={accountGroupOption?.value}
                    defaultValue={accountGroupOption}
                    options={accountGroupOptions}
                    onChange={handleOnChangeGroup} />
            </div>
            <div>
                <Form.Label className="mb-1 title text-l">{t('name', { ns: 'common' })}</Form.Label>
                <Form.Control type="text" name="name" defaultValue={pantryForm?.name} onChange={(e) => setPantryForm({ ...pantryForm, name: e.target.value })} />
            </div>
            <div>
                <Form.Label className="mb-1 title">{t('type')}</Form.Label>
                <Select name="type" key={typeOption?.value}
                    defaultValue={typeOption}
                    options={typeOptions}
                    onChange={(e) => handleOnChangeType(e)} />
            </div>
            <div className="mt-3">
                <Form.Label className="mb-1 title">{t('wizard-pantry-size')}</Form.Label>
                <Form.Check type="radio" name="group1" id="pantry-size-solo" label={t('wizard-pantry-size-solo')} onChange={(e) => handleOnChangePantrySize("solo", e)} defaultChecked={pantryForm?.size === "solo"} />
                <Form.Check type="radio" name="group1" id="pantry-size-couple" label={t('wizard-pantry-size-couple')} onChange={(e) => handleOnChangePantrySize("couple", e)} defaultChecked={pantryForm?.size === "couple"} />
                <Form.Check type="radio" name="group1" id="pantry-size-fam3" label={t('wizard-pantry-size-fam3')} onChange={(e) => handleOnChangePantrySize("fam3", e)} defaultChecked={pantryForm?.size === "fam3"} />
                <Form.Check type="radio" name="group1" id="pantry-size-fam4" label={t('wizard-pantry-size-fam4')} onChange={(e) => handleOnChangePantrySize("fam4", e)} defaultChecked={pantryForm?.size === "fam4"} />
            </div>
        </div>
    )
}

export default NewPantryDetailsWizard;