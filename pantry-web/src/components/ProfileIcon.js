import Image from 'react-bootstrap/Image';
import iAccount from '../assets/images/profile.png';
import iNoAccount from '../assets/images/no-login.png';
import { ProfileContext } from '../services/context/AppContext';
import React, { useContext, useState, useRef } from 'react';
import OverlayTrigger from 'react-bootstrap/OverlayTrigger';
import Popover from 'react-bootstrap/Popover';
import { Button } from 'react-bootstrap';
import { LoginWithGoogle, LogoutFromGoogle } from './LoginWithGoogle.js';
import { logout, register, login } from '../services/LoginService';
import { useNavigate } from 'react-router-dom';
import Form from 'react-bootstrap/Form';
import VariantType from '../components/VariantType.js';
import useAlert from '../hooks/useAlert.js';
import Tooltip from 'react-bootstrap/Tooltip';
import { getAccount, postResetPassword, getResetPassword, updateAccount } from '../services/apis/mypantry/requests/AccountRequests';

function ProfileIcon() {

    const { profileCtx, setProfileCtx } = useContext(ProfileContext);
    const target = useRef(null);
    const navigate = useNavigate();

    const [showProfile, setShowProfile] = useState(false);
    const [showEditProfile, setShowEditProfile] = useState(false);
    const [showLogin, setShowLogin] = useState(false);
    const [showRegister, setShowRegister] = useState(false);
    const [showResetPassword, setShowResetPassword] = useState(false);

    const [name, setName] = useState("");
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");

    const [passwordQuestion, setPasswordQuestion] = useState("");
    const [passwordAnswer, setPasswordAnswer] = useState("");

    const { showAlert } = useAlert();

    const handleLogout = async () => {
        await logout();
        clearNewAccountStates();
        setProfileCtx(null);
        setShowProfile(false);
        navigate('/login');
    };

    const handlePostLogin = (profile) => {
        setProfileCtx(profile);
        setShowLogin(false);
        navigate('/');
    }

    async function fetchProfile(id) {
        try {
            const res = await getAccount(id);
            setName(res.name);
            setEmail(res.email);
            setPassword(res.password);
            setPasswordQuestion(res.passwordQuestion);
            setPasswordAnswer(res.passwordAnswer);
            toggleProfileEditProfile();
        } catch (error) {
            showAlert(VariantType.DANGER, error.message);
        }
    }

    async function handleRegister() {
        try {
            var account = {
                name: name,
                email: email,
                password: password,
                passwordQuestion: passwordQuestion,
                passwordAnswer: passwordAnswer
            }

            await register(account);
            showAlert(VariantType.SUCCESS, "You've been successfully registered. Please login.");
        } catch (error) {
            showAlert(VariantType.DANGER, error.message);
        } finally {
            clearNewAccountStates();
            setShowRegister(false);
        }
    }

    async function handleEditProfile() {
        try {
            var account = {
                id: profileCtx.id,
                name: name,
                email: email,
                password: password,
                passwordQuestion: passwordQuestion,
                passwordAnswer: passwordAnswer
            }

            await updateAccount(account);
            showAlert(VariantType.SUCCESS, "Your profile has been successfully updated.");
        } catch (error) {
            showAlert(VariantType.DANGER, error.message);
        } finally {
            clearNewAccountStates();
            setShowEditProfile(false);

        }
    }

    async function handleLogin() {
        try {
            var account = { email: email, password: password }
            var profile = await login(account);
            handlePostLogin(profile);
        } catch (error) {
            setShowLogin(false);
            error.status === 401 ?
                showAlert(VariantType.DANGER, "Status 401: Invalid email or password!") :
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
        } finally {
            clearNewAccountStates();
            setShowResetPassword(false);
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
        toggleLoginRegister();
    }

    function handleCancelResetPassword() {
        setPasswordQuestion("");
        setPasswordAnswer("");
        setPassword("");
        toggleLoginResetPassword();
    }

    async function getPasswordQuestion() {
        try {
            const res = await getResetPassword(email);
            setPasswordQuestion(res.passwordQuestion);
            toggleLoginResetPassword();
        }
        catch (error) {
            showAlert(VariantType.DANGER, error.message);
        }
    }

    function renderPopovers() {

        if (showRegister) return renderRegisterPopover();

        if (showResetPassword) return renderResetPasswordPopover();

        return renderLoginPopover();
    }

    function renderProfilePopovers() {

        if (showEditProfile) return renderEditProfilePopover();

        return renderProfilePopover();
    }

    function toggleLoginRegister() {
        setShowLogin(!showLogin);
        setShowRegister(!showRegister);
    }

    function toggleLoginResetPassword() {
        setShowLogin(!showLogin);
        setShowResetPassword(!showResetPassword);
    }

    function toggleProfileEditProfile() {
        setShowProfile(!showProfile);
        setShowEditProfile(!showEditProfile);
    }

    function renderProfilePopover() {
        const popoverProfile = (
            <Popover id="popover-basic-profile">
                <Popover.Body className='card-login-profile'>
                    {profileCtx.pictureUrl && profileCtx.pictureUrl.length > 0 ?
                        <Image className='profile-icon' width={50} height={50} roundedCircle referrerPolicy="no-referrer" src={profileCtx.pictureUrl} /> :
                        <Image width={50} height={50} roundedCircle referrerPolicy="no-referrer" src={iAccount} />
                    }
                    <span className='title'>{profileCtx.name}</span>
                    <span className='small'>{profileCtx.email}</span>

                    <div className='section pt-1'><span className='title small'>Settings:</span></div>
                    <div className="settings pb-2">
                        <Button variant="link" className='p-0' onClick={() => fetchProfile(profileCtx.id)}><span className='small'>My Profile</span></Button>
                        <Button href="#" variant="link" className='p-0'><span className='small'>App Properties</span></Button>
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
                            <Image className='profile-icon hover-effect' width={40} height={40} roundedCircle referrerPolicy="no-referrer" src={profileCtx.pictureUrl} /> :
                            <Image className='hover-effect' width={30} height={30} roundedCircle referrerPolicy="no-referrer" src={iAccount} />
                        }
                    </Button>
                </OverlayTrigger>
            </>
        )
    }

    //Edit Profile Form
    function renderEditProfilePopover() {
        const popoverEditProfile = (
            <Popover id="popover-basic-editProfile">
                <Popover.Body className='card-login-profile'>
                    <Form.Control size="sm" type="text" placeholder='name' defaultValue={name} onChange={(e) => setName(e.target.value)} />
                    <Form.Control size="sm" type="text" placeholder='email' defaultValue={email} onChange={(e) => setEmail(e.target.value)} />
                    <Form.Control size="sm" type="password" placeholder='password' defaultValue={password} onChange={(e) => setPassword(e.target.value)} />
                    <Form.Control size="sm" type="text" placeholder='Reset Password Question' defaultValue={passwordQuestion} onChange={(e) => setPasswordQuestion(e.target.value)} />
                    <Form.Control size="sm" type="text" placeholder='Reset Password Answer' defaultValue={passwordAnswer} onChange={(e) => setPasswordAnswer(e.target.value)} />
                    <div className="d-flex justify-content-center gap-1 pt-2 pb-2">
                        <Button bsPrefix="btn-custom" size="sm" onClick={handleEditProfile} variant="link" >Save</Button>
                        <Button bsPrefix="btn-custom" size="sm" onClick={toggleProfileEditProfile} variant="link" >Cancel</Button>
                    </div>
                </Popover.Body>
            </Popover>
        );

        return (
            <OverlayTrigger placement="left-start" trigger="click" overlay={popoverEditProfile} onToggle={(e) => setShowEditProfile(e)} show={showEditProfile}>
                <Button variant='link'>
                    {profileCtx.pictureUrl && profileCtx.pictureUrl.length > 0 ?
                        <Image className='profile-icon hover-effect' width={40} height={40} roundedCircle referrerPolicy="no-referrer" src={profileCtx.pictureUrl} /> :
                        <Image className='hover-effect' width={30} height={30} roundedCircle referrerPolicy="no-referrer" src={iAccount} />
                    }
                </Button>
            </OverlayTrigger>
        )
    }

    //Register Form
    function renderRegisterPopover() {
        const popoverRegister = (
            <Popover id="popover-basic-register">
                <Popover.Body className='card-login-profile'>
                    <Form.Control size="sm" type="text" placeholder='name' onChange={(e) => setName(e.target.value)} />
                    <Form.Control size="sm" type="text" placeholder='email' onChange={(e) => setEmail(e.target.value)} />
                    <Form.Control size="sm" type="password" placeholder='password' onChange={(e) => setPassword(e.target.value)} />
                    <Form.Control size="sm" type="text" placeholder='Reset Password Question' defaultValue={passwordQuestion} onChange={(e) => setPasswordQuestion(e.target.value)} />
                    <Form.Control size="sm" type="text" placeholder='Reset Password Answer' defaultValue={passwordAnswer} onChange={(e) => setPasswordAnswer(e.target.value)} />
                    <div className="d-flex justify-content-center gap-1 pt-2 pb-2">
                        <Button bsPrefix="btn-custom" size="sm" onClick={handleRegister} variant="link" >Register</Button>
                        <Button bsPrefix="btn-custom" size="sm" onClick={handleCancelSignup} variant="link" >Cancel</Button>
                    </div>
                    <LoginWithGoogle handlePostLogin={handlePostLogin} />
                </Popover.Body>
            </Popover>
        );

        return (
            <OverlayTrigger placement="left-start" trigger="click" overlay={popoverRegister} onToggle={(e) => setShowRegister(e)} show={showRegister}>
                <Button variant='link'>
                    <Image className='hover-effect' width={30} height={30} roundedCircle referrerPolicy="no-referrer" src={iNoAccount} />
                </Button>
            </OverlayTrigger>
        )
    }

    //Login Form
    function renderLoginPopover() {
        const popoverLogin = (
            <Popover id="popover-basic-login">
                <Popover.Body className='card-login-profile'>
                    <Form.Control size="sm" type="text" placeholder='name' onChange={(e) => setName(e.target.value)} hidden={true} />
                    <Form.Control size="sm" type="text" placeholder='email' defaultValue={email} onChange={(e) => setEmail(e.target.value)} />
                    <Form.Control size="sm" type="password" placeholder='password' defaultValue={password} onChange={(e) => setPassword(e.target.value)} />
                    <div className="d-flex justify-content-end"><Button variant='link' size="sm" onClick={getPasswordQuestion} disabled={email.length === 0}>Reset password</Button></div>
                    <div className="d-flex justify-content-center gap-1 pt-2 pb-2">
                        <Button bsPrefix="btn-custom" size="sm" onClick={handleLogin} variant="link" >Login</Button>
                        <Button bsPrefix="btn-custom" size="sm" onClick={toggleLoginRegister} variant="link" >Register</Button>
                    </div>
                    <LoginWithGoogle handlePostLogin={handlePostLogin} />
                </Popover.Body>
            </Popover>
        );

        return (
            <OverlayTrigger placement="left-start" trigger="click" overlay={popoverLogin} onToggle={(e) => setShowLogin(e)} show={showLogin}>
                <Button variant='link'>
                    <Image className='hover-effect' width={30} height={30} roundedCircle referrerPolicy="no-referrer" src={iNoAccount} />
                </Button>
            </OverlayTrigger>
        )
    }

    function renderUserIcon() {
        return (
            <Button variant='link'>
                <Image className='hover-effect' width={30} height={30} roundedCircle referrerPolicy="no-referrer" src={iNoAccount} />
            </Button>
        )
    }

    //Reset Password Form
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
                </Popover.Body>
            </Popover>
        );

        return (
            <OverlayTrigger placement="left-start" trigger="click" overlay={popoverResetPassword} onToggle={(e) => setShowResetPassword(e)} show={showResetPassword}>
                <Button variant='link'>
                    <Image className='hover-effect' width={30} height={30} roundedCircle referrerPolicy="no-referrer" src={iNoAccount} />
                </Button>
            </OverlayTrigger>
        )
    }


    return (
        <div>
            {profileCtx && Object.keys(profileCtx).length > 0 ? renderProfilePopovers() : renderUserIcon()}
        </div>
    )
}
export default ProfileIcon;