import Form from 'react-bootstrap/Form';
import Select from './Select';
import Col from 'react-bootstrap/Col';

export default function AccountGroupSelect({ accountGroupOption, setAccountGroupOption, accountGroupOptions }) {

    return (
        <>
            <Form.Group as={Col} className="mb-2" controlId="formAccountGroups" size="sm">
                <Form.Label size="sm" className="title mb-1">Account Group</Form.Label>
                <Select name="accountGroup"
                    defaultValue={accountGroupOption}
                    options={accountGroupOptions}
                    onChange={setAccountGroupOption} />
            </Form.Group>
        </>
    )

}