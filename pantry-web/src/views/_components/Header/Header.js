import { Link } from "react-router-dom";
import React, { useContext } from 'react';
import { PantryContext } from '../../../components/PantryContext';
import '../../../styles/header.css';


export default function Header() {
    const pantry = useContext(PantryContext);
    let isPantrySelected = pantry !== null && pantry.id !== 0 ? true : false;

    return (
        <div className="row">
            <nav className='teal lighten-2' >
                <div className="nav-wrapper teal lighten-2" >
                    <Link to="/" className="brand-logo left" >My Pantry</Link>
                    < ul id="nav-mobile" className="right show-on-med white-text" >
                        <li><Link to="/consume" style={isPantrySelected ? { pointerEvents: "auto" } : { pointerEvents: "none" }}> <h5>Consume</h5> </Link></li >
                        <li><Link to="/purchase" > <h5>Purchase </h5></Link></li >
                    </ul>
                </div>
            </nav>
        </div>
    )
}