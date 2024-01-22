import React, { useContext } from 'react';
import { PantryContext } from '../services/context/AppContext';
import Container from 'react-bootstrap/Container';
import Navbar from 'react-bootstrap/Navbar';
import Nav from 'react-bootstrap/Nav';
import '../assets/styles/Header.scss';

export default function Header() {
    const { pantryCtx } = useContext(PantryContext);
    let isPantrySelected = (pantryCtx !== null && pantryCtx.id !== 0 && pantryCtx.isActive) ? true : false;

    return (
        <div className="header">
            <Navbar collapseOnSelect expand="md">
                <Navbar.Brand href="/" >The Pantry</Navbar.Brand>
                <Nav><Nav.Link href={"/pantries/" + pantryCtx.id + "/edit"}>{pantryCtx.name}</Nav.Link></Nav>
                <Navbar.Toggle className="toggle-btn" />
                <Navbar.Collapse >
                    <div className="menu">
                        <Nav.Item className="menuItem"><Nav.Link href={"/pantries/" + pantryCtx.id + "/consume"} eventKey="link-consume" disabled={!isPantrySelected} >Consume</Nav.Link></Nav.Item>
                        <Nav.Item className="menuItem"><Nav.Link href="/purchase" eventKey="link-purchase">Purchase</Nav.Link></Nav.Item>
                        <Nav.Item className="menuItem"><Nav.Link href="/product" eventKey="link-product">Product</Nav.Link></Nav.Item>
                    </div>
                </Navbar.Collapse>
            </Navbar>
        </div>
    )
}