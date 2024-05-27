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
import OverlayTrigger from 'react-bootstrap/OverlayTrigger';
import Tooltip from 'react-bootstrap/Tooltip';
import AccountMenu from './AccountMenu.js';
import Container from 'react-bootstrap/Container';
import i18n from 'i18next';
import { useTranslation } from 'react-i18next';

import LanguageSelect from './LanguageSelect';

export default function Header() {
    const { t } = useTranslation(['header', 'common']);

    const { profileCtx } = useContext(ProfileContext);
    let hasActiveProfile = (profileCtx && Object.keys(profileCtx).length > 0) ? true : false;

    function setLanguage(code) {
        i18n.changeLanguage(code)
    }

    return (
        <div className="header">
            <Navbar collapseOnSelect expand="md">
                <Navbar.Brand className="homeLink" href="/home" ><span className="homeText">{t("app-name", {ns: "common"})}</span></Navbar.Brand>
                <Container className='container fix-pantry-name'></Container>
                <Container bsPrefix='fix-toggle-btn'>
                    <Navbar.Toggle className='hover-effect'>
                        <Image src={iconMenu} className="menu-icon" />
                    </Navbar.Toggle>
                </Container>

                <Container bsPrefix='fix-login-btn-before'>
                    <div className='d-flex align-items-center gap-2'>
                        <Navbar>
                            <LanguageSelect className="menuItem" language={i18n.language} onChange={setLanguage} />
                        </Navbar>
                        <AccountMenu />
                    </div>
                </Container>

                <Navbar.Collapse >
                    <div className="menu">
                        <OverlayTrigger placement="bottom" delay={{ show: 250, hide: 400 }} overlay={<Tooltip className="custom-tooltip">{t("tooltip-consume")}</Tooltip>}>
                            <Nav.Item><Nav.Link href={"/pantries/consume"} eventKey="link-consume" className="menuItem" disabled={!hasActiveProfile} >
                                <Image src={iconConsume} className="menu-icon " /></Nav.Link>
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
                    </div>
                </Navbar.Collapse>

                <Container bsPrefix='fix-login-btn-after' >
                    <div className='d-flex align-items-center gap-2'>
                        <Navbar>
                            <LanguageSelect language={i18n.language} onChange={setLanguage} />
                        </Navbar>
                        <AccountMenu />
                    </div>
                </Container>
            </Navbar>
        </div>
    )
}