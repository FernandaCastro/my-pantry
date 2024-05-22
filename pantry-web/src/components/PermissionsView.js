import { useEffect, useState } from 'react';
import { Table } from 'react-bootstrap';
import useAlert from '../hooks/useAlert.js';
import VariantType from '../components/VariantType.js';
import { getRoles } from '../services/apis/mypantry/requests/AccountRequests.js'
import { camelCase, fullCamelCase } from '../services/Utils.js';

export default function PermissionsView() {

    const { showAlert } = useAlert();
    const [roles, setRoles] = useState([]);

    useEffect(() => {
        fetchRoles();
    }, [])

    async function fetchRoles() {
        try {
            const res = await getRoles();
            setRoles(res);
        } catch (error) {
            showAlert(VariantType.DANGER, error.message);
        }
    }

    function renderRoleAndPermissions(role) {
        return (
            <Table key={role.id} size='sm' className='hover-disabled' >
                <thead>
                    <tr key={role.id}><th><span className='title'>{camelCase(role.name)}</span></th></tr>
                </thead>
                <tbody>
                    {role.permissions.map((permission) => { return renderPermission(permission) })}
                </tbody>
            </Table>
        )
    }

    function renderPermission(permission) {
        let str = permission.name.replaceAll("_", " ");
        return (
            <tr key={permission.id}>
                <td><span className="text-small">{fullCamelCase(str)}</span></td>
            </tr>
        )
    }

    return (
        <div className='d-flex align-items-start gap-1'>
            {roles.map((role) => { return renderRoleAndPermissions(role) })}
        </div>

    );

}