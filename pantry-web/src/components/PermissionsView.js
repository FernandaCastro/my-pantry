import { useEffect, useState } from 'react';
import { Table } from 'react-bootstrap';
import useAlert from '../state/useAlert.js';
import VariantType from '../components/VariantType.js';
import { fetchRoles } from '../api/mypantry/account/accountService.js'
import { fullCamelCase } from '../util/Utils.js'
import { useTranslation } from 'react-i18next';

export default function PermissionsView() {

    const { t } = useTranslation(['group-members']);

    const { showAlert } = useAlert();
    const [roles, setRoles] = useState([]);

    useEffect(() => {
        loadRoles();
    }, [])

    async function loadRoles() {
        try {
            const res = await fetchRoles();
            setRoles(res);
        } catch (error) {
            showAlert(VariantType.DANGER, error.message);
        }
    }

    function renderRoleAndPermissions(role) {
        return (
            <Table key={role.id} size='sm' className='hover-disabled' >
                <thead>
                    <tr key={role.id}><th><span className='title'>{t((role.id.toLowerCase()))}</span></th></tr>
                </thead>
                <tbody>
                    {role.permissions.map((permission) => { return renderPermission(permission) })}
                </tbody>
            </Table>
        )
    }

    function renderPermission(permission) {
        let str = permission.id.replaceAll("_", " ");
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