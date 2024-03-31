import Form from 'react-bootstrap/Form';
import Button from 'react-bootstrap/Button';
import { useState, useContext } from 'react';
import { BsEraser, BsCheck2All, BsChevronDown, BsPlusLg, BsSearch } from "react-icons/bs";
import VariantType from './VariantType.js';
import { AlertContext } from '../services/context/AppContext.js';
import Card from 'react-bootstrap/Card';
import Table from 'react-bootstrap/Table';
import Stack from 'react-bootstrap/Stack';
import CloseButton from 'react-bootstrap/CloseButton';
import Collapse from 'react-bootstrap/Collapse';
import { getFilteredAccountList, createAccount, } from '../services/apis/mypantry/requests/AccountRequests.js';
import RoleSelect from './RoleSelect';
import AccountForm from './AccountForm';

function AccountSearchBar({ handleSelectAction, handleClearAction, disabled }) {

    const notFound = "Account not found";
    const [searchText, setSearchText] = useState("");
    const [results, setResults] = useState([]);
    const [notFoundMessage, setNotFoundMessage] = useState("");
    const [showAccountForm, setShowAccountForm] = useState(false);
    const [show, setShow] = useState(true);
    const { setAlert } = useContext(AlertContext);

    const [selectedRole, setSelectedRole] = useState();
    const [account, setAccount] = useState({});

    function handleSearch() {
        if (searchText.length > 2) fetchAccount(searchText);
    }

    function clearSearch() {
        setSearchText(() => "");
        setResults([]);
        setNotFoundMessage("");
    }
    function handleClear() {
        clearSearch();
        if (handleClearAction) handleClearAction();
    }
    function handleSelect(item) {
        handleSelectAction(item, selectedRole);
        clearSearch();
    }

    async function fetchAccount(value) {
        try {
            const res = await getFilteredAccountList(value);
            setNotFoundMessage(res.length === 0 ? notFound : "");
            setResults(res);
        } catch (error) {
            setAlert({
                show: true,
                type: VariantType.DANGER,
                message: error.message
            });
        }
    }

    function renderSearchBar() {
        return (
            <Stack direction="horizontal" gap={2} className="w-100">
                <div className="w-75 pe-0">
                    <Form.Control size="sm" type="text" placeholder='Search by e-mail or name' value={searchText} onChange={(e) => setSearchText(e.target.value)} disabled={disabled}/>
                </div>
                <div>
                    <Button className="w-0 p-0" variant="link" onClick={handleSearch} title='Search' disabled={!searchText && searchText.length < 3}><BsSearch className='icon' /></Button>
                </div>
                <div>
                    <Button className="w-0 p-0" type="reset" variant="link" onClick={handleClear} title='Clear search text' disabled={disabled}><BsEraser className='icon' /></Button>
                </div>
                <div>
                    <Button className="w-0 p-0" variant="link" onClick={() => setShowAccountForm(true)} title='Pre-register an account.' disabled={disabled}><BsPlusLg className='icon' /></Button>
                </div>
            </Stack>
        );
    }

    function renderResults() {
        return (
            results.map((item) => {
                return (
                    <tr key={item.id} className="w-0 p-0 colorfy">
                        <td className="w-0 p-0 border-end-0 colorfy">
                            <span>{item.name} {item.email === "" ? "" : ' - ' + item.email}</span></td>
                        <td className="w-0 p-0 colorfy">
                            {/* <Form.Check size="sm" className="mb-1 title"
                                onClick={e => item = { ...item, groupRole: (e.target.checked ? "ADMIN" : "USER") }}
                                label="as Admin" /> */}
                            <RoleSelect setSelectedRole={setSelectedRole} />
                        </td>
                        <td className="w-0 p-0 colorfy">
                            <Button onClick={() => handleSelect(item)} variant="link" title='Add Member to the group'><BsCheck2All className='icon' /></Button>
                        </td>
                    </tr>

                );
            })
        );
    }

    function showAlert(type, message) {
        setAlert({
            show: true,
            type: type,
            message: message
        })
    }


    async function handleSaveSuccess(newAccount) {
        clearSearch();
        setShowAccountForm(false);
        setResults([...results, newAccount]);
    }

    function renderAccountForm() {
        if (showAccountForm) {
            return (
                <div>
                    <div className="me-3 d-flex justify-content-end align-items-center">
                        <CloseButton aria-label="Hide" onClick={() => setShowAccountForm(false)} />
                    </div>
                    <AccountForm handleSaveSuccess={handleSaveSuccess} />
                </div>
            );
        }
    }

    return (
        <>
            <div onClick={() => setShow(!show)} className="d-flex justify-content-start gap-3 align-items-center">
                <h6 className='title'> Add Members to the Group</h6>
                <BsChevronDown className='icon' />
            </div>
            <div className='custom-card'>
                <Collapse in={show} >
                    <Card>
                        <Card.Header className="m-0 p-2 d-flex justify-content-between">
                            {renderSearchBar()}
                        </Card.Header>
                        <Card.Body className="m-0 p-2">
                            <span style={{ color: 'red', fontSize: '11px' }}>{notFoundMessage}</span>
                            {renderAccountForm()}
                            {results ? (
                                <Table hover>
                                    <tbody>
                                        {renderResults()}
                                    </tbody>
                                </Table>)
                                : <span />
                            }
                        </Card.Body>
                    </Card>

                </Collapse>
            </div>
        </>

    );
}
export default AccountSearchBar;