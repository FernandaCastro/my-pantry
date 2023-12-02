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
                <Form.Group as={Col} className="mb-3" controlId="formId">
                    <Form.Label>Id</Form.Label>
                    <Form.Control type="text" name="id" defaultValue={pantry.id} disabled />
                </Form.Group>
                <Form.Group as={Col} className="mb-3" controlId="formName">
                    <Form.Label>Name</Form.Label>
                    <Form.Control type="text" name="name" defaultValue={pantry.name} />
                </Form.Group>
            </Row>
            <Row>
                <Form.Group as={Col} className="mb-3" controlId="formType">
                    <Form.Label>Type</Form.Label>
                    <Form.Select name="type" defaultValue={pantry.type}>
                        <option value='' disabled >Select a type</option>
                        <option value='R'>Recurring</option>
                        <option value='S'>Single</option>
                    </Form.Select>
                </Form.Group>
                <Form.Group as={Col} className="mb-3" controlId="formIsActive">
                    <Form.Label></Form.Label>
                    <Form.Check
                        name="isActive"
                        defaultChecked={pantry.isActive}
                        onClick={e => setIsActiveLabel(e.target.checked ? "Active" : "Inactive")}
                        label={isActiveLabel} />
                </Form.Group>
            </Row>
            <Row>
                <Stack direction="horizontal" gap={2} className="d-flex justify-content-end">
                    <div><Button variant="primary" type="reset">Clear</Button></div>
                    <div><Button variant="primary" type="submit">Save</Button></div>
                </Stack>
            </Row>
        </Form>
    )
}