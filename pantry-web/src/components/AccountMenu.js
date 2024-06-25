import Image from 'react-bootstrap/Image';
import iNoAccount from '../assets/images/no-login.png';
import { ProfileContext } from '../services/context/AppContext';
import React, { useContext, useEffect, useState } from 'react';
import { Button, Offcanvas } from 'react-bootstrap';
import { LogoutFromGoogle } from './LoginWithGoogle.js';
import { logout } from '../services/LoginService';
import { useNavigate } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import Select from './Select';
import { BsPalette, BsPeople, BsPersonGear } from 'react-icons/bs';

function AccountMenu() {

    const { t } = useTranslation(['header']);

    const navigate = useNavigate();
    const { profileCtx, setProfileCtx } = useContext(ProfileContext);
    const [expandMenu, setExpandMenu] = useState(false);

    const themes = [
        { label: 'Default Light', value: '' },
        { label: 'Default Dark', value: 'theme-dark' },
        { label: 'Mono Light', value: 'theme-mono-light' },
        { label: 'Mono Dark', value: 'theme-mono-dark' }

    ]
    const [themeOption, setThemeOption] = useState(findThemeOption());

    const handleLogout = async () => {
        await logout();
        setProfileCtx({ theme: themeOption.value });
        setExpandMenu(!expandMenu)
        navigate('/login');
    };

    useEffect(() => {
        if (themeOption) {
            document.body.className = themeOption.value;
            setProfileCtx(
                {
                    ...profileCtx,
                    theme: themeOption.value
                })
            handleClose();

        }
    }, [themeOption.value]);

    function handleClose() {
        document.getElementsByClassName("btn-close")[0]?.click();
        //document.getElementById("btn-close")?.click();
    }


    function findThemeOption() {
        const theme = { label: 'Default Light', value: '' }
        if (profileCtx && profileCtx.theme) {
            const found = themes.find(t => t.value === profileCtx.theme);
            return found ? found : theme;
        }
        return theme;
    }

    function renderSingin() {
        return (
            <Button variant='link' href="/login" >
                <Image className='hover-effect' width={30} height={30} roundedCircle referrerPolicy="no-referrer" src={iNoAccount} />
            </Button>
        )
    }

    function renderUserProfile() {

        return (
            <div>
                <Button variant='link' onClick={() => setExpandMenu(!expandMenu)} >
                    {profileCtx.pictureUrl && profileCtx.pictureUrl.length > 0 ?
                        <Image className='profile-icon hover-effect' width={40} height={40} roundedCircle referrerPolicy="no-referrer" src={profileCtx.pictureUrl} /> :
                        <div className="gradient-profile-box hover-effect"><span className="gradient-icon-label">{profileCtx.initials}</span></div>
                    }
                </Button>

                <Offcanvas className="slide-menu-box" show={expandMenu} placement="end" onHide={() => setExpandMenu(!expandMenu)}>
                    <Offcanvas.Header closeButton className='align-items-start pb-0'>
                        <div className='d-flex flex-column justify-content-start align-items-center flex-grow-1'>
                            {profileCtx.pictureUrl && profileCtx.pictureUrl.length > 0 ?
                                <Image className='profile-icon' width={50} height={50} roundedCircle referrerPolicy="no-referrer" src={profileCtx.pictureUrl} /> :
                                <div className='gradient-profile-box'><span className="gradient-icon-label">{profileCtx.initials}</span></div>

                            }
                            <span className='title mt-2'>{profileCtx.name}</span>
                            <span className='small'>{profileCtx.email}</span>
                        </div>
                    </Offcanvas.Header>
                    <Offcanvas.Body className="d-flex flex-column gap-3">
                        <div className='section' />
                        <div className="settings">
                            <div className='d-flex flex-row'>
                                <BsPalette className="simple-icon p-0 me-2 align-bottom" />
                                <Select name="type" key={themeOption.value}
                                    defaultValue={themeOption}
                                    options={themes}
                                    onChange={setThemeOption} />
                            </div>
                            <Button bsPrefix="btn-profile-menu" href={"/account/edit"} onClick={handleClose}>
                                <BsPersonGear className="simple-icon me-2" />
                                {t("edit-account-link")}
                            </Button>
                            <Button bsPrefix="btn-profile-menu" href="/group-members" onClick={handleClose}>
                                <BsPeople className="simple-icon me-2" />
                                {t("group-members-link")}
                            </Button>
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
            {profileCtx && Object.keys(profileCtx).length > 1 ? renderUserProfile() : renderSingin()}
        </div>
    )
}
export default AccountMenu;