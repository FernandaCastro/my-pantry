import { useState, useEffect } from 'react';
import { FormCheck, Stack } from "react-bootstrap";
import Button from 'react-bootstrap/Button';
import VariantType from '../components/VariantType.js';
import {
    getAccountGroupList, createAccountGroup, editAccountGroup,
    deleteAccountGroup, getAccountGroupMemberList, addAccountMember,
    deleteAccountMember
} from '../api/mypantry/account/accountService.js';
import Table from 'react-bootstrap/Table';
import { BsPencil, BsTrash, BsCheck2All, BsXLg } from "react-icons/bs";
import AccountSearchBar from '../components/AccountSearchBar';
import Form from 'react-bootstrap/Form';
import Modal from 'react-bootstrap/Modal';
import { getAssociatedPantries } from '../api/mypantry/pantry/pantryService.js'
import useAlert from '../hooks/useAlert.js';
import PermissionsView from '../components/PermissionsView.js'
import { useTranslation } from 'react-i18next';
import { useGlobalLoading } from '../hooks/useGlobalLoading.js';

function GroupMembers() {

    const { t } = useTranslation(['group-members', 'common']);

    const [refresh, setRefresh] = useState(true);
    const [refreshMembers, setRefreshMembers] = useState(false);

    const [groups, setGroups] = useState([]);
    const [members, setMembers] = useState([]);
    const [associatedPantries, setAssociatedPantries] = useState([]);

    const [groupName, setGroupName] = useState("");
    const [selectedGroup, setSelectedGroup] = useState({ id: 0 });
    const [editGroup, setEditGroup] = useState(0);

    const [showPermissionsView, setShowPermissionsView] = useState(false);
    const [showNewGroup, setShowNewGroup] = useState(false);
    const [showModal, setShowModal] = useState(false);

    const { showAlert } = useAlert();
    const { isLoading, setIsLoading } = useGlobalLoading();

    useEffect(() => {
        if (refresh) fetchGroups();
    }, [refresh])

    useEffect(() => {
        fetchMembers();
    }, [selectedGroup.id, refreshMembers])

    // useEffect(() => {
    //     if (refreshMembers) fetchMembers();
    // }, [refreshMembers])

    async function fetchGroups() {
        setRefresh(true);
        setIsLoading(true);
        try {
            const res = await getAccountGroupList();
            setGroups(res);
            if (res.length > 0 ? setSelectedGroup(res[0]) : "");
            setRefresh(false);
        } catch (error) {
            showAlert(VariantType.DANGER, error.message);
        } finally {
            setIsLoading(false);
        }
    }

    async function fetchMembers() {
        //setIsLoading(true);
        try {
            if (selectedGroup.id > 0) {
                const res = await getAccountGroupMemberList(selectedGroup.id);
                setMembers(res);
            } else {
                setMembers([]);
            }
        } catch (error) {
            showAlert(VariantType.DANGER, error.message);
        } finally {
            //setIsLoading(false);
        }
    }

    async function fetchAssociatedPantries(groupId) {
        try {
            const res = await getAssociatedPantries(groupId);
            setAssociatedPantries(res);
            return res;
        } catch (error) {
            showAlert(VariantType.DANGER, error.message);
        }
    }


    async function fetchCreateGroup(group) {
        try {
            setIsLoading(true);
            const res = await createAccountGroup(group);
            showAlert(VariantType.SUCCESS, t("create-group-success"));
            setSelectedGroup(res);
        } catch (error) {
            showAlert(VariantType.DANGER, error.message);
        } finally {
            setRefresh(true);
            setIsLoading(false);
        }
    }

    async function fetchEditGroup(group) {
        try {
            setIsLoading(true);
            await editAccountGroup(group);
            showAlert(VariantType.SUCCESS, t("update-group-success"));
        } catch (error) {
            showAlert(VariantType.DANGER, error.message);
        } finally {
            setRefresh(true);
            setIsLoading(false);
        }
    }

    async function fetchDeleteGroup(groupId) {
        try {
            setIsLoading(true);
            await deleteAccountGroup(groupId);
            showAlert(VariantType.SUCCESS, t("delete-group-success"));
        } catch (error) {
            showAlert(VariantType.DANGER, error.message);
        } finally {
            setRefresh(true);
            setIsLoading(false);
        }
    }

    async function fetchAddMember(accountMember) {
        try {
            setIsLoading(true);
            setRefreshMembers(!refreshMembers);
            await addAccountMember(accountMember);
            showAlert(VariantType.SUCCESS, t("add-member-success"));
        } catch (error) {
            showAlert(VariantType.DANGER, error.message);
        } finally {
            setRefreshMembers(true);
            setIsLoading(false);
        }
    }

    async function fetchDeleteMember(groupId, accountId) {
        try {
            setIsLoading(true);
            setRefreshMembers(!refreshMembers);
            await deleteAccountMember(groupId, accountId);
            showAlert(VariantType.SUCCESS, t("delete-member-success"));
        } catch (error) {
            showAlert(VariantType.DANGER, error.message);
        } finally {
            setRefreshMembers(true);
            setIsLoading(false);
        }
    }

    function handleNewGroup() {
        const group = { id: 0, name: groupName };
        fetchCreateGroup(group);
        setShowNewGroup(false);
    }

    async function handleRemoveGroup(id) {
        //Show modal to confirm deletion when there's any object associated to the group
        const pantries = await fetchAssociatedPantries(id);
        (pantries && pantries.length > 0) ? setShowModal(true) : fetchDeleteGroup(id);
    }

    function handleAddMember(newMember, selectedRole) {
        const accountMember = {
            accountGroupId: selectedGroup.id,
            accountId: newMember.id,
            role: selectedRole
        }

        fetchAddMember(accountMember);
    }

    function handleRemoveMember(groupId, accountId) {
        fetchDeleteMember(groupId, accountId);
    }

    function handleEditButtonClick(item) {
        setEditGroup(item.id);
        setSelectedGroup(item);
    }

    function handleEditGroupSave(item) {
        const editedGroup = {
            ...item,
            name: groupName
        }
        fetchEditGroup(editedGroup);
        setEditGroup(0);
    }

    function renderGroups() {

        return (
            <Table size='sm' className='bordered'>
                <tbody>
                    {showNewGroup ? renderNewGroup() : <></>}
                    {groups.map((item) => (renderGroup(item)))}
                </tbody>
            </Table>
        )
    }

    function renderGroup(item) {
        return (
            <tr key={item.id} className="align-middle">
                {editGroup === item.id ?
                    <td colSpan={3}>
                        <Stack direction="horizontal" gap={1} className="d-flex justify-content-start">
                            <div><FormCheck type="radio" checked={selectedGroup && selectedGroup.id === item.id}
                                defaultChecked={selectedGroup && selectedGroup.id === item.id}
                                disabled={true} name="groupId" onChange={() => setSelectedGroup(item)} /></div>
                            <div className='w-100'><Form.Control size="sm" type="text" defaultValue={item.name} onChange={(e) => setGroupName(e.target.value)} /></div>
                            <div><Button onClick={() => handleEditGroupSave(item, groupName)} variant="link" className='pe-3'><BsCheck2All className='icon' /></Button></div>
                            <div><Button onClick={() => setEditGroup(0)} variant='link' title='Clear text'><BsXLg className='icon' /></Button></div>
                        </Stack >
                    </td >
                    :
                    <>
                        <td><FormCheck type="radio" defaultValue={selectedGroup && selectedGroup.id === item.id}
                            defaultChecked={selectedGroup && selectedGroup.id === item.id}
                            onChange={() => setSelectedGroup(item)} style={{ color: "hsl(219, 11%, 25%)" }}
                            label={item.name} name="groupId" />
                        </td>
                        <td><span>{!item.parentAccountGroup ? t('parent') : t('child')}</span></td>
                        <td>
                            <Stack direction="horizontal" gap={1} className="d-flex justify-content-end">
                                <div><Button onClick={() => handleEditButtonClick(item)} variant="link"><BsPencil className='icon' /></Button></div>
                                <div><Button onClick={() => handleRemoveGroup(item.id)} variant="link" disabled={groups.length === 1}><BsTrash className='icon' /></Button></div>
                            </Stack>
                        </td>
                    </>
                }
            </tr >
        )
    }

    function renderNewGroup() {
        return (
            <tr key={0}>
                <td colSpan={2}>
                    <Stack direction="horizontal" gap={1} className="d-flex justify-content-start">
                        <div><FormCheck type="radio" checked={false} disabled={true} /></div>
                        <div className='w-50'><Form.Control size="sm" type="text" placeholder='Group Name' defaultValue={groupName} onChange={(e) => setGroupName(e.target.value)} /></div>
                        <div><Button onClick={handleNewGroup} variant="link" className='pe-0' disabled={groupName.length === 0}><BsCheck2All className='icon' /></Button></div>
                        <div><Button onClick={() => setShowNewGroup(false)} variant='link' title='Clear text'><BsXLg className='icon' /></Button></div>
                    </Stack>
                </td>
            </tr>
        )
    }

    function renderMembers() {

        return (
            <Table size='sm' className='bordered' key={selectedGroup?.id}>
                <tbody>
                    {members.map((item) => (renderMember(item)))}
                </tbody>
            </Table>
        )
    }

    function renderMember(item) {
        return (
            <tr key={item.accountId} className="align-middle">
                <td >
                    <span>{item.account.name}</span></td>
                <td >
                    <span>{t(item.role.id.toLowerCase())}</span></td>
                <td>
                    <Stack direction="horizontal" gap={1} className="d-flex justify-content-end">
                        <div><Button onClick={() => handleRemoveMember(item.accountGroupId, item.accountId)} variant="link" disabled={item.role.id === 'OWNER'}><BsTrash className='icon' /></Button></div>
                    </Stack>
                </td>
            </tr>
        )
    }

    return (
        <>
            <Stack gap={4}>
                <div></div>
                <div className="d-flex align-items-center gap-2">
                    <h6 className='title flex-grow-1'>{t("groups-title")}</h6>
                    <Button bsPrefix="btn-custom" size="sm" onClick={() => setShowPermissionsView(!showPermissionsView)} className="pe-2 ps-2"><span className="gradient-text">{t("btn-view-permissions")}</span></Button>
                    <Button bsPrefix="btn-custom" size="sm" onClick={() => setShowNewGroup(true)} className="pe-2 ps-2"><span className="gradient-text">{t("btn-create-group")}</span></Button>
                </div>
                <div>
                    {renderGroups()}
                </div>
                <div>
                    <AccountSearchBar key={selectedGroup.id} handleSelectAction={handleAddMember} disabled={selectedGroup.id === 0} />
                </div>
                <div key={refreshMembers}>
                    <h6 className='title'>{t("members-title")}</h6>
                    {renderMembers()}
                </div>
                <div hidden={!showPermissionsView}>
                    <h6 className='title pb-3'>{t("permissions-title")}</h6>
                    <PermissionsView />
                </div>
            </Stack>
            <Modal className='custom-alert' size='sm' show={showModal} onHide={() => setShowModal(false)} >
                <Modal.Body className='custom-alert-body pb-0'>
                    <span className='title text-small'>
                        <b>{t("delete-group-alert-header")}</b>
                        <br />
                        {t("delete-group-alert-body")}
                        <br />
                    </span>
                    <ul className='pt-2'>
                        {associatedPantries.map(p => (<li key={p.id} className='text-small'>{p.name}</li>))}
                    </ul>
                </Modal.Body>
                <Modal.Footer className='custom-alert-footer p-2'>
                    <Button bsPrefix='btn-custom' size='sm' onClick={() => setShowModal(false)}>
                        {t("close")}
                    </Button>
                </Modal.Footer>
            </Modal >
        </>
    )
}

export default GroupMembers;