import Image from 'react-bootstrap/Image';
import iAccount from '../assets/images/profile.png';
import iNoAccount from '../assets/images/no-login.png';
import { ProfileContext } from '../services/context/AppContext';
import React, { useContext, useState, useRef } from 'react';
import OverlayTrigger from 'react-bootstrap/OverlayTrigger';
import Popover from 'react-bootstrap/Popover';
import { Button, ListGroupItem } from 'react-bootstrap';
import { LoginWithGoogle, LogoutFromGoogle } from './LoginWithGoogle.js';
import { logout, signup, login } from '../services/LoginService';
import { useNavigate } from 'react-router-dom';
import Form from 'react-bootstrap/Form';
import { AlertContext } from '../services/context/AppContext.js';
import VariantType from '../components/VariantType.js';
import Tooltip from 'react-bootstrap/Tooltip';
import { postResetPassword, getResetPassword } from '../services/apis/mypantry/requests/AccountRequests';

function ProfileIcon() {

    const { profileCtx, setProfileCtx } = useContext(ProfileContext);
    const target = useRef(null);
    const navigate = useNavigate();

    const [showProfile, setShowProfile] = useState(false);
    const [showLogin, setShowLogin] = useState(false);
    const [showRegister, setShowRegister] = useState(false);
    const [showResetPassword, setShowResetPassword] = useState(false);

    const [name, setName] = useState("");
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");

    const [passwordQuestion, setPasswordQuestion] = useState("");
    const [passwordAnswer, setPasswordAnswer] = useState("");

    const { setAlert } = useContext(AlertContext);

    const handleLogout = async () => {
        await logout();
        clearNewAccountStates();
        setProfileCtx(null);
        setShowProfile(false);
        navigate('/logout');
    };

    const handlePostLogin = (profile) => {
        setProfileCtx(profile);
        setShowLogin(false);
        navigate('/');
    }

    async function handleSignup() {
        try {
            var account = {
                name: name,
                email: email,
                password: password,
                passwordQuestion: passwordQuestion,
                passwordAnswer: passwordAnswer
            }

            await signup(account);
            clearNewAccountStates();
            setShowRegister(false);
            showAlert(VariantType.SUCCESS, "You've been successfully registered. Please login.");
        } catch (error) {
            showAlert(VariantType.DANGER, error.message);
        }
    }

    async function handleLogin() {
        try {
            var account = { email: email, password: password }
            var profile = await login(account);
            handlePostLogin(profile);
        } catch (error) {
            showAlert(VariantType.DANGER, error.message);
        }
    }

    function clearNewAccountStates() {
        setPassword("");
        setName("");
        setPasswordQuestion("");
        setPasswordAnswer("");
    }

    function handleCancelSignup() {
        clearNewAccountStates();
        toggleSigninSignup();
    }

    function handleCancelResetPassword() {
        setPasswordQuestion("");
        setPasswordAnswer("");
        setPassword("");
        toggleSigninResetPassword();
    }

    async function getPasswordQuestion() {
        try {
            const res = await getResetPassword(email);
            setPasswordQuestion(res.passwordQuestion);
            toggleSigninResetPassword();
        }
        catch (error) {
            showAlert(VariantType.DANGER, error.message);
        }
    }

    async function handleResetPassword() {
        try {
            var account = { email: email, passwordAnswer: passwordAnswer, password: password }
            await postResetPassword(account);
            showAlert(VariantType.SUCCESS, "Password successfully updated. Please log in.")
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

    function renderProfilePopover() {
        const popoverProfile = (
            <Popover id="popover-basic">
                <Popover.Body className='card-login-profile'>
                    {profileCtx.pictureUrl && profileCtx.pictureUrl.length > 0 ?
                        <Image className='profile-icon' width={50} height={50} roundedCircle referrerPolicy="no-referrer" src={profileCtx.pictureUrl} /> :
                        <Image width={50} height={50} roundedCircle referrerPolicy="no-referrer" src={iAccount} />
                    }
                    <span className='title'>{profileCtx.name}</span>
                    <span className='small'>{profileCtx.email}</span>

                    <div className='section pt-1'><span className='title small'>Settings:</span></div>
                    <div className="settings pb-2">
                        <Button href="#" variant="link" className='p-0'><span className='small'>Properties</span></Button>
                        <Button href="/group-members" variant="link" className='p-0'><span className='small'>Groups & Members</span></Button>
                    </div >
                    <LogoutFromGoogle handleLogout={handleLogout}>Logout</LogoutFromGoogle>
                </Popover.Body >
            </Popover >
        );

        return (
            <>
                <OverlayTrigger placement="left-start" trigger="click" overlay={popoverProfile} onToggle={(e) => setShowProfile(e)} show={showProfile}>
                    <Button variant='link'>
                        {profileCtx.pictureUrl && profileCtx.pictureUrl.length > 0 ?
                            <Image className='profile-icon' width={40} height={40} roundedCircle referrerPolicy="no-referrer" src={profileCtx.pictureUrl} /> :
                            <Image width={30} height={30} roundedCircle referrerPolicy="no-referrer" src={iAccount} />
                        }
                    </Button>
                </OverlayTrigger>
            </>
        )
    }

    function renderPopovers() {

        if (showRegister) {
            return renderSignUpPopover();
        }
        if (showResetPassword) {
            return renderResetPasswordPopover();
        }
        return renderSignInPopover();

    }

    function renderSignUpPopover() {
        const popoverSignUp = (
            <Popover id="popover-basic-signup">
                <Popover.Body className='card-login-profile'>
                    <Form.Control size="sm" type="text" placeholder='name' onChange={(e) => setName(e.target.value)} />
                    <Form.Control size="sm" type="text" placeholder='email' onChange={(e) => setEmail(e.target.value)} />
                    <Form.Control size="sm" type="password" placeholder='password' onChange={(e) => setPassword(e.target.value)} />
                    <Form.Control size="sm" type="text" placeholder='Reset Password Question' defaultValue={passwordQuestion} onChange={(e) => setPasswordQuestion(e.target.value)} />
                    <Form.Control size="sm" type="text" placeholder='Reset Password Answer' defaultValue={passwordAnswer} onChange={(e) => setPasswordAnswer(e.target.value)} />
                    <div className="d-flex justify-content-center gap-1 pt-2 pb-2">
                        <Button bsPrefix="btn-custom" size="sm" onClick={handleSignup} variant="link" >Register</Button>
                        <Button bsPrefix="btn-custom" size="sm" onClick={handleCancelSignup} variant="link" >Cancel</Button>
                    </div>
                    <LoginWithGoogle handlePostLogin={handlePostLogin} />
                </Popover.Body>
            </Popover>
        );

        return (
            <OverlayTrigger placement="left-start" trigger="click" overlay={popoverSignUp} onToggle={(e) => setShowRegister(e)} show={showRegister}>
                <Button variant='link'>
                    <Image width={30} height={30} roundedCircle referrerPolicy="no-referrer" src={iNoAccount} />
                </Button>
            </OverlayTrigger>
        )
    }
    function toggleSigninSignup() {
        setShowLogin(!showLogin);
        setShowRegister(!showRegister);
    }

    function toggleSigninResetPassword() {
        setShowLogin(!showLogin);
        setShowResetPassword(!showResetPassword);
    }

    function renderSignInPopover() {
        const popoverSignIn = (
            <Popover id="popover-basic-signin">
                <Popover.Body className='card-login-profile'>
                    <Form.Control size="sm" type="text" placeholder='name' onChange={(e) => setName(e.target.value)} hidden={true} />
                    <Form.Control size="sm" type="text" placeholder='email' defaultValue={email} onChange={(e) => setEmail(e.target.value)} />
                    <Form.Control size="sm" type="password" placeholder='password' defaultValue={password} onChange={(e) => setPassword(e.target.value)} />
                    <div className="d-flex justify-content-end"><Button variant='link' size="sm" onClick={getPasswordQuestion} disabled={email.length === 0}>Reset password</Button></div>
                    <div className="d-flex justify-content-center gap-1 pt-2 pb-2">
                        <Button bsPrefix="btn-custom" size="sm" onClick={handleLogin} variant="link" >Login</Button>
                        <Button bsPrefix="btn-custom" size="sm" onClick={toggleSigninSignup} variant="link" >Register</Button>
                    </div>
                    <LoginWithGoogle handlePostLogin={handlePostLogin} />
                </Popover.Body>
            </Popover>
        );

        return (
            <OverlayTrigger placement="left-start" trigger="click" overlay={popoverSignIn} onToggle={(e) => setShowLogin(e)} show={showLogin}>
                <Button variant='link'>
                    <Image width={30} height={30} roundedCircle referrerPolicy="no-referrer" src={iNoAccount} />
                </Button>
            </OverlayTrigger>
        )
    }

    function renderResetPasswordPopover() {
        const popoverResetPassword = (
            <Popover id="popover-basic-resetPassword">
                <Popover.Body className='card-login-profile'>
                    <span>{email}</span>
                    <span>{passwordQuestion}</span>
                    <Form.Control size="sm" type="text" placeholder='Reset Password Answer' defaultValue={passwordAnswer} onChange={(e) => setPasswordAnswer(e.target.value)} />
                    <Form.Control size="sm" type="password" placeholder='New Password' defaultValue={password} onChange={(e) => setPassword(e.target.value)} />
                    <div className="d-flex justify-content-center gap-1 pt-2 pb-2">
                        <Button bsPrefix="btn-custom" size="sm" onClick={handleResetPassword} variant="link" >Reset Password</Button>
                        <Button bsPrefix="btn-custom" size="sm" onClick={handleCancelResetPassword} variant="link" >Cancel</Button>
                    </div>
                    <LoginWithGoogle handlePostLogin={handlePostLogin} />
                </Popover.Body>
            </Popover>
        );

        return (
            <OverlayTrigger placement="left-start" trigger="click" overlay={popoverResetPassword} onToggle={(e) => setShowResetPassword(e)} show={showResetPassword}>
                <Button variant='link'>
                    <Image width={30} height={30} roundedCircle referrerPolicy="no-referrer" src={iNoAccount} />
                </Button>
            </OverlayTrigger>
        )
    }


    return (
        <div>
            {profileCtx && Object.keys(profileCtx).length > 0 ? renderProfilePopover() : renderPopovers()}
        </div>
    )
}
export default ProfileIcon;