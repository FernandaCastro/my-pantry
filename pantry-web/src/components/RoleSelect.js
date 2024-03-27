import { Form, Col } from 'react-bootstrap';
import { useEffect, useState } from 'react';
import Select from './Select';
import { getRoles } from '../services/apis/mypantry/requests/AccountRequests';

export default function RoleSelect({ setSelectedRole }) {

    const [roles, setRoles] = useState([]);
    const [role, setRole] = useState();

    useEffect(() => {
        fetchRoles();
    }, [])

    async function fetchRoles() {
        const res = await getRoles();
        let list = [{}];
        res.map(r => {
            list = [...list,
            {
                value: r.id,
                label: r.name
            }]
        })
        setRoles(list);
    }

    function selectRole(option) {
        setRole(option);
        setSelectedRole({ id: option.value, name: option.label })
    }

    return (
        <>
            <Form.Group as={Col} className="mb-2" controlId="formRole" size="sm">
                <Select name="role" placeholder={"Select a Role"}
                    defaultValue={role}
                    options={roles}
                    onChange={(e) => selectRole(e)} />
            </Form.Group>
        </>
    )

}