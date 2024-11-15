import Form from 'react-bootstrap/Form';
import Button from 'react-bootstrap/Button';
import { useState } from 'react';
import { BsEraser, BsCheck2All, BsChevronDown, BsSearch, BsPersonPlus } from "react-icons/bs";
import VariantType from './VariantType.js';
import useAlert from '../state/useAlert.js';
import Card from 'react-bootstrap/Card';
import Stack from 'react-bootstrap/Stack';
import CloseButton from 'react-bootstrap/CloseButton';
import Collapse from 'react-bootstrap/Collapse';
import { fetchFilteredAccountList } from '../api/mypantry/account/accountService.js';
import RoleSelect from './RoleSelect';
import AccountForm from './AccountForm';
import { useTranslation } from 'react-i18next';
import { maskEmail } from '../util/utils.js'

function AccountSearchBar({ handleSelectAction, handleClearAction, disabled }) {

    const { t } = useTranslation(['group-members', 'common']);

    const notFound = t("account-not-found");
    const [searchText, setSearchText] = useState("");
    const [results, setResults] = useState([]);
    const [notFoundMessage, setNotFoundMessage] = useState("");
    const [showAccountForm, setShowAccountForm] = useState(false);
    const [show, setShow] = useState(true);
    const { showAlert } = useAlert();

    const [selectedRole, setSelectedRole] = useState();

    function handleSearch() {
        if (searchText.length > 2) loadAccount(searchText);
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

    async function loadAccount(value) {
        try {
            const res = await fetchFilteredAccountList(value);
            setNotFoundMessage(res.length === 0 ? notFound : "");
            setResults(res);
        } catch (error) {
            showAlert(VariantType.DANGER, error.message);
        }
    }


    async function handleSaveSuccess(newAccount) {
        clearSearch();
        setShowAccountForm(false);
        setResults([...results, newAccount]);
    }

    function renderSearchBar() {
        return (
            <Stack direction="horizontal" gap={2} className="w-100">
                <div className="w-75 pe-0">
                    <Form.Control type="text" placeholder={t("search-by-email-name")} value={searchText} onChange={(e) => setSearchText(e.target.value)} disabled={disabled} />
                </div>
                <div>
                    <Button className="w-0 p-0" variant="link" onClick={handleSearch} title={t('tooltip-btn-search')} disabled={!searchText && searchText.length < 3}><BsSearch className='icon' /></Button>
                </div>
                <div>
                    <Button className="w-0 p-0" type="reset" variant="link" onClick={handleClear} title={t('tooltip-btn-clear-search')} disabled={disabled}><BsEraser className='icon' /></Button>
                </div>
                <div>
                    <Button className="w-0 p-0" variant="link" onClick={() => setShowAccountForm(true)} title={t('tooltip-btn-add-account')} disabled={disabled}><BsPersonPlus className='icon' /></Button>
                </div>
            </Stack>
        );
    }

    function renderResults() {
        return (
            <div >
                {results.map((item) => {
                    return (
                        <div key={item.id} className='d-flex justify-content-between align-items-center gap-2'>
                            <div className='d-lg-none d-flex flex-column flex-grow-1 align-items-start'>
                                <span>{item.name}</span>
                                <span>{maskEmail(item.email)}</span>
                            </div>
                            <div className='d-none d-lg-block d-flex flex-column align-items-start'>
                                <span>{item.name} - {maskEmail(item.email)}</span>
                            </div>
                            <div className='d-flex align-items-start gap-2'>
                                <RoleSelect setSelectedRole={setSelectedRole} />
                                <Button onClick={() => handleSelect(item)} variant="link" title={t('tooltip-btn-add-member')} disabled={!selectedRole || selectedRole.id === 0}><BsCheck2All className='icon' /></Button>
                            </div>
                        </div>
                    )
                })}
            </div>
        )
    }

    function renderAccountForm() {
        return (
            <div>
                <div className="me-3 d-flex justify-content-end align-items-center">
                    <CloseButton aria-label="Hide" onClick={() => setShowAccountForm(false)} />
                </div>
                <AccountForm handleSaveSuccess={handleSaveSuccess} />
            </div>
        );
    }

    return (
        <>
            <div onClick={() => setShow(!show)} className="d-flex justify-content-start gap-3 align-items-center">
                <h6 className='title'>{t('add-members-title')}</h6>
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
                            {showAccountForm ? renderAccountForm() : <span />}
                            {results ? renderResults() : <span />}
                        </Card.Body>
                    </Card>

                </Collapse>
            </div>
        </>

    );
}
export default AccountSearchBar;