import { useState, useEffect, useContext } from 'react';
import { FormCheck, Stack } from "react-bootstrap";
import Button from 'react-bootstrap/Button';
import { AlertContext } from '../services/context/AppContext.js';
import VariantType from '../components/VariantType.js';
import { getAccountGroupList, createAccountGroup, editAccountGroup, deleteAccountGroup, getAccountGroupMemberList, deleteAccountMember } from '../services/apis/mypantry/requests/AccountRequests.js';
import Table from 'react-bootstrap/Table';
import { BsPencil, BsTrash, BsCheck2All, BsXLg } from "react-icons/bs";
import AccountSearchBar from '../components/AccountSearchBar';
import Form from 'react-bootstrap/Form';

function GroupMembers() {

    const [groups, setGroups] = useState([]);
    const [members, setMembers] = useState([]);
    const [refresh, setRefresh] = useState(true);
    const [isLoading, setIsLoading] = useState(true);
    const [showNewGroup, setShowNewGroup] = useState(false);
    const [groupName, setGroupName] = useState("");
    const [selectedGroup, setSelectedGroup] = useState();
    const [editGroup, setEditGroup] = useState(0);

    const { setAlert } = useContext(AlertContext);

    useEffect(() => {
        if (refresh) fetchGroups();
    }, [refresh])

    useEffect(() => {
        if (selectedGroup) fetchMembers();
    }, [selectedGroup])

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

    async function fetchDeleteMember(groupId, accountId) {
        try {
            setIsLoading(true);
            await deleteAccountMember(groupId, accountId);
            showAlert(VariantType.SUCCESS, "Member removed successfully!");
        } catch (error) {
            showAlert(VariantType.DANGER, error.message);
        } finally {
            setRefresh(true);
            setIsLoading(false);
        }
    }

    function handleNewGroup() {
        const group = { id: 0, name: groupName };
        fetchCreateGroup(group);
        setShowNewGroup(false);
    }

    function handleRemoveGroup(id) {
        fetchDeleteGroup(id);
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
                            checked={selectedGroup && selectedGroup.id === item.id}
                            onClick={() => setSelectedGroup(item)} style={{ color: "hsl(219, 11%, 25%)" }}
                            label={item.name} />
                        </td>
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
                    <span>{item.role}</span></td>
                <td>
                    <Stack direction="horizontal" gap={1} className="d-flex justify-content-end">
                        <div><Button onClick={() => handleRemoveMember(item.groupId, item.accountId)} variant="link" disabled={members.length === 1}><BsTrash className='icon' /></Button></div>
                    </Stack>
                </td>
            </tr>
        )
    }

    return (
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
                <AccountSearchBar />
            </div>
            <div>
                <h6 className='title'>Members</h6>
                {renderMembers()}
            </div>
        </Stack>
    )
}

export default GroupMembers;