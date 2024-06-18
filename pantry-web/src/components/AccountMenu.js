import Image from 'react-bootstrap/Image';
import iAccount from '../assets/images/profile.png';
import iNoAccount from '../assets/images/no-login.png';
import { ProfileContext } from '../services/context/AppContext';
import React, { useContext, useState } from 'react';
import OverlayTrigger from 'react-bootstrap/OverlayTrigger';
import Popover from 'react-bootstrap/Popover';
import { Button } from 'react-bootstrap';
import { LogoutFromGoogle } from './LoginWithGoogle.js';
import { logout } from '../services/LoginService';
import { useNavigate } from 'react-router-dom';
import Tooltip from 'react-bootstrap/Tooltip';
import { useTranslation } from 'react-i18next';

function AccountMenu() {

    const { t } = useTranslation(['header']);

    const navigate = useNavigate();
    const { profileCtx, setProfileCtx } = useContext(ProfileContext);
    const [showProfile, setShowProfile] = useState(false);

    const handleLogout = async () => {
        await logout();
        setProfileCtx(null);
        setShowProfile(false);
        navigate('/login');
    };

    function renderProfilePopover() {
        const popoverProfile = (
            <Popover id="popover-basic-profile">
                <Popover.Body className='card-login-profile'>
                    {profileCtx.pictureUrl && profileCtx.pictureUrl.length > 0 ?
                        <Image className='profile-icon' width={50} height={50} roundedCircle referrerPolicy="no-referrer" src={profileCtx.pictureUrl} /> :
                        <div className='gradient-round-box'><span className="gradient-icon-label">{profileCtx.initials}</span></div>
                        // <Image width={50} height={50} roundedCircle referrerPolicy="no-referrer" src={iAccount} />
                    }
                    <span className='title'>{profileCtx.name}</span>
                    <span className='small'>{profileCtx.email}</span>

                    <div className='section pt-1'><span className='title small'>{t("settings")}</span></div>
                    <div className="settings pb-2">
                        <Button bsPrefix="btn-profile-menu" href={"/account/edit"}>{t("edit-account-link")}</Button>
                        <Button bsPrefix="btn-profile-menu" href="/group-members">{t("group-members-link")}</Button>
                    </div >
                    <LogoutFromGoogle handleLogout={handleLogout} text={t("btn-logout")} />
                </Popover.Body >
            </Popover >
        );

        return (
            <>
                <OverlayTrigger placement="left-start" trigger="click" overlay={popoverProfile} onToggle={(e) => setShowProfile(e)} show={showProfile}>
                    <Button variant='link'>
                        {profileCtx.pictureUrl && profileCtx.pictureUrl.length > 0 ?
                            <Image className='profile-icon hover-effect' width={40} height={40} roundedCircle referrerPolicy="no-referrer" src={profileCtx.pictureUrl} /> :
                            <div className="gradient-round-box hover-effect"><span className="gradient-icon-label">{profileCtx.initials}</span></div>
                            // <Image className='hover-effect' width={30} height={30} roundedCircle referrerPolicy="no-referrer" src={iAccount} />
                        }
                    </Button>
                </OverlayTrigger>
            </>
        )
    }

    function renderUserIcon() {
        return (
            <OverlayTrigger placement="bottom" delay={{ show: 250, hide: 400 }} overlay={<Tooltip className="custom-tooltip">{t("tooltip-login")}</Tooltip>}>
                <Button variant='link' href="/login" >
                    <Image className='hover-effect' width={30} height={30} roundedCircle referrerPolicy="no-referrer" src={iNoAccount} />
                </Button>
            </OverlayTrigger>
        )
    }

    return (
        <div>
            {profileCtx && Object.keys(profileCtx).length > 0 ? renderProfilePopover() : renderUserIcon()}
        </div>
    )
}
export default AccountMenu;