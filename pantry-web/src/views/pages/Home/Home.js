import { Link } from "react-router-dom";
import { useState, useEffect, useContext } from 'react';
import { PantryContext, SetPantryContext } from '../../../components/PantryContext.js';
import { getPantryList } from '../../../services/apis/mypantry/fetch/requests/PantryRequests.ts';

export default function Home() {

    const [pantries, setPantries] = useState([])
    const [isLoading, setIsLoading] = useState(true)

    const pantry = useContext(PantryContext);
    const setPantry = useContext(SetPantryContext);

    async function fetchPantries() {
        const res = await getPantryList();
        setPantries(res)
        setIsLoading(false)
    }

    useEffect(() => {
        fetchPantries()
        setIsLoading(true)
    }, [])

    function renderItem(item) {
        return (
            <li className="collection-item" key={item.id}>
                <label>
                    <input
                        type="radio"
                        className="with-gap"
                        name="group1"
                        value={item.id}
                        onChange={(e) => handleRadioOnChange(e.target.value)}
                        defaultChecked={setDefaultRadioChecked(item)}
                        disabled={!item.isActive}
                    />
                    <span className={item.isActive ? 'blue-grey-text text-darken-3' : 'gray-text'}>
                        {item.name}
                    </span>
                </label>
                <Link to={"/pantries/" + item.id} className="right">
                    <i className="material-icons" >edit</i>
                </Link>
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
        <div className='section'>
            <ul className="collection with-header">
                <li className="collection-header teal-text text-lighten-2"><h6>Select a Pantry: {pantry.name}</h6></li>
                {renderItems()}
            </ul>
        </div>

    )
}