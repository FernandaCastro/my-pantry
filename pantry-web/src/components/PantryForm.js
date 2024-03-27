import Button from 'react-bootstrap/Button';
import Stack from 'react-bootstrap/Stack';
import Row from 'react-bootstrap/Row';
import Col from 'react-bootstrap/Col';
import Form from 'react-bootstrap/Form';
import { useState, useEffect, useContext } from 'react';
import Select from './Select';
//import Select from 'react-select';
import { AlertContext } from '../services/context/AppContext.js';

export default function PantryForm({ pantry, handleSave, accountGroupOptions }) {

    const [isActiveLabel, setIsActiveLabel] = useState(pantry.isActive ? "Active" : "Inactive");
    const [accountGroupOption, setAccountGroupOption] = useState({ value: 0, label: "" });
    const { setAlert } = useContext(AlertContext);
    const [isLoading, setIsLoading] = useState(false);

    useEffect(() => {
        if (Object.keys(pantry).length > 0 && pantry.id > 0) {
            const found = accountGroupOptions.find(a => a.value === pantry.accountGroupId);
            setAccountGroupOption(() => found);
        }
    }, [pantry.id]);

    function handleSubmit(e) {
        // Prevent the browser from reloading the page
        e.preventDefault();

        // Read the form data
        const form = e.target;
        const formData = new FormData(form);

        let formJson = Object.fromEntries(formData.entries());
        formJson.isActive = formJson.isActive === 'on' ? true : false;

        formJson = { ...formJson, accountGroupId: accountGroupOption.value }

        handleSave(formJson);

        console.log(formJson);
    }


    return (
        <Form onSubmit={handleSubmit}>
            <Row>
                <Form.Group as={Col} className="mb-2" controlId="formAccountGroups" size="sm">
                    <Form.Label size="sm" className="title mb-1">Account Group</Form.Label>
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
                    <Form.Control size="sm" className="mb-1 input-custom" type="text" name="id" defaultValue={pantry.id} disabled />
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
                    <Form.Select key={pantry.id} size="sm" className="mb-1" name="type" defaultValue={pantry.type}>
                        <option value='' disabled >Select a type</option>
                        <option value='R'>Recurring</option>
                        <option value='S'>Single</option>
                    </Form.Select>
                </Form.Group>
            </Row>
            <Row>
                <Stack direction="horizontal" gap={2} className="mb-3 d-flex justify-content-end">
                    <Button bsPrefix='btn-custom' type="reset" size="sm">Clear</Button>
                    <Button bsPrefix='btn-custom' type="submit" size="sm">Save</Button>
                </Stack>
            </Row>
        </Form>
    )
}