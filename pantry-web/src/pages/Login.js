import { ProfileContext } from '../services/context/AppContext';
import React, { useContext, useState} from 'react';
import { Button } from 'react-bootstrap';
import { LoginWithGoogle } from '../components/LoginWithGoogle.js';
import { login } from '../services/LoginService';
import { useNavigate } from 'react-router-dom';
import Form from 'react-bootstrap/Form';
import VariantType from '../components/VariantType.js';
import useAlert from '../hooks/useAlert.js';

export default function Login() {

    const navigate = useNavigate();
    const { profileCtx, setProfileCtx } = useContext(ProfileContext);
    const { showAlert } = useAlert();

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
                showAlert(VariantType.DANGER, "Status 401: Invalid email or password!") :
                showAlert(VariantType.DANGER, error.message);
        }
    }

    return (
        <>
            <div className='login-header-box'>
                <span className="homeText">My Pantry</span>
                <span>Sign in to My Pantry</span>
            </div>
            <div className='login-box'>
                <Form onSubmit={handleSubmitLogin} className='w-100'>
                    <Form.Group className="mb-2" controlId="formId">
                        <Form.Label size="sm" className="mb-0 title">Email</Form.Label>
                        <Form.Control type="text" name="email" defaultValue={account.email} onChange={(v) => setAccount({ ...account, email: v.target.value })}
                        />
                    </Form.Group>
                    <Form.Group className="mb-2" controlId="formId">
                        <div className="d-flex justify-content-between pt-2 ">
                            <Form.Label size="sm" className="mb-0 title">Password</Form.Label>
                            <Button bsPrefix="btn-custom-login" className="text-small" size="sm" href={'/reset-password/' + account.email} >Forgot Password?</Button>
                        </div>
                        <Form.Control type="password" name="password" defaultValue={account.password} onChange={(v) => setAccount({ ...account, password: v.target.value })} />
                    </Form.Group>

                    <div className="d-flex justify-content-end gap-1 pt-2 pb-2">
                        <Button bsPrefix='btn-custom' onClick={clearAccount} size="sm" disabled={account.email.length === 0 && account.password.length === 0}>Clear</Button>
                        <Button bsPrefix='btn-custom' type="submit" size="sm" disabled={account.email.length === 0 || account.password.length === 0}>Login</Button>
                    </div>
                </Form>
            </div>
            <div className='login-footer-box'>
                <LoginWithGoogle handlePostLogin={handlePostLogin} />
                <Button bsPrefix="btn-custom-register" size="sm" href='/account/new'>New to My Pantry? <span className="link">Create an Account</span></Button>
            </div>
        </>

    )
}