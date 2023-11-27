import React, { useEffect, useState } from 'react';
import M from 'materialize-css';
import { useParams } from 'react-router';
import { getPantry } from '../../../services/apis/mypantry/fetch/requests/PantryRequests.js';

export default function Pantry() {

    let { id } = useParams();
    let pantry = {};

    const [name, setName] = useState("");
    const [type, setType] = useState("");
    const [isActive, setIsActive] = useState(false);
    const [isLoading, setIsLoading] = useState(true);

    useEffect(() => {
        fetchPantry();
        setIsLoading(true);

        M.AutoInit();
        document.addEventListener('DOMContentLoaded', function () {
            var elems = document.querySelectorAll('typeSelect');
            var instances = M.FormSelect.init(elems, {});
        });
    }, [])

    async function fetchPantry() {
        pantry = await getPantry(id);
        loadFormData();
        setIsLoading(false);
    }

    function loadFormData() {
        setName(pantry.name);
        setType(pantry.type);
        setIsActive(pantry.isActive);
    }

    function handleSave() {
        let body = {
            id: pantry.id,
            name: name,
            type: type,
            isActive: isActive
        }
        return console.log(
            " id: " + body.id +
            " name: " + body.name +
            " type: " + body.type +
            " isActive: " + body.isActive
        );
    }

    function handleClear() {
        setName(pantry.name);
        setType(pantry.type);
        setIsActive(pantry.isActive);
        return console.log("Changes cleared!");
    }

    function renderPantryForm() {
        if (isLoading) return "Loading";
        return (
            <div className="row">
                <form className="col s12">
                    <div className="row">
                        <div className="input-field col s6">
                            <input type="text" className="validate" disabled defaultValue={id} />
                            <label className="active" htmlFor="id">Id</label>
                        </div>
                        <div className="input-field col s6">
                            <input type="text" className="validate" value={name} onChange={(e) => setName(e.target.value)} />
                            <label className="active" htmlFor="name">Name</label>
                        </div>
                    </div>
                    <div className="row">
                        <div className="input-field col s6">
                            <select id="typeSelect"
                                selectedindex={type}
                                onChange={(e) => (setType(e.target.value))}>
                                <option value='' disabled >Select a type</option>
                                <option value='R'>Recurring</option>
                                <option value='S'>Single</option>
                            </select>
                            <label className="active" htmlFor="typeSelect">Type</label>
                        </div>
                        <div className="input-field col s6">
                            <label>
                                <input type="checkbox" checked={isActive} onChange={(e) => setIsActive(e.target.checked)} />
                                <span>{isActive ? "Active" : "Inactive"}</span>
                            </label>
                        </div>
                    </div>
                </form>
            </div>
        )
    }

    return (
        <>
            {renderPantryForm()}
            <div className='section'>
                <a href="#!" className="right waves-effect waves-light btn-small"
                    onClick={handleSave}>
                    <i className="material-icons left">done_all</i>
                    Save
                </a>
                <a href="#!" className="right waves-effect waves-light btn-small"
                    onClick={handleClear}>
                    <i className="material-icons left">clear</i>
                    Clear
                </a>
            </div>
        </>
    );
}