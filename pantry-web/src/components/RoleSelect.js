import { Form, Col } from 'react-bootstrap';
import { useEffect, useState } from 'react';
import Select from './Select';
import { fetchRoles } from '../api/mypantry/account/accountService';
import { useTranslation } from 'react-i18next';

export default function RoleSelect({ setSelectedRole }) {

    const { t } = useTranslation(['group-members']);

    const [roles, setRoles] = useState([]);
    const [role, setRole] = useState();

    useEffect(() => {
        loadRoles();
    }, [])

    async function loadRoles() {
        const res = await fetchRoles();
        let list = [];
        res.map(r => {
            if (r.id.toLowerCase() !== 'owner') {
                list = [...list,
                {
                    value: r.id,
                    label: t(r.id.toLowerCase())
                }]
            }
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
                <Select name="role" placeholder={t("placeholder-select-role")}
                    defaultValue={role}
                    options={roles}
                    onChange={(e) => selectRole(e)} />
            </Form.Group>
        </>
    )

}