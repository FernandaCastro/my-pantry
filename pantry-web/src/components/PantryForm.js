import Button from 'react-bootstrap/Button';
import Stack from 'react-bootstrap/Stack';
import Row from 'react-bootstrap/Row';
import Col from 'react-bootstrap/Col';
import Form from 'react-bootstrap/Form';
import { useState, useEffect } from 'react';
import Select from './Select';
//import Select from 'react-select';

export default function PantryForm({ pantry, handleSave, accountGroupOptions }) {

    const [isActiveLabel, setIsActiveLabel] = useState(pantry.isActive ? "Active" : "Inactive");
    const [accountGroupOption, setAccountGroupOption] = useState({ value: 0, label: "" });
    const [isLoading, setIsLoading] = useState(false);
    const [isProcessing, setIsProcessing] = useState(false);
    const [id, setId] = useState(pantry.id);
    const [typeOptions, setTypeOptions] = useState([
        { value: "R", label: "Recurring" },
        { value: "N", label: "No Recurring" },
    ]);
    const [typeOption, setTypeOption] = useState({ value: "", label: "" });

    useEffect(() => {
        if (pantry && pantry.id > 0) {
            if (accountGroupOptions && accountGroupOptions.length > 0) {
                const group = accountGroupOptions.find(a => a.value === pantry.accountGroup.id);
                setAccountGroupOption(() => group);
            }
            if (typeOptions && typeOptions.length > 0) {
                const type = typeOptions.find(a => a.value === pantry.type);
                setTypeOption(() => type);
            }
        }

    }, [pantry.id]);

    useEffect(() => {
        if (pantry && pantry.id === 0 && accountGroupOptions && accountGroupOptions.length > 0) {
            const group = accountGroupOptions[0];
            setAccountGroupOption(() => group);
        }
    }, [accountGroupOptions])

    useEffect(() => {
        if (pantry && pantry.id === 0 && typeOptions && typeOptions.length > 0) {
            const type = typeOptions[0];
            setTypeOption(() => type);
        }
    }, [])

    async function handleSubmit(e) {
        if (!isProcessing) {
            setIsProcessing(true);

            // Prevent the browser from reloading the page
            e.preventDefault();

            // Read the form data
            const form = e.target;
            const formData = new FormData(form);

            let formJson = Object.fromEntries(formData.entries());
            formJson.id = id;
            formJson.isActive = formJson.isActive === 'on' ? true : false;
            formJson.type = typeOption.value;

            const accountGroup = { id: accountGroupOption.value };
            formJson = { ...formJson, accountGroup: accountGroup }

            await handleSave(formJson);

            setIsProcessing(false);
        }
    }


    return (
        <Form onSubmit={handleSubmit}>
            <Row>
                <Form.Group className="mb-2" controlId="formAccountGroups" size="sm">
                    {isLoading ? <span>Loading...</span> :
                        <Select name="accountGroup" key={accountGroupOption.value}
                            defaultValue={accountGroupOption}
                            options={accountGroupOptions}
                            onChange={setAccountGroupOption} />
                    }
                </Form.Group>
            </Row>
            <Row>
                <Form.Group className="mb-2 w-25" controlId="formId">
                    <Form.Label size="sm" className="mb-1 title">Id</Form.Label>
                    <Form.Control size="sm" className="mb-1 input-custom" type="text" name="id" defaultValue={id} disabled />
                </Form.Group>
                <Form.Group as={Col} className="mb-2" controlId="formName">
                    <Form.Label size="sm" className="mb-1 title">Name</Form.Label>
                    <Form.Control size="sm" className="mb-1 input-custom" type="text" name="name" defaultValue={pantry.name} />
                </Form.Group>
            </Row>
            <Row>
                <Form.Group className="mb-2 w-25" controlId="formIsActive">
                    <Form.Label size="sm" className="mb-1"></Form.Label>
                    <Form.Check size="sm" className="mb-1 title"
                        name="isActive"
                        defaultChecked={pantry.isActive}
                        onClick={e => setIsActiveLabel(e.target.checked ? "Active" : "Inactive")}
                        label={isActiveLabel} />
                </Form.Group>
                <Form.Group as={Col} className="mb-2" controlId="formType">
                    <Form.Label size="sm" className="mb-1 title">Type</Form.Label>
                    <Select name="type" key={typeOption.value}
                        defaultValue={typeOption}
                        options={typeOptions}
                        onChange={setTypeOption} />
                </Form.Group>
            </Row>
            <Row>
                <Stack direction="horizontal" gap={2} className="mb-3 d-flex justify-content-end">
                    <Button bsPrefix='btn-custom' type="reset" size="sm" disabled={isProcessing}>Clear</Button>
                    <Button bsPrefix='btn-custom' type="submit" size="sm" disabled={isProcessing}>Save</Button>
                </Stack>
            </Row>
        </Form>
    )
}