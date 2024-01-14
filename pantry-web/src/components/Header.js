import React, { useContext } from 'react';
import { PantryContext } from '../services/context/AppContext';
import Navbar from 'react-bootstrap/Navbar';
import Nav from 'react-bootstrap/Nav';
import '../assets/styles/Header.scss';
import Image from 'react-bootstrap/Image';
import iconConsume from '../assets/images/cook-lavender.png';
import iconPurchase from '../assets/images/shopping-cart-lavender.png';
import iconProduct from '../assets/images/food-lavender.png';
import OverlayTrigger from 'react-bootstrap/OverlayTrigger';
import Tooltip from 'react-bootstrap/Tooltip';
import LoginButton from './LoginButton.js';
import Container from 'react-bootstrap/Container';

export default function Header() {
    const { pantryCtx } = useContext(PantryContext);
    let isPantrySelected = (pantryCtx !== null && pantryCtx.id !== 0 && pantryCtx.isActive) ? true : false;

    return (
        <div className="header">
            <Navbar collapseOnSelect expand="md">
                <Navbar.Brand href="/home" >The Pantry</Navbar.Brand>
                <Container className='container fix-pantry-name'>
                    <Nav><Nav.Link href={"/pantries/" + pantryCtx.id + "/edit"}>{pantryCtx.name}</Nav.Link></Nav>
                </Container>
                <Container bsPrefix='fix-toggle-btn'>
                    <Navbar.Toggle className="toggle-btn" />
                </Container>
                <Container bsPrefix='fix-login-btn-before'>
                    <LoginButton />
                </Container>
                <Navbar.Collapse>
                    <div className="menu">
                        <OverlayTrigger placement="top" delay={{ show: 250, hide: 400 }} overlay={<Tooltip id="button-tooltip">Consume</Tooltip>}>
                            <Nav.Item className="menuItem"><Nav.Link href={"/pantries/" + pantryCtx.id + "/consume"} eventKey="link-consume" disabled={!isPantrySelected} >
                                <Image src={iconConsume} className="icon" /></Nav.Link>
                            </Nav.Item>
                        </OverlayTrigger>
                        <Nav.Item className="menuItem">
                            <Nav.Link href="/purchase" eventKey="link-purchase">
                                <Image src={iconPurchase} className="icon" />
                            </Nav.Link>
                        </Nav.Item>
                        <Nav.Item className="menuItem"><Nav.Link href="/product" eventKey="link-product">
                            <Image src={iconProduct} className="icon" />
                        </Nav.Link>
                        </Nav.Item>
                    </div>
                </Navbar.Collapse>
                <Container bsPrefix='fix-login-btn-after'>
                    <LoginButton />
                </Container>
            </Navbar>

        </div>
    )
}