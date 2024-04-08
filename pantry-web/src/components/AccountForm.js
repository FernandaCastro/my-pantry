import Form from 'react-bootstrap/Form';
import Button from 'react-bootstrap/Button';
import Stack from 'react-bootstrap/Stack';
import Row from 'react-bootstrap/Row';
import VariantType from './VariantType.js';
import useAlert from '../hooks/useAlert.js';
import { createAccount } from '../services/apis/mypantry/requests/AccountRequests';

export default function AccountForm({ handleSaveSuccess, show }) {

    const { showAlert } = useAlert();

    async function fetchCreateAccount(body) {
        try {
            const res = await createAccount(body);
            showAlert(VariantType.SUCCESS, "Account added successfully.");
            handleSaveSuccess(res);

        } catch (error) {
            showAlert(VariantType.DANGER, error.message);
        }
    }

    function handleSubmit(e) {
        // Prevent the browser from reloading the page
        e.preventDefault();

        // Read the form data
        const form = e.target;
        const formData = new FormData(form);

        let formJson = Object.fromEntries(formData.entries());

        fetchCreateAccount(formJson);

        console.log(formJson);
    }

    return (
        <Form onSubmit={handleSubmit}>
            <Row>
                <Form.Group className="mb-2 w-50" >
                    <Form.Label size="sm" className="mb-1 title">Name</Form.Label>
                    <Form.Control size="sm" type="text" name="name" />
                </Form.Group>
                <Form.Group className="mb-2 w-50" >
                    <Form.Label size="sm" className="mb-1 title">Email</Form.Label>
                    <Form.Control size="sm" type="text" name="email" />
                </Form.Group>
            </Row>
            <Row>
                <Stack direction="horizontal" gap={2} className="mb-3 mt-3 d-flex justify-content-end">
                    <Button bsPrefix='btn-custom' type="reset" size="sm">Clear</Button>
                    <Button bsPrefix='btn-custom' type="submit" size="sm">Save</Button>
                </Stack>
            </Row>
        </Form>
    )
}