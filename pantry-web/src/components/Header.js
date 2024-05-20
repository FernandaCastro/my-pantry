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
import ProfileIcon from './ProfileIcon.js';
import Container from 'react-bootstrap/Container';

export default function Header() {
    const { profileCtx } = useContext(ProfileContext);
    let hasActiveProfile = (profileCtx && Object.keys(profileCtx).length > 0) ? true : false;

    return (
        <div className="header">
            <Navbar collapseOnSelect expand="md">
                <Navbar.Brand className="homeLink" href="/home" ><span className="homeText">My Pantry</span></Navbar.Brand>
                <Container className='container fix-pantry-name'></Container>
                <Container bsPrefix='fix-toggle-btn'>
                    <Navbar.Toggle className='hover-effect'>
                        <Image src={iconMenu} className="menu-icon" />
                    </Navbar.Toggle>
                </Container>

                <Container bsPrefix='fix-login-btn-before'>
                    <ProfileIcon />
                </Container>

                <Navbar.Collapse>
                    <div className="menu">
                        <OverlayTrigger placement="bottom" delay={{ show: 250, hide: 400 }} overlay={<Tooltip className="custom-tooltip">Consume Items</Tooltip>}>
                            <Nav.Item><Nav.Link href={"/pantries/consume"} eventKey="link-consume" className="menuItem" disabled={!hasActiveProfile} >
                                <Image src={iconConsume} className="menu-icon" /></Nav.Link>
                            </Nav.Item>
                        </OverlayTrigger>
                        <OverlayTrigger placement="bottom" delay={{ show: 250, hide: 400 }} overlay={<Tooltip className="custom-tooltip">Shopping List</Tooltip>}>
                            <Nav.Item>
                                <Nav.Link href="/purchase" eventKey="link-purchases" className="menuItem" disabled={!hasActiveProfile}>
                                    <Image src={iconPurchase} className="menu-icon" />
                                </Nav.Link>
                            </Nav.Item>
                        </OverlayTrigger>
                        <OverlayTrigger placement="bottom" delay={{ show: 250, hide: 400 }} overlay={<Tooltip className="custom-tooltip">Pantries</Tooltip>}>
                            <Nav.Item><Nav.Link href="/pantries" eventKey="link-pantries" className="menuItem" disabled={!hasActiveProfile}>
                                <Image src={iconPantry} className="menu-icon" />
                            </Nav.Link>
                            </Nav.Item>
                        </OverlayTrigger>
                        <OverlayTrigger placement="bottom" delay={{ show: 250, hide: 400 }} overlay={<Tooltip className="custom-tooltip">Products</Tooltip>}>
                            <Nav.Item><Nav.Link href="/product" eventKey="link-products" className="menuItem" disabled={!hasActiveProfile}>
                                <Image src={iconProduct} className="menu-icon" />
                            </Nav.Link>
                            </Nav.Item>
                        </OverlayTrigger>
                    </div>
                </Navbar.Collapse>

                <Container bsPrefix='fix-login-btn-after'>
                    <ProfileIcon />
                </Container>
            </Navbar>
            <div className="d-flex justify-content-center align-items-center p-0">

            </div>
        </div>
    )
}