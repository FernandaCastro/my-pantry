import React, { useContext } from 'react';
import { ProfileContext } from '../services/context/AppContext';
import Navbar from 'react-bootstrap/Navbar';
import Nav from 'react-bootstrap/Nav';
import '../assets/styles/Header.scss';
import Image from 'react-bootstrap/Image';
import iconConsume from '../assets/images/cook-gradient.png';
import iconPurchase from '../assets/images/shoppingcart-gradient.png';
import iconProduct from '../assets/images/food-gradient.png';
import iconPantry from '../assets/images/cupboard-gradient.png';
import iconMenu from '../assets/images/menu-gradient.png';
import iconSupermarket from '../assets/images/supermarket-gradient.png';

import OverlayTrigger from 'react-bootstrap/OverlayTrigger';
import Tooltip from 'react-bootstrap/Tooltip';
import AccountMenu from './AccountMenu.js';
import Container from 'react-bootstrap/Container';
import i18n from 'i18next';
import { useTranslation } from 'react-i18next';

import LanguageSelect from './LanguageSelect';
import { Stack } from 'react-bootstrap';

export default function Header() {
    const { t } = useTranslation(['header', 'common']);

    const { profileCtx } = useContext(ProfileContext);
    let hasActiveProfile = (profileCtx && Object.keys(profileCtx).length > 0) ? true : false;

    function setLanguage(code) {
        i18n.changeLanguage(code)
    }

    function renderMainMenu() {
        return (
            <Navbar>
                <div className="menu">
                    <OverlayTrigger placement="bottom" delay={{ show: 250, hide: 400 }} overlay={<Tooltip className="custom-tooltip">{t("tooltip-consume")}</Tooltip>}>
                        <Nav.Item><Nav.Link href={"/pantries/consume"} eventKey="link-consume" className="menuItem" disabled={!hasActiveProfile} >
                            <Image src={iconConsume} className="menu-icon" /></Nav.Link>
                        </Nav.Item>
                    </OverlayTrigger>
                    <OverlayTrigger placement="bottom" delay={{ show: 250, hide: 400 }} overlay={<Tooltip className="custom-tooltip">{t("tooltip-purchase")}</Tooltip>}>
                        <Nav.Item>
                            <Nav.Link href="/purchase" eventKey="link-purchases" className="menuItem" disabled={!hasActiveProfile}>
                                <Image src={iconPurchase} className="menu-icon" />
                            </Nav.Link>
                        </Nav.Item>
                    </OverlayTrigger>
                    <OverlayTrigger placement="bottom" delay={{ show: 250, hide: 400 }} overlay={<Tooltip className="custom-tooltip">{t("tooltip-pantries")}</Tooltip>}>
                        <Nav.Item><Nav.Link href="/pantries" eventKey="link-pantries" className="menuItem" disabled={!hasActiveProfile}>
                            <Image src={iconPantry} className="menu-icon" />
                        </Nav.Link>
                        </Nav.Item>
                    </OverlayTrigger>
                    <OverlayTrigger placement="bottom" delay={{ show: 250, hide: 400 }} overlay={<Tooltip className="custom-tooltip">{t("tooltip-products")}</Tooltip>}>
                        <Nav.Item><Nav.Link href="/product" eventKey="link-products" className="menuItem" disabled={!hasActiveProfile}>
                            <Image src={iconProduct} className="menu-icon" />
                        </Nav.Link>
                        </Nav.Item>
                    </OverlayTrigger>
                    <OverlayTrigger placement="bottom" delay={{ show: 250, hide: 400 }} overlay={<Tooltip className="custom-tooltip">{t("tooltip-supermarkets")}</Tooltip>}>
                        <Nav.Item><Nav.Link href="/supermarkets" eventKey="link-supermarkets" className="menuItem" disabled={!hasActiveProfile}>
                            <Image src={iconSupermarket} className="menu-icon" />
                        </Nav.Link>
                        </Nav.Item>
                    </OverlayTrigger>
                </div>
            </Navbar>
        )
    }

    function renderProfileMenu() {
        return (
            <div className='d-flex align-items-center fix-login-btn-after ms-5 gap-2'>
                <Navbar>
                    <LanguageSelect className="menuItem" language={i18n.language} onChange={setLanguage} />
                </Navbar>
                <AccountMenu/>
            </div>
        )
    }

    return (
        <Stack className="header" direction='vertical'>
            <div>
                <Navbar >
                    <Navbar.Brand className="homeLink" href="/home" ><span className="homeText">{t("app-name", { ns: "common" })}</span></Navbar.Brand>
                    <Container className='container fix-pantry-name'></Container>

                    {/*hidden on smaller than md*/}
                    <div className='d-none d-md-block'>
                        {renderMainMenu()}
                    </div>

                    {renderProfileMenu()}
                </Navbar>
            </div>

            {/*hidden on bigger than md*/}
            <div className='d-flex justify-content-around d-block d-md-none'>

                {renderMainMenu()}
            </div>
        </Stack>
    )
}