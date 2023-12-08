import Button from 'react-bootstrap/Button';
import Stack from 'react-bootstrap/Stack';
import Row from 'react-bootstrap/Row';
import Col from 'react-bootstrap/Col';
import Form from 'react-bootstrap/Form';

export default function ProductForm({ product, handleSave }) {

    function handleSubmit(e) {
        // Prevent the browser from reloading the page
        e.preventDefault();

        // Read the form data
        const form = e.target;
        const formData = new FormData(form);

        let formJson = Object.fromEntries(formData.entries());

        handleSave(formJson);

        console.log(formJson);
    }


    return (
        <Form key={product} onSubmit={handleSubmit}>
            <Row>
                <Form.Group className="mb-2 w-25" controlId="formId">
                    <Form.Label size="sm">Id</Form.Label>
                    <Form.Control size="sm" type="text" name="id" defaultValue={product.id} disabled />
                </Form.Group>
                <Form.Group as={Col} className="mb-2" controlId="formCode">
                    <Form.Label size="sm">Code</Form.Label>
                    <Form.Control size="sm" type="text" name="code" defaultValue={product.code} />
                </Form.Group>
            </Row>
            <Row>
                <Form.Group className="mb-2 w-25" controlId="formSize">
                    <Form.Label size="sm">Size</Form.Label>
                    <Form.Control size="sm" type="text" name="size" defaultValue={product.size} />
                </Form.Group>
                <Form.Group as={Col} className="mb-2" controlId="formDescription">
                    <Form.Label size="sm">Description</Form.Label>
                    <Form.Control size="sm" type="text" name="description" defaultValue={product.description} />
                </Form.Group>
            </Row>
            <Row>
                <Stack direction="horizontal" gap={2} className="mb-3 d-flex justify-content-end">
                    <div><Button variant="primary" type="reset" size="sm">Clear</Button></div>
                    <div><Button variant="primary" type="submit" size="sm">Save</Button></div>
                </Stack>
            </Row>
        </Form>
    )
}