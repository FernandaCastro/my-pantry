import Image from 'react-bootstrap/Image';
import iAccount from '../assets/images/profile.png';
import iNoAccount from '../assets/images/no-login.png';
import { ProfileContext } from '../services/context/AppContext';
import React, { useContext, useEffect, useState } from 'react';
import OverlayTrigger from 'react-bootstrap/OverlayTrigger';
import Popover from 'react-bootstrap/Popover';
import { Button, Offcanvas } from 'react-bootstrap';
import { LogoutFromGoogle } from './LoginWithGoogle.js';
import { logout } from '../services/LoginService';
import { useNavigate } from 'react-router-dom';
import Tooltip from 'react-bootstrap/Tooltip';
import { useTranslation } from 'react-i18next';
import Select from './Select';

function AccountMenu() {

    const { t } = useTranslation(['header']);

    const navigate = useNavigate();
    const { profileCtx, setProfileCtx } = useContext(ProfileContext);
    const [showProfile, setShowProfile] = useState(false);
    const [expand, setExpand] = useState(false);
    const themes = [
        { label: 'Default Light', value: '' },
        { label: 'Default Dark', value: 'theme-dark' },
        { label: 'Mono Dark', value: 'theme-mono' }
    ]
    const [themeOption, setThemeOption] = useState(findThemeOption());

    const handleLogout = async () => {
        await logout();
        setProfileCtx(null);
        setShowProfile(false);
        navigate('/login');
    };

    useEffect(() => {
        if (themeOption) {
            document.body.className = themeOption.value;
            handleClose();
            setProfileCtx(
                {
                    ...profileCtx,
                    theme: themeOption.value
                })
        }
    }, [themeOption.value]);

    function handleClose() {
        //document.getElementsByClassName("btn-close")[0]?.click();
        document.getElementById("btn-close")?.click();
    }


    function findThemeOption() {
        const theme = { label: 'Default Light', value: '' }
        if (profileCtx && profileCtx.theme) {
            const found = themes.find(t => t.value === profileCtx.theme);
            return found ? found : theme;
        }
        return theme;
    }

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

            <OverlayTrigger placement="left-start" trigger="click" overlay={popoverProfile} onToggle={(e) => setShowProfile(e)} show={showProfile}>
                <Button variant='link'>
                    {profileCtx.pictureUrl && profileCtx.pictureUrl.length > 0 ?
                        <Image className='profile-icon hover-effect' width={40} height={40} roundedCircle referrerPolicy="no-referrer" src={profileCtx.pictureUrl} /> :
                        <div className="gradient-round-box hover-effect"><span className="gradient-icon-label">{profileCtx.initials}</span></div>
                        // <Image className='hover-effect' width={30} height={30} roundedCircle referrerPolicy="no-referrer" src={iAccount} />
                    }
                </Button>
            </OverlayTrigger>

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

    function renderSingin() {

    }

    function renderUserProfile() {

        return (
            <div>
                <Button variant='link' onClick={() => setExpand(!expand)} >
                    {profileCtx.pictureUrl && profileCtx.pictureUrl.length > 0 ?
                        <Image className='profile-icon hover-effect' width={40} height={40} roundedCircle referrerPolicy="no-referrer" src={profileCtx.pictureUrl} /> :
                        <div className="gradient-round-box hover-effect"><span className="gradient-icon-label">{profileCtx.initials}</span></div>
                    }
                </Button>

                <Offcanvas className="slide-menu-box" show={expand} placement="end" onHide={() => setExpand(!expand)}>
                    <Offcanvas.Header closeButton className='align-items-start pb-0'>
                        <div className='d-flex flex-column justify-content-start align-items-center flex-grow-1'>
                            {profileCtx.pictureUrl && profileCtx.pictureUrl.length > 0 ?
                                <Image className='profile-icon' width={50} height={50} roundedCircle referrerPolicy="no-referrer" src={profileCtx.pictureUrl} /> :
                                <div className='gradient-round-box'><span className="gradient-icon-label">{profileCtx.initials}</span></div>

                            }
                            <span className='title mt-2'>{profileCtx.name}</span>
                            <span className='small'>{profileCtx.email}</span>
                        </div>
                    </Offcanvas.Header>
                    <Offcanvas.Body className="d-flex flex-column gap-3">
                        <div className='section' />
                        <div className="settings gap-2">
                            <Select name="type" key={themeOption.value}
                                defaultValue={themeOption}
                                options={themes}
                                onChange={setThemeOption} />
                                <Button bsPrefix="btn-profile-menu" href={"/account/edit"} onClick={handleClose}>{t("edit-account-link")}</Button>
                                <Button bsPrefix="btn-profile-menu" href="/group-members" onClick={handleClose}>{t("group-members-link")}</Button>
                        </div >
                        <div className='mt-auto align-self-center'>
                            <LogoutFromGoogle handleLogout={handleLogout} text={t("btn-logout")} />
                        </div>
                    </Offcanvas.Body>
                </Offcanvas>
            </div>
        )

    }

    return (
        <div>
            {profileCtx && Object.keys(profileCtx).length > 0 ? renderUserProfile() : renderUserIcon()}
        </div>
    )
}
export default AccountMenu;