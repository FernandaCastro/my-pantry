import React, { useContext } from 'react';
import { PantryContext } from '../../services/context/AppContext';
import Container from 'react-bootstrap/Container';
import Navbar from 'react-bootstrap/Navbar';
import Nav from 'react-bootstrap/Nav';

export default function Header() {
    const { pantryCtx } = useContext(PantryContext);
    let isPantrySelected = (pantryCtx !== null && pantryCtx.id !== 0 && pantryCtx.isActive) ? true : false;

    return (
        <Navbar bg="primary" data-bs-theme="dark" collapseOnSelect expand="md">
            < Container className="d-flex justify-content-between">
                <Navbar.Brand href="/">My Pantry</Navbar.Brand>
                <Nav><Nav.Link href={"/pantries/" + pantryCtx.id + "/edit"}>Pantry: {pantryCtx.name}</Nav.Link></Nav>
                <Navbar.Toggle />
                <Navbar.Collapse className="justify-content-end">
                    <Nav>
                        <Nav.Item><Nav.Link href={"/pantries/" + pantryCtx.id + "/consume"} eventKey="link-consume" disabled={!isPantrySelected} >Consume</Nav.Link></Nav.Item>
                        <Nav.Item><Nav.Link href="/purchase" eventKey="link-purchase">Purchase</Nav.Link></Nav.Item>
                        <Nav.Item><Nav.Link href="/product" eventKey="link-product">Product</Nav.Link></Nav.Item>
                    </Nav>
                </Navbar.Collapse>
            </Container >
        </Navbar >
    )
}