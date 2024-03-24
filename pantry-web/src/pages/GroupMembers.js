import { useState, useEffect, useContext } from 'react';
import { FormCheck, Stack } from "react-bootstrap";
import Button from 'react-bootstrap/Button';
import { AlertContext } from '../services/context/AppContext.js';
import VariantType from '../components/VariantType.js';
import {
    getAccountGroupList, createAccountGroup, editAccountGroup,
    deleteAccountGroup, getAccountGroupMemberList, addAccountMember,
    deleteAccountMember, getRoles
} from '../services/apis/mypantry/requests/AccountRequests.js';
import Table from 'react-bootstrap/Table';
import { BsPencil, BsTrash, BsCheck2All, BsXLg } from "react-icons/bs";
import AccountSearchBar from '../components/AccountSearchBar';
import Form from 'react-bootstrap/Form';
import Modal from 'react-bootstrap/Modal';
import { getAssociatedPantries } from '../services/apis/mypantry/requests/PantryRequests.js'

function GroupMembers() {

    const [refresh, setRefresh] = useState(true);
    const [refreshMembers, setRefreshMembers] = useState(false);
    const [isLoading, setIsLoading] = useState(true);

    const [groups, setGroups] = useState([]);
    const [members, setMembers] = useState([]);
    const [associatedPantries, setAssociatedPantries] = useState([]);

    const [groupName, setGroupName] = useState("");
    const [selectedGroup, setSelectedGroup] = useState();
    const [editGroup, setEditGroup] = useState(0);

    const [showNewGroup, setShowNewGroup] = useState(false);
    const [showModal, setShowModal] = useState(false);

    const { setAlert } = useContext(AlertContext);

    useEffect(() => {
        if (refresh) fetchGroups();
    }, [refresh])

    useEffect(() => {
        if (selectedGroup) fetchMembers();
    }, [selectedGroup])

    useEffect(() => {
        if (refreshMembers) fetchMembers();
    }, [refreshMembers])

    async function fetchGroups() {
        setRefresh(true);
        setIsLoading(true);
        try {
            const res = await getAccountGroupList();
            setGroups(res);
            setRefresh(false);
            setIsLoading(false);
        } catch (error) {
            showAlert(VariantType.DANGER, error.message);
        }
    }

    async function fetchMembers() {
        setRefresh(true);
        setIsLoading(true);
        try {
            const res = await getAccountGroupMemberList(selectedGroup.id);
            setMembers(res);
            setRefresh(false);
            setIsLoading(false);
        } catch (error) {
            showAlert(VariantType.DANGER, error.message);
        }
    }

    function showAlert(type, message) {
        setAlert({
            show: true,
            type: type,
            message: message
        })
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
            await createAccountGroup(group);
            showAlert(VariantType.SUCCESS, "Group updated successfully!");
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
            showAlert(VariantType.SUCCESS, "Group updated successfully!");
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
            showAlert(VariantType.SUCCESS, "Group removed successfully!");
        } catch (error) {
            showAlert(VariantType.DANGER, error.message);
        } finally {
            setRefresh(true);
            setIsLoading(false);
        }
    }

    async function fetchAddMember(groupId, accountMember) {
        try {
            setIsLoading(true);
            setRefreshMembers(false);
            await addAccountMember(groupId, accountMember);
            showAlert(VariantType.SUCCESS, "Member added successfully!");
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
            setRefreshMembers(false);
            await deleteAccountMember(groupId, accountId);
            showAlert(VariantType.SUCCESS, "Member removed successfully!");
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

    function handleAddMember(newMember, role) {
        const accountMember = {
            accountGroupId: selectedGroup.id,
            accountId: newMember.id,
            role: role
        }

        fetchAddMember(selectedGroup.id, accountMember);
    }

    function handleRemoveMember(groupId, accountId) {
        fetchDeleteMember(groupId, accountId);
    }

    function handleEditGroup(item) {
        const editedGroup = {
            ...item,
            name: groupName
        }
        fetchEditGroup(editedGroup);
        setEditGroup(0);
    }

    function renderGroups() {

        if (isLoading)
            return (<span>Loading...</span>)

        return (
            <Table className='bordered'>
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
                    <td colSpan={2}>
                        <Stack direction="horizontal" gap={1} className="d-flex justify-content-start">
                            <div><FormCheck type="radio" checked={true} disabled={true} /></div>
                            <div className='w-50'><Form.Control size="sm" type="text" defaultValue={item.name} onChange={(e) => setGroupName(e.target.value)} /></div>
                            <div><Button onClick={() => handleEditGroup(item, groupName)} variant="link" className='pe-0'><BsCheck2All className='icon' /></Button></div>
                            <div><Button onClick={() => setEditGroup(0)} variant='link' title='Clear text'><BsXLg className='icon' /></Button></div>
                        </Stack >
                    </td >
                    :
                    <>
                        <td><FormCheck type="radio" defaultValue={selectedGroup && selectedGroup.id === item.id}
                            defaultChecked={selectedGroup && selectedGroup.id === item.id}
                            onChange={() => setSelectedGroup(item)} style={{ color: "hsl(219, 11%, 25%)" }}
                            label={item.name} />
                        </td>
                        <td><span>{!item.parentAccountGroup ? "parent" : "child"}</span></td>
                        <td>
                            <Stack direction="horizontal" gap={1} className="d-flex justify-content-end">
                                <div><Button onClick={() => setEditGroup(item.id)} variant="link"><BsPencil className='icon' /></Button></div>
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

        if (isLoading)
            return (<span>Loading...</span>)

        return (
            <Table className='bordered'>
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
                    <span>{item.role.name}</span></td>
                <td>
                    <Stack direction="horizontal" gap={1} className="d-flex justify-content-end">
                        <div><Button onClick={() => handleRemoveMember(item.accountGroupId, item.accountId)} variant="link" disabled={members.length === 1}><BsTrash className='icon' /></Button></div>
                    </Stack>
                </td>
            </tr>
        )
    }

    return (
        <>

            <Stack gap={3}>
                <div></div>
                <div className="d-flex justify-content-between align-items-center">
                    <h6 className='title'>Groups</h6>
                    <Button bsPrefix="btn-custom" size="sm" onClick={() => setShowNewGroup(true)} className="pe-2 ps-2">New Group</Button>
                </div>
                <div>
                    {renderGroups()}
                </div>
                <div>
                    <AccountSearchBar handleSelectAction={handleAddMember} />
                </div>
                <div>
                    <h6 className='title'>Members</h6>
                    {renderMembers()}
                </div>
            </Stack>
            <Modal className='custom-alert' size='sm' show={showModal} onHide={() => setShowModal(false)} >
                <Modal.Body className='custom-alert-body pb-0'>
                    <span className='title text-small'>
                        <b>Group cannot be deleted.</b>
                        <br />
                        There is at least one pantry associated to this group.
                        <br />
                    </span>
                    <ul className='pt-2'>
                        {associatedPantries.map(p => (<li key={p.id} className='text-small'>{p.name}</li>))}
                    </ul>
                </Modal.Body>
                <Modal.Footer className='custom-alert-footer p-2'>
                    <Button bsPrefix='btn-custom' onClick={() => setShowModal(false)}>
                        Close
                    </Button>
                </Modal.Footer>
            </Modal >
        </>
    )
}

export default GroupMembers;