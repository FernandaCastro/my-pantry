import React, { useContext } from 'react';
import { PantryContext } from '../../services/context/PantryContext';
import Container from 'react-bootstrap/Container';
import Navbar from 'react-bootstrap/Navbar';
import Nav from 'react-bootstrap/Nav';

export default function Header() {
    const pantry = useContext(PantryContext);
    let isPantrySelected = (pantry !== null && pantry.id != 0 && pantry.isActive) ? true : false;

    return (
        <Navbar bg="primary" data-bs-theme="dark">
            <Container>
                <Navbar.Brand href="/">My Pantry</Navbar.Brand>
                <Nav>
                    <Nav.Link href={"/pantries/" + pantry.id + "/consume"} eventKey="link-consume" disabled={!isPantrySelected} >Consume</Nav.Link>
                    <Nav.Link href="/purchase" eventKey="link-purchase">Purchase</Nav.Link>
                </Nav>
            </Container>
        </Navbar>
    )
}