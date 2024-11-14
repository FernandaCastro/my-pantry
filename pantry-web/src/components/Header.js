import React, { useEffect } from 'react';
import Navbar from 'react-bootstrap/Navbar';
import Nav from 'react-bootstrap/Nav';
import Image from 'react-bootstrap/Image';
import iconConsume from '../assets/images/cook-gradient.png';
import iconPurchase from '../assets/images/shoppingcart-gradient.png';
import iconProduct from '../assets/images/food-gradient.png';
import iconPantry from '../assets/images/cupboard-gradient.png';
import iconSupermarket from '../assets/images/supermarket-gradient.png';
import logo from '../assets/images/logo.png';

import OverlayTrigger from 'react-bootstrap/OverlayTrigger';
import Tooltip from 'react-bootstrap/Tooltip';
import AccountMenu from './AccountMenu.js';
import Container from 'react-bootstrap/Container';
import i18n from 'i18next';
import { useTranslation } from 'react-i18next';

import LanguageSelect from './LanguageSelect';
import { Stack } from 'react-bootstrap';
import useProfile from '../hooks/useProfile';
import { Link } from 'react-router-dom';

export default function Header() {
    const { t } = useTranslation(['header', 'common']);

    const { profile, resetProfile } = useProfile();

    function setThema() {

        if (profile && Object.keys(profile).length > 0) {

            if (profile?.theme === null || profile?.theme === "") {
                resetProfile();
            }

            if (document.body.className !== profile?.theme) {
                document.body.className = profile?.theme;
            }
        }
    }

    function setLanguage(code) {
        i18n.changeLanguage(code)
    }

    function MainMenu() {
        setThema();
        return (
            <Navbar className="p-0" >
                <div className="menu">
                    <OverlayTrigger placement="bottom" delay={{ show: 250, hide: 400 }} overlay={<Tooltip className="custom-tooltip">{t("tooltip-consume")}</Tooltip>}>
                        <Nav.Item><Link to={"/pantries/consume"} className="menuItem" disabled={!(profile && Object.keys(profile).length > 1)} >
                            <div className="gradient-icon-box-header"><Image src={iconConsume} className="menu-icon" /></div></Link>
                        </Nav.Item>
                    </OverlayTrigger>
                    <OverlayTrigger placement="bottom" delay={{ show: 250, hide: 400 }} overlay={<Tooltip className="custom-tooltip">{t("tooltip-purchase")}</Tooltip>}>
                        <Nav.Item>
                            <Link to="/purchase" className="menuItem" disabled={!(profile && Object.keys(profile).length > 1)}>
                                <div className="gradient-icon-box-header"><Image src={iconPurchase} className="menu-icon" /></div>
                            </Link>
                        </Nav.Item>
                    </OverlayTrigger>

                    <OverlayTrigger placement="bottom" delay={{ show: 250, hide: 400 }} overlay={<Tooltip className="custom-tooltip">{t("tooltip-pantries")}</Tooltip>}>
                        <Nav.Item><Link to="/pantries"  className="menuItem" disabled={!(profile && Object.keys(profile).length > 1)}>
                            <Image src={iconPantry} className="menu-icon" />
                        </Link>
                        </Nav.Item>
                    </OverlayTrigger>
                    <OverlayTrigger placement="bottom" delay={{ show: 250, hide: 400 }} overlay={<Tooltip className="custom-tooltip">{t("tooltip-products")}</Tooltip>}>
                        <Nav.Item><Link to="/product" className="menuItem" disabled={!(profile && Object.keys(profile).length > 1)}>
                            <Image src={iconProduct} className="menu-icon" />
                        </Link>
                        </Nav.Item>
                    </OverlayTrigger>
                    <OverlayTrigger placement="bottom" delay={{ show: 250, hide: 400 }} overlay={<Tooltip className="custom-tooltip">{t("tooltip-supermarkets")}</Tooltip>}>
                        <Nav.Item><Link to="/supermarkets" className="menuItem" disabled={!(profile && Object.keys(profile).length > 1)}>
                            <Image src={iconSupermarket} className="menu-icon" />
                        </Link>
                        </Nav.Item>
                    </OverlayTrigger>
                </div>
            </Navbar>
        )
    }

    function ProfileMenu() {
        return (
            <div className='d-flex align-items-center fix-login-btn-after ms-5 me-2 gap-2'>
                <Navbar>
                    <LanguageSelect className="menuItem" language={i18n.language} onChange={setLanguage} />
                </Navbar>
                <AccountMenu />
            </div>
        )
    }

    return (
        <Stack className="header" direction='vertical'>
            <div>
                <Navbar className="pt-2 pb-1">
                    <Link className="homeLink pb-0" to="/home" ><Image src={logo} className="logo" /></Link>
                    <Container />

                    {/*hidden on smaller than md*/}
                    <div className='d-none d-md-block'>
                        <MainMenu />
                    </div>

                    <ProfileMenu />
                </Navbar>
            </div>

            {/*hidden on bigger than md*/}
            <div className='d-flex justify-content-around d-block d-md-none'>
                <MainMenu />
            </div>
        </Stack>
    )
}