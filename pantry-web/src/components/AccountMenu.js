import Image from 'react-bootstrap/Image';
import iNoAccount from '../assets/images/no-login.png';
import React, { useEffect, useState } from 'react';
import { Button, Modal, Offcanvas } from 'react-bootstrap';
import { LogoutFromGoogle } from './LoginWithGoogle.js';
import { logout } from '../api/mypantry/account/loginService';
import { useNavigate } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import Select from './Select';
import { BsPalette, BsPeople, BsPersonGear, BsTrash } from 'react-icons/bs';
import useProfile from '../state/useProfile';
import { updateTheme } from '../api/mypantry/account/accountService';
import useGlobalLoading from '../state/useLoading';
import useAlert from '../state/useAlert';
import VariantType from '../components/VariantType.js';
import CustomLink from './CustomLink';

function AccountMenu() {

    const { t } = useTranslation(['header']);

    const navigate = useNavigate();

    const { profile, setProfile, resetProfile } = useProfile();
    const { isLoading, setIsLoading } = useGlobalLoading();
    const { showAlert } = useAlert();

    const [expandMenu, setExpandMenu] = useState(false);
    const [showModal, setShowModal] = useState(false);


    const themes = [
        { value: 'theme-mono-light', label: 'Mono Light' },
        { value: 'theme-mono-dark', label: 'Mono Dark' },
        { value: 'theme-lila-light', label: 'Lila Light' },
        { value: 'theme-lila-dark', label: 'Lila Dark' }

    ]
    const [themeOption, setThemeOption] = useState(() => {
        var optionData = { value: 'theme-mono-light', label: 'Mono Light' };
        if (profile?.theme) {
            const found = themes.find(t => t.value === profile.theme);
            if (found) {
                optionData = found;
            }
        }
        return optionData;
    });

    const handleLogout = async () => {
        await logout();

        resetProfile();
        setExpandMenu(false)
        navigate('/login');
    };

    useEffect(() => {
        if (themeOption.value && profile?.theme && themeOption.value !== profile?.theme) {
            document.body.className = themeOption.value;

            fetchUpdateTheme(profile.id, themeOption.value);
            handleClose();

        }
    }, [themeOption?.value]);

    async function fetchUpdateTheme(id, themeData) {
        if (!isLoading) {
            setIsLoading(true);
            try {
                await updateTheme(id, themeData);

                const newProfile = {
                    ...profile,
                    theme: themeData
                }

                setProfile(newProfile);

            } catch (error) {
                showAlert(VariantType.DANGER, error.message);
            } finally {
                setIsLoading(false);
            }
        }
    }

    function handleClose() {
        //document.getElementsByClassName("btn-close")[0]?.click();
        setExpandMenu(false);
    }

    function Singin() {
        return (
            <CustomLink to="/login" >
                <Image className='hover-effect' width={30} height={30} roundedCircle referrerPolicy="no-referrer" src={iNoAccount} />
            </CustomLink>
        )
    }

    function handleDeleteAccount() {
        setExpandMenu(false);
        setShowModal(true);
    }

    function fetchDeleteAccount() {
        console.log("delete account!")
    }

    function UserProfile() {

        return (
            <div>
                <Button variant='link' onClick={() => setExpandMenu(true)} >
                    {profile.pictureUrl && profile.pictureUrl.length > 0 ?
                        <Image className='profile-icon hover-effect' width={40} height={40} roundedCircle referrerPolicy="no-referrer" src={profile.pictureUrl} /> :
                        <div className="gradient-profile-box hover-effect"><span className="gradient-icon-label">{profile.initials}</span></div>
                    }
                </Button>

                <Offcanvas className="slide-menu-box" show={expandMenu} placement="end" onHide={() => setExpandMenu(false)}>
                    <Offcanvas.Header closeButton className='align-items-start pb-0'>
                        <div className='d-flex flex-column justify-content-start align-items-center flex-grow-1'>
                            {profile.pictureUrl && profile.pictureUrl.length > 0 ?
                                <Image className='profile-icon' width={50} height={50} roundedCircle referrerPolicy="no-referrer" src={profile.pictureUrl} /> :
                                <div className='gradient-profile-box'><span className="gradient-icon-label">{profile.initials}</span></div>

                            }
                            <span className='title mt-2'>{profile.name}</span>
                            <span className='small'>{profile.email}</span>
                        </div>
                    </Offcanvas.Header>
                    <Offcanvas.Body className="d-flex flex-column gap-3">
                        <div className='section' />
                        <div className="settings gap-3">
                            <div className='d-flex flex-row'>
                                <BsPalette className="simple-icon p-0 me-2 align-bottom" />
                                <Select name="theme" key={themeOption?.value}
                                    defaultValue={themeOption}
                                    options={themes}
                                    onChange={(e) => setThemeOption(e)} />
                            </div>

                            <CustomLink bsPrefix="btn-profile-menu" to={"/account/edit"} onClick={handleClose}>
                                <BsPersonGear className="simple-icon me-2" />
                                {t("edit-account-link")}
                            </CustomLink>

                            <CustomLink bsPrefix="btn-profile-menu" to="/group-members" onClick={handleClose}>
                                <BsPeople className="simple-icon me-2" />
                                {t("group-members-link")}
                            </CustomLink>

                            <CustomLink bsPrefix="btn-profile-menu" onClick={handleDeleteAccount}>
                                <BsTrash className="simple-icon me-2" />
                                {t("delete-account")}
                            </CustomLink>

                        </div >
                        <div className='mt-auto align-self-center'>
                            <LogoutFromGoogle handleLogout={handleLogout} text={t("btn-logout")} />
                        </div>
                    </Offcanvas.Body>
                </Offcanvas>

                <Modal className='custom-alert' size='md' show={showModal} onHide={() => setShowModal(false)} >
                    <Modal.Body className='custom-alert-body'>
                        <span className='title text-center'>
                            {t('delete-account-alert')}
                        </span>
                    </Modal.Body>
                    <Modal.Footer className='custom-alert-footer p-2'>
                        <Button bsPrefix='btn-custom' size='sm' onClick={() => setShowModal(false)}><span>{t("btn-no", { ns: "common" })}</span></Button>
                        <Button bsPrefix='btn-custom' size='sm' onClick={fetchDeleteAccount}><span>{t("btn-yes", { ns: "common" })}</span></Button>
                    </Modal.Footer>
                </Modal >
            </div>
        )

    }

    return (
        <div>
            {profile && Object.keys(profile).length > 1 ? <UserProfile /> : <Singin />}
        </div>
    )
}
export default AccountMenu;