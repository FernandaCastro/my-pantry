import React, { useContext } from 'react';
import { PantryContext } from '../services/context/AppContext';
import Navbar from 'react-bootstrap/Navbar';
import Nav from 'react-bootstrap/Nav';
import '../assets/styles/Header.scss';
import Image from 'react-bootstrap/Image';
import iconConsume from '../assets/images/cook-lavender.png';
import iconPurchase from '../assets/images/shopping-cart-lavender.png';
import iconProduct from '../assets/images/food-lavender.png';
import iconPantry from '../assets/images/cupboard-lavender.png';
import OverlayTrigger from 'react-bootstrap/OverlayTrigger';
import Tooltip from 'react-bootstrap/Tooltip';
import ProfileIcon from './ProfileIcon.js';
import Container from 'react-bootstrap/Container';

export default function Header() {
    const { pantryCtx } = useContext(PantryContext);
    let isPantrySelected = (pantryCtx !== null && pantryCtx.id !== 0 && pantryCtx.isActive) ? true : false;

    return (
        <div className="header">
            <Navbar collapseOnSelect expand="md">
                <Navbar.Brand className="home" href="/home" >The Pantry</Navbar.Brand>
                <Container className='container fix-pantry-name'>
                    <Nav><Nav.Link href={"/pantries/" + pantryCtx.id + "/edit"}>{pantryCtx.name}</Nav.Link></Nav>
                </Container>
                <Container bsPrefix='fix-toggle-btn'>
                    <Navbar.Toggle className="toggle-btn" />
                </Container>
                <Container bsPrefix='fix-login-btn-before'>
                    <ProfileIcon />
                </Container>
                <Navbar.Collapse>
                    <div className="menu">
                        <OverlayTrigger placement="auto" delay={{ show: 250, hide: 400 }} overlay={<Tooltip id="button-tooltip">Consume from Pantry</Tooltip>}>
                            <Nav.Item><Nav.Link href={"/pantries/" + pantryCtx.id + "/consume"} eventKey="link-consume" className="menuItem" disabled={!isPantrySelected} >
                                <Image src={iconConsume} className="menu-icon" /></Nav.Link>
                            </Nav.Item>
                        </OverlayTrigger>
                        <OverlayTrigger placement="auto" delay={{ show: 250, hide: 400 }} overlay={<Tooltip id="button-tooltip">Shopping List</Tooltip>}>
                            <Nav.Item>
                                <Nav.Link href="/purchase" eventKey="link-purchases" className="menuItem">
                                    <Image src={iconPurchase} className="menu-icon" />
                                </Nav.Link>
                            </Nav.Item>
                        </OverlayTrigger>
                        <OverlayTrigger placement="auto" delay={{ show: 250, hide: 400 }} overlay={<Tooltip id="button-tooltip">Pantries</Tooltip>}>
                            <Nav.Item><Nav.Link href="/pantries" eventKey="link-pantries" className="menuItem">
                                <Image src={iconPantry} className="menu-icon" />
                            </Nav.Link>
                            </Nav.Item>
                        </OverlayTrigger>
                        <OverlayTrigger placement="auto" delay={{ show: 250, hide: 400 }} overlay={<Tooltip id="button-tooltip">Products</Tooltip>}>
                            <Nav.Item><Nav.Link href="/product" eventKey="link-products" className="menuItem">
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

        </div>
    )
}