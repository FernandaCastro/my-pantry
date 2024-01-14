import Image from 'react-bootstrap/Image';
import iAccount from '../assets/images/profile.png';
import { ProfileContext } from '../services/context/AppContext';
import React, { useContext, useState, useRef } from 'react';
import OverlayTrigger from 'react-bootstrap/OverlayTrigger';
import Popover from 'react-bootstrap/Popover';
import { Button } from 'react-bootstrap';
import { LoginWithGoogle, LogoutFromGoogle } from './LoginWithGoogle.js';
import { logout } from '../services/LoginService';

function LoginButton() {

    const { profileCtx, setProfileCtx } = useContext(ProfileContext);
    const [showProfile, setShowProfile] = useState(false);
    const [showLogin, setShowLogin] = useState(false);
    const target = useRef(null);

    const handleLogout = async () => {
        await logout();
        setProfileCtx(null);
        setShowProfile(false);
    };

    function renderProfilePopover() {
        const popoverProfile = (
            <Popover id="popover-basic">
                <Popover.Body className='card-login-profile'>
                    <Image width={50} height={50} roundedCircle referrerPolicy="no-referrer" src={profileCtx.pictureUrl} />
                    <span className='title'>{profileCtx.name}</span>
                    <span className='small'>{profileCtx.email}</span>
                    <LogoutFromGoogle onClick={handleLogout}>Logout</LogoutFromGoogle>
                </Popover.Body>
            </Popover>
        );

        return (
            <OverlayTrigger placement="bottom-start" trigger="click" overlay={popoverProfile} onToggle={(e) => setShowProfile(e)} show={showProfile}>
                <Button variant='link'><Image width={30} height={30} roundedCircle referrerPolicy="no-referrer" src={profileCtx.pictureUrl} /></Button>
            </OverlayTrigger>
        )
    }

    function renderSignInPopover() {
        const popoverSignIn = (
            <Popover id="popover-basic">
                <Popover.Body className='card-login-profile'>
                    <LoginWithGoogle setShowLogin={setShowLogin} />
                </Popover.Body>
            </Popover>
        );

        return (
            <OverlayTrigger placement="bottom-start" trigger="click" overlay={popoverSignIn} onToggle={(e) => setShowLogin(e)} show={showLogin}>
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

{/* <Modal className='custom-modal-child' show={showProfile} onHide={() => setShowProfile(false)} size='sm' animation={true} style={{ position: 'absolute' }}>
<Modal.Header closeButton>
    <Modal.Title as='h6'>
        <div>
            <Image width={50} height={50} rounded referrerPolicy="no-referrer" src={profileCtx.pictureUrl} />
            <span>{profileCtx.name}</span>
        </div>
    </Modal.Title>
</Modal.Header>
<Modal.Body>
    List of options for profile
</Modal.Body>
</Modal> */}