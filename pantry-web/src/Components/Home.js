import { useState, useEffect, useContext } from 'react';
import { PantryContext, SetPantryContext } from './PantryContext.js';
import Navbar from './Navbar.js';

export default function Home() {

    const [pantries, setPantries] = useState([])
    const pantry = useContext(PantryContext);
    const setPantry = useContext(SetPantryContext);
    const URL_GET_PANTRIES = 'http://localhost:8080/pantries';

    useEffect(() => {
        fetch(URL_GET_PANTRIES)
            .then((response) => response.json())
            .then((data) => { setPantries(data) })
            .catch((error) => { console.log(error) })
    }, []);

    function renderItem(item) {
        return (
            <li className="collection-item black-text" key={item.id}>
                <label>
                    <input
                        type="radio"
                        className="with-gap secondary-content black-text"
                        name="group1"
                        value={item.id}
                        onChange={(e) => handleRadioOnChange(e.target.value)}
                        defaultChecked={setDefaultRadioChecked(item)}
                        disabled={!item.isActive}
                    />
                    <span>{item.name}</span>
                </label>
            </li>
        )
    }

    function renderItems() {
        return pantries.map((item) => (renderItem(item)));
    }

    function handleRadioOnChange(itemId) {
        pantries.forEach((item) => {
            if (item.id === parseInt(itemId)) {
                return setPantry(item);
            }
        })
    }

    function setDefaultRadioChecked(item) {
        if (pantries !== null && pantries.length === 1) return true;
        if (pantry != null && pantry.id !== 0 && pantry.id === item.id) return true;
        return false;
    }

    return (
        <div>
            <Navbar />
            <div className='row'>
                <div className="col s12">
                    <ul className="collection with-header">
                        <li className="collection-header teal-text text-lighten-2"><h5>Pantries</h5></li>
                        {renderItems()}
                    </ul>
                    <p>{pantry.name}</p>
                </div>
            </div>
        </div>
    )
}