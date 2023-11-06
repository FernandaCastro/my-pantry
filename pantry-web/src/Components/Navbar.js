import './Navbar.css';
import { Link } from "react-router-dom";
import { useContext } from 'react';
import { PantryContext } from './PantryContext.js';

export default function Navbar() {

    const pantry = useContext(PantryContext);
    let isPantrySelected = pantry !== null && pantry.id !== 0 ? true : false;

    return (
        <nav className='teal lighten-2'>
            <div className="nav-wrapper teal lighten-2">
                <Link to="/" className="brand-logo left">My Pantry</Link>
                <ul id="nav-mobile" className="right show-on-med white-text">
                    <li>
                        <Link
                            to="/consume"
                            style={isPantrySelected ? { pointerEvents: "auto" } : { pointerEvents: "none" }}>
                            Consume
                        </Link>
                    </li>
                    <li><Link to="/purchase">Purchase</Link></li>
                </ul>
            </div>
        </nav>
    )
}