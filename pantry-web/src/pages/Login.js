import { ProfileContext } from '../context/AppContext';
import React, { useContext, useState } from 'react';
import { Button } from 'react-bootstrap';
import { LoginWithGoogle } from '../components/LoginWithGoogle.js';
import { login } from '../api/mypantry/account/loginService';
import { useNavigate } from 'react-router-dom';
import Form from 'react-bootstrap/Form';
import VariantType from '../components/VariantType.js';
import useAlert from '../hooks/useAlert.js';
import { useTranslation } from 'react-i18next';
import useEncrypt from '../hooks/useRSAEncrypt';
import PasswordInput from '../components/PasswordInput';

export default function Login() {

    const { t } = useTranslation(['login', 'common']);

    const navigate = useNavigate();
    const { profileCtx, setProfileCtx } = useContext(ProfileContext);
    const { showAlert } = useAlert();
    const { encrypt } = useEncrypt();

    const [account, setAccount] = useState({
        name: "",
        email: "",
        password: "",
        confirmPassword: "",
        resetQuestion: "",
        resetAnswer: ""
    });
    const [isProcessing, setIsProcessing] = useState(false);

    const handlePostLogin = (profile) => {
        profile = {
            id: profile.id,
            name: profile.name,
            email: profile.email,
            pictureUrl: profile.pictureUrl,
            initials: getInitialsAttribute(profile.name),
            theme: profileCtx?.theme
        }
        setProfileCtx(profile);
        navigate('/');
    }

    async function handleSubmitLogin(e) {
        if (!isProcessing) {
            // Prevent the browser from reloading the page
            e.preventDefault();
            setIsProcessing(true);

            // Read the form data
            const form = e.target;
            const formData = new FormData(form);

            let formJson = Object.fromEntries(formData.entries());

            const encryptedPassword = encrypt.encrypt(formJson.password);

            formJson = {
                ...formJson,
                password: encryptedPassword
            }

            await handleLogin(formJson);
            setIsProcessing(false);
        }
    }

    function clearAccount() {
        setAccount({
            ...account,
            email: "",
            password: ""
        })
    }

    async function handleLogin(loginData) {
        try {
            var profile = await login(loginData);
            handlePostLogin(profile);
        } catch (error) {
            error.status === 401 ?
                showAlert(VariantType.DANGER, t('login-error')) :
                showAlert(VariantType.DANGER, error.message);
        }
    }

    function getInitialsAttribute(name) {
        const _name = name.split(" ");
        const initials = _name.length > 1 ? _name[0][0] + _name[_name.length - 1][0] : _name[0][0];
        return initials;
    }

    function setPassword(value) {
        setAccount({ ...account, password: value });
    }
    // <Form.Control type="password" name="password" defaultValue={account.password} onChange={(v) => setAccount({ ...account, password: v.target.value })} /> 

    return (
        <>
            <div className='login-header-box'>
                <span className="homeText">{t('app-name', { ns: 'common' })}</span>
                <span>{t('login-text')}</span>
            </div>
            <div className='login-box'>
                <Form onSubmit={handleSubmitLogin} className='w-100'>
                    <Form.Group className="mb-2" controlId="formId">
                        <Form.Label size="sm" className="mb-0 title">{t('email')}</Form.Label>
                        <Form.Control type="text" name="email" defaultValue={account.email} onChange={(v) => setAccount({ ...account, email: v.target.value })}
                        />
                    </Form.Group>
                    <Form.Group className="mb-2" controlId="formId">
                        <div className="d-flex justify-content-between pt-2 ">
                            <Form.Label size="sm" className="mb-0 title">{t('password')}</Form.Label>
                            <Button bsPrefix="btn-custom-login" className="text-small" size="sm" href={'/reset-password/' + account.email} >{t('forgot-password')}</Button>
                        </div>
                        <PasswordInput defaultValue={account.password} updatePassword={setPassword} />
                    </Form.Group>
                    <div className="d-flex justify-content-end gap-1 pt-2 pb-2">
                        <Button bsPrefix='btn-custom' onClick={clearAccount} size="sm" disabled={account.email.length === 0 && account.password.length === 0}><span>{t('btn-clear', { ns: 'common' })}</span></Button>
                        <Button bsPrefix='btn-custom' type="submit" size="sm" disabled={account.email.length === 0 || account.password.length === 0}><span>{t('btn-login')}</span></Button>
                    </div>
                </Form>
            </div>
            <div className='login-footer-box'>
                <LoginWithGoogle handlePostLogin={handlePostLogin} />
                <Button bsPrefix="btn-custom-register" size="sm" href='/account/new'>{t('new-to-the-app')}<span className="link ps-1">{t('create-account')}</span></Button>
            </div>
        </>

    )
}