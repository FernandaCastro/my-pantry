import React, { useContext } from 'react';
import { PantryContext } from '../services/context/AppContext';
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
    const { pantryCtx } = useContext(PantryContext);
    let isPantrySelected = (pantryCtx !== null && pantryCtx.id !== 0 && pantryCtx.isActive) ? true : false;

    return (
        <div className="header">
            <Navbar collapseOnSelect expand="md">
                <Navbar.Brand className="homeLink" href="/home" ><span className="homeText">My Pantry</span></Navbar.Brand>
                <Container className='container fix-pantry-name'></Container>
                <Container bsPrefix='fix-toggle-btn'>
                    {/* className="toggle-btn" */}
                    <Navbar.Toggle >
                        <Image src={iconMenu} className="menu-icon" />
                    </Navbar.Toggle>
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
            <div className="d-flex justify-content-center align-items-center p-0">
                <Nav><Nav.Link className="link-to-pantry" href={"/pantries/" + pantryCtx.id + "/edit"}>{pantryCtx.name}</Nav.Link></Nav>
            </div>
        </div>
    )
}