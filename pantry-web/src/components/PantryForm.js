import Button from 'react-bootstrap/Button';
import Stack from 'react-bootstrap/Stack';
import Row from 'react-bootstrap/Row';
import Col from 'react-bootstrap/Col';
import Form from 'react-bootstrap/Form';
import { useState } from 'react';

export default function PantryForm({ pantry, handleSave }) {

    const [isActiveLabel, setIsActiveLabel] = useState(pantry.isActive ? "Active" : "Inactive");

    function handleSubmit(e) {
        // Prevent the browser from reloading the page
        e.preventDefault();

        // Read the form data
        const form = e.target;
        const formData = new FormData(form);

        let formJson = Object.fromEntries(formData.entries());
        formJson.isActive = formJson.isActive === 'on' ? true : false;

        handleSave(formJson);

        console.log(formJson);
    }


    return (
        <Form onSubmit={handleSubmit}>
            <Row>
                <Form.Group className="mb-2 w-25" controlId="formId">
                    <Form.Label size="sm">Id</Form.Label>
                    <Form.Control size="sm" type="text" name="id" defaultValue={pantry.id} disabled />
                </Form.Group>
                <Form.Group as={Col} className="mb-2" controlId="formName">
                    <Form.Label size="sm">Name</Form.Label>
                    <Form.Control size="sm" type="text" name="name" defaultValue={pantry.name} />
                </Form.Group>
            </Row>
            <Row>
                <Form.Group className="mb-2 w-25" controlId="formIsActive">
                    <Form.Label size="sm"></Form.Label>
                    <Form.Check size="sm"
                        name="isActive"
                        defaultChecked={pantry.isActive}
                        onClick={e => setIsActiveLabel(e.target.checked ? "Active" : "Inactive")}
                        label={isActiveLabel} />
                </Form.Group>
                <Form.Group as={Col} className="mb-2" controlId="formType">
                    <Form.Label size="sm">Type</Form.Label>
                    <Form.Select size="sm" name="type" defaultValue={pantry.type}>
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