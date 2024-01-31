import Image from 'react-bootstrap/Image';
import iAccount from '../assets/images/profile.png';
import { ProfileContext } from '../services/context/AppContext';
import React, { useContext, useState, useRef } from 'react';
import OverlayTrigger from 'react-bootstrap/OverlayTrigger';
import Popover from 'react-bootstrap/Popover';
import { Button } from 'react-bootstrap';
import { LoginWithGoogle, LogoutFromGoogle } from './LoginWithGoogle.js';
import { logout } from '../services/LoginService';
import { useNavigate } from 'react-router-dom';

function LoginButton() {

    const { profileCtx, setProfileCtx } = useContext(ProfileContext);
    const [showProfile, setShowProfile] = useState(false);
    const [showLogin, setShowLogin] = useState(false);
    const target = useRef(null);
    const navigate = useNavigate();

    const handleLogout = async () => {
        await logout();
        setProfileCtx(null);
        setShowProfile(false);
        navigate('/logout');
    };

    const handleLogin = (profile) => {
        setProfileCtx(profile);
        setShowLogin(false);
        navigate('/');
    }

    function renderProfilePopover() {
        const popoverProfile = (
            <Popover id="popover-basic">
                <Popover.Body className='card-login-profile'>
                    <Image width={50} height={50} roundedCircle referrerPolicy="no-referrer" src={profileCtx.pictureUrl} />
                    <span className='title'>{profileCtx.name}</span>
                    <span className='small'>{profileCtx.email}</span>
                    <LogoutFromGoogle handleLogout={handleLogout}>Logout</LogoutFromGoogle>
                </Popover.Body>
            </Popover>
        );

        return (
            <OverlayTrigger placement="auto" trigger="click" overlay={popoverProfile} onToggle={(e) => setShowProfile(e)} show={showProfile}>
                <Button variant='link'><Image width={30} height={30} roundedCircle referrerPolicy="no-referrer" src={profileCtx.pictureUrl} /></Button>
            </OverlayTrigger>
        )
    }

    function renderSignInPopover() {
        const popoverSignIn = (
            <Popover id="popover-basic">
                <Popover.Body className='card-login-profile'>
                    <LoginWithGoogle handleLogin={handleLogin} />
                </Popover.Body>
            </Popover>
        );

        return (
            <OverlayTrigger placement="auto" trigger="click" overlay={popoverSignIn} onToggle={(e) => setShowLogin(e)} show={showLogin}>
                <Button variant='link'>
                    <Image width={30} height={30} roundedCircle referrerPolicy="no-referrer" src={iAccount} />
                </Button>
            </OverlayTrigger>
        )
    }


    return (
        <div>
            {profileCtx && Object.keys(profileCtx).length > 0 ? renderProfilePopover() : renderSignInPopover()}
        </div>
    )
}
export default LoginButton;